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
package fr.ms.log4jdbc.operator.impl;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class PreparedStatementDecorator extends StatementDecorator {

    private QueryImpl newQuery;

    public PreparedStatementDecorator(final PreparedStatement preparedStatement, final ConnectionContext connectionContext, final String sql,
	    final QueryFactory querySQLFactory) {
	super(preparedStatement, querySQLFactory);

	query = querySQLFactory.newQuery(connectionContext, sql);
    }

    public SqlOperationImpl transformMessage(final Object proxy, final Method method, final Object[] args, final SqlOperationContext mic,
	    final SqlOperationImpl message) {

	final TimeInvocation timeInvocation = mic.getInvokeTime();
	final ConnectionContext connectionContext = mic.getConnectionContext();

	final String nameMethod = method.getName();

	final boolean addBatchMethod = nameMethod.equals("addBatch") && args == null;
	if (addBatchMethod) {
	    query.setMethodQuery(Query.METHOD_BATCH);
	    query.setTimeInvocation(timeInvocation);

	    connectionContext.addQuery(query, true);

	    query.execute();
	    message.setQuery(query);
	    mic.setQuery(query);

	    // Creation de la prochaine requete
	    newQuery = createWrapperQuery(querySQLFactory, connectionContext, query);

	    return message;
	}

	final boolean setNullMethod = nameMethod.equals("setNull") && args != null && args.length >= 1;
	if (setNullMethod) {
	    if (newQuery != null) {
		this.query = newQuery;
		newQuery = null;
	    }
	    final Object param = args[0];
	    final Object value = null;
	    query.putParams(param, value);
	    return message;
	}

	final boolean setMethod = nameMethod.startsWith("set") && args != null && args.length >= 2;
	if (setMethod) {
	    if (newQuery != null) {
		this.query = newQuery;
		newQuery = null;
	    }
	    final Object param = args[0];
	    final Object value = args[1];
	    query.putParams(param, value);
	    return message;
	}

	final boolean executeMethod = nameMethod.startsWith("execute") && !nameMethod.equals("executeBatch") && args == null;
	if (executeMethod) {

	    query.setMethodQuery(Query.METHOD_EXECUTE);
	    query.setTimeInvocation(timeInvocation);
	    final Integer updateCount = getUpdateCount(timeInvocation, method);
	    query.setUpdateCount(updateCount);

	    connectionContext.addQuery(query, false);

	    query.execute();
	    message.setQuery(query);
	    mic.setQuery(query);

	    // Creation de la prochaine requete
	    newQuery = createWrapperQuery(querySQLFactory, connectionContext, query);

	    return message;
	}

	return super.transformMessage(proxy, method, args, mic, message);
    }

    private static QueryImpl createWrapperQuery(final QueryFactory querySQLFactory, final ConnectionContext connectionContext, final QueryImpl query) {
	final QueryImpl newQuery = querySQLFactory.newQuery(connectionContext, query.getJDBCQuery());

	final Map jdbcParameters = query.getJDBCParameters();

	final Iterator entries = jdbcParameters.entrySet().iterator();
	while (entries.hasNext()) {
	    final Entry thisEntry = (Entry) entries.next();
	    final Object key = thisEntry.getKey();
	    final Object value = thisEntry.getValue();

	    newQuery.putParams(key, value);
	}

	return newQuery;
    }
}
