/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.log4jdbc.sql.internal;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.rdbms.DataRdbms;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.util.CollectionsUtil;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QuerySQL {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final RdbmsSpecifics rdbms;

	private final String jdbcQuery;

	private final Map params = CollectionsUtil.synchronizedMap(new HashMap());

	private Reference sqlQuery;

	private String typeQuery;

	QuerySQL(final RdbmsSpecifics rdbms, final String jdbcQuery) {
		this.rdbms = rdbms;
		this.jdbcQuery = jdbcQuery.intern();
	}

	public Object putParams(final Object key, final Object value) {
		sqlQuery = null;
		return params.put(key, value);
	}

	public String getJDBCQuery() {
		return jdbcQuery;
	}

	public Map getJDBCParameters() {
		return params;
	}

	public String getTypeQuery() {
		if (typeQuery == null) {
			typeQuery = rdbms.getTypeQuery(getSQLQuery());
		}
		return typeQuery;
	}

	public String getSQLQuery() {
		String sql = null;

		Reference ref = sqlQuery;
		if (ref != null) {
			sql = (String) ref.get();
		}

		if (sql == null) {
			sql = addQueryParameters(jdbcQuery);
			sqlQuery = new WeakReference(sql);
		}

		return sql;
	}

	protected String addQueryParameters(String sql) {
		sql = sql.trim();

		if (params == null || params.isEmpty()) {
			return sql;
		}

		final StringMaker query = stringFactory.newString();
		int index = 1;
		int lastPos = 0;

		int positionParam = rdbms.beginQuery(sql, lastPos);
		if (positionParam == -1) {
			positionParam = lastPos;
		}
		int position = sql.indexOf('?', positionParam);

		while (position != -1) {
			String partQuery = sql.substring(lastPos, position);
			query.append(partQuery);

			final Integer indexCast = new Integer(index);
			final Object param = params.get(indexCast);
			final DataRdbms data = rdbms.getData(param);
			final String paramFormat = data.getParameter();
			query.append(paramFormat);

			lastPos = position + 1;
			positionParam = rdbms.beginQuery(sql, lastPos);
			if (positionParam == -1) {
				positionParam = lastPos;
			}
			position = sql.indexOf('?', positionParam);

			index++;
		}

		if (lastPos < sql.length()) {
			query.append(sql.substring(lastPos, sql.length()));
		}

		return query.toString();
	}

	public String toString() {
		return "QuerySQL [sql=" + getSQLQuery() + "]";
	}
}
