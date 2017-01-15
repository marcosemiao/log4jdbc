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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QuerySQLNamed extends QuerySQL {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final Map params = new HashMap();

	QuerySQLNamed(final RdbmsSpecifics rdbms, final String jdbcQuery) {
		super(rdbms, jdbcQuery);
	}

	public Object putParams(final Object key, final Object value) {
		if (key instanceof String) {
			final String keyString = (String) key;
			params.put(keyString, value);
		}
		return super.putParams(key, value);
	}

	protected String addQueryParameters(final String sql) {
		String formatQuery = super.addQueryParameters(sql);

		final Set entrySet = params.entrySet();

		final Iterator iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			final Map.Entry entry = (Map.Entry) iterator.next();

			final String key = (String) entry.getKey();
			final Object value = entry.getValue();
			formatQuery = replaceAll(formatQuery, key, value.toString());
		}

		return formatQuery;
	}

	public String toString() {
		return "QuerySQLNamed [sql=" + getSQLQuery() + "]";
	}

	private static String replaceAll(final String str, final String replace, final String replacement) {
		final StringMaker sb = stringFactory.newString();
		int firstOccurrence = sb.toString().indexOf(replace);

		while (firstOccurrence != -1) {
			sb.replace(firstOccurrence, firstOccurrence + replace.length(), replacement);
			final int position = firstOccurrence + replacement.length();
			firstOccurrence = sb.toString().indexOf(replace, position);
		}

		return sb.toString();
	}
}
