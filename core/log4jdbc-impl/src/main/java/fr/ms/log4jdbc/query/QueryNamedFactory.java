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
package fr.ms.log4jdbc.query;

import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QueryNamedFactory implements QueryFactory {

    private final static QueryFactory instance = new QueryNamedFactory();

    private QueryNamedFactory() {
    }

    public final static QueryFactory getInstance() {
	return instance;
    }

    public QueryImpl newQuery(final ConnectionContext connectionContext, final String jdbcQuery) {
	if (jdbcQuery == null) {
	    final QueryImpl wrapper = new QueryImpl(null);
	    return wrapper;
	}
	final RdbmsSpecifics rdbms = connectionContext.getRdbmsSpecifics();
	final QuerySQLNamed query = new QuerySQLNamed(rdbms, jdbcQuery);
	final QueryImpl wrapper = new QueryImpl(query);
	return wrapper;
    }
}
