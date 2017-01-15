/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.ms.log4jdbc.formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;

/*
 * Lots of this could be abstracted out into a word-wrapping class.
 */

/**
 * Converts single-line SQL strings into nicely-formatted multi-line, indented
 * statements. Example: <code> select * from PERSON t0,
 * COMPANY t1 WHERE t0.ID = 10 AND \ t0.COMPANY_ID = t1.ID AND t1.NAME = 'OpenJPA'</code>
 * becomes <code>SELECT * FROM PERSON t0, COMPANY t1
 * WHERE t0.ID = 10 AND t0.COMPANY_ID = t1.ID AND t1.NAME = 'OpenJPA'\
 * </code> and
 * <code>INSERT INTO PERSON VALUES('Patrick', 'Linskey', 'OpenJPA', \
 * '202 595 2064 x1111')</code> becomes
 * <code>INSERT INTO PERSON VALUES('Patrick', 'Linskey', 'OpenJPA', '202
 * 595 2064 x1111')</code> etc.
 *
 * @author Patrick Linskey
 * @author Marco Semiao
 */
public class DefaultSQLFormatter {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private boolean multiLine = true;
	private boolean doubleSpace = false;
	private String newline = System.getProperty("line.separator");
	private int lineLength = 72;
	private String wrapIndent = "        ";
	private String clauseIndent = "    ";

	private static final String[] selectSeparators = new String[] { "FROM ", "WHERE ", "ORDER BY ", // ###
			// is
			// this
			// order
			// correct?
			"GROUP BY ", "HAVING ", };

	private static final String[] insertSeparators = new String[] { "VALUES ", };

	private static final String[] updateSeparators = new String[] { "SET ", "WHERE ", };

	private static final String[] deleteSeparators = new String[] { "WHERE ", };

	private static final String[] createTableSeparators = new String[] { "( ", };

	private static final String[] createIndexSeparators = new String[] { "ON ", "( ", };

	public void setNewline(final String val) {
		newline = val;
	}

	public String getNewline() {
		return newline;
	}

	public void setLineLength(final int val) {
		lineLength = val;
	}

	public int getLineLength() {
		return lineLength;
	}

	public void setWrapIndent(final String val) {
		wrapIndent = val;
	}

	public String getWrapIndent() {
		return wrapIndent;
	}

	public void setClauseIndent(final String val) {
		clauseIndent = val;
	}

	public String getClauseIndent() {
		return clauseIndent;
	}

	/**
	 * If true, then try to parse multi-line SQL statements.
	 *
	 * @param multiLine
	 *            multiLine
	 */
	public void setMultiLine(final boolean multiLine) {
		this.multiLine = multiLine;
	}

	/**
	 * If true, then try to parse multi-line SQL statements.
	 *
	 * @return multiLine
	 */
	public boolean getMultiLine() {
		return this.multiLine;
	}

	/**
	 * If true, then output two lines after multi-line statements.
	 *
	 * @param doubleSpace
	 *            doubleSpace
	 */
	public void setDoubleSpace(final boolean doubleSpace) {
		this.doubleSpace = doubleSpace;
	}

	/**
	 * If true, then output two lines after multi-line statements.
	 *
	 * @return doubleSpace
	 */
	public boolean getDoubleSpace() {
		return this.doubleSpace;
	}

	public String prettyPrint(final String sqlObject) {
		if (!multiLine) {
			return prettyPrintLine(sqlObject);
		} else {
			final StringMaker sql = stringFactory.newString(sqlObject);
			final StringMaker buf = stringFactory.newString(sql.length());

			while (sql.length() > 0) {
				String line = null;

				final int index = Math.max(sql.toString().indexOf(";\n"), sql.toString().indexOf(";\r"));
				if (index == -1) {
					line = sql.toString();
				} else {
					line = sql.substring(0, index + 2);
				}

				// remove the current line from the sql buffer
				sql.delete(0, line.length());

				buf.append(prettyPrintLine(line));
				for (int i = 0; i < 1 + (getDoubleSpace() ? 1 : 0); i++) {
					buf.append(System.getProperty("line.separator"));
				}
			}

			return buf.toString().trim();
		}
	}

	private String prettyPrintLine(final String sqlObject) {
		final String sql = sqlObject.trim();
		final String lowerCaseSql = sql.toLowerCase();

		String[] separators;
		if (lowerCaseSql.startsWith("select")) {
			separators = selectSeparators;
		} else if (lowerCaseSql.startsWith("insert")) {
			separators = insertSeparators;
		} else if (lowerCaseSql.startsWith("update")) {
			separators = updateSeparators;
		} else if (lowerCaseSql.startsWith("delete")) {
			separators = deleteSeparators;
		} else if (lowerCaseSql.startsWith("create table")) {
			separators = createTableSeparators;
		} else if (lowerCaseSql.startsWith("create index")) {
			separators = createIndexSeparators;
		} else {
			separators = new String[0];
		}

		int start = 0;
		int end = -1;
		StringMaker clause;
		final List clauses = new ArrayList();
		clauses.add(stringFactory.newString());

		for (int i = 0; i < separators.length; i++) {
			final String separator = separators[i];

			end = lowerCaseSql.indexOf(" " + separator.toLowerCase(), start);
			if (end == -1) {
				break;
			}

			clause = (StringMaker) clauses.get(clauses.size() - 1);
			clause.append(sql.substring(start, end));

			clause = stringFactory.newString();
			clauses.add(clause);
			clause.append(clauseIndent);
			clause.append(separator);

			start = end + 1 + separator.length();
		}

		clause = (StringMaker) clauses.get(clauses.size() - 1);
		clause.append(sql.substring(start));

		final StringMaker pp = stringFactory.newString(sql.length());
		for (final Iterator iter = clauses.iterator(); iter.hasNext();) {
			pp.append(wrapLine(iter.next().toString()));
			if (iter.hasNext()) {
				pp.append(newline);
			}
		}

		return pp.toString();
	}

	private String wrapLine(final String line) {
		final StringMaker lines = stringFactory.newString(line.length());

		// ensure that any leading whitespace is preserved.
		for (int i = 0; i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t'); i++) {
			lines.append(line.charAt(i));
		}

		final StringTokenizer tok = new StringTokenizer(line);
		int length = 0;
		String elem;
		while (tok.hasMoreTokens()) {
			elem = tok.nextToken();
			length += elem.length();

			// if we would have exceeded the max, write out a newline
			// before writing the elem.
			if (length >= lineLength) {
				lines.append(newline);
				lines.append(wrapIndent);
				lines.append(elem);
				lines.append(' ');
				length = wrapIndent.length() + elem.length() + 1;
				continue;
			}

			// if the current length is greater than the max, then the
			// last word alone was too long, so just write out a
			// newline and move on.
			if (elem.length() >= lineLength) {
				lines.append(elem);
				if (tok.hasMoreTokens()) {
					lines.append(newline);
				}
				lines.append(wrapIndent);
				length = wrapIndent.length();
				continue;
			}

			lines.append(elem);
			lines.append(' ');
			length++;
		}

		return lines.toString();
	}
}