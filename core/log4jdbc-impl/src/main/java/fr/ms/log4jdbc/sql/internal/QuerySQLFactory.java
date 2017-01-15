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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QuerySQLFactory implements QueryFactory {

	private final static QueryFactory instance = new QuerySQLFactory();

	private QuerySQLFactory() {
	}

	public final static QueryFactory getInstance() {
		return instance;
	}

	public QueryImpl newQuery(final ConnectionContextJDBC connectionContext, final String jdbcQuery) {
		QuerySQL query = null;

		if (jdbcQuery != null) {
			final RdbmsSpecifics rdbms = connectionContext.getRdbmsSpecifics();
			query = new QuerySQL(rdbms, jdbcQuery);
		}

		final QueryImpl wrapper = new QueryImpl(query);
		wrapper.setState(Query.STATE_NOT_EXECUTE);

		return wrapper;
	}

	public QueryImpl newQuery(final ConnectionContextJDBC connectionContext, final String jdbcQuery,
			final Map jdbcParameters) {
		final QueryImpl query = newQuery(connectionContext, jdbcQuery);

		final Iterator entries = jdbcParameters.entrySet().iterator();
		while (entries.hasNext()) {
			final Entry thisEntry = (Entry) entries.next();
			final Object key = thisEntry.getKey();
			final Object value = thisEntry.getValue();

			query.putParams(key, value);
		}

		return query;
	}
}
