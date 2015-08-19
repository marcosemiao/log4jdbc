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
package fr.ms.log4jdbc.messagefactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.internal.JdbcContext;
import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
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
public class StatementHandler implements MessageFactory {

    private final Statement statement;

    protected QueryImpl query;

    protected final QueryFactory querySQLFactory;

    public StatementHandler(final Statement statement, final QueryFactory querySQLFactory) {
	this.statement = statement;
	this.querySQLFactory = querySQLFactory;
    }

    public SqlOperationImpl transformMessage(final Object proxy, final Method method, final Object[] args, final SqlOperationContext mic,
	    final SqlOperationImpl message) {
	final TimeInvocation timeInvocation = mic.getInvokeTime();
	final JdbcContext jdbcContext = mic.getJdbcContext();

	final String nameMethod = method.getName();

	final boolean addBatchMethod = nameMethod.equals("addBatch") && args != null && args.length >= 1;
	if (addBatchMethod) {
	    final String sql = (String) args[0];

	    final QueryImpl query = querySQLFactory.newQuery(jdbcContext, sql);
	    query.setMethodQuery(Query.METHOD_BATCH);
	    query.setTimeInvocation(timeInvocation);

	    jdbcContext.addQuery(query, true);

	    query.execute();
	    message.setQuery(query);
	    mic.setQuery(query);

	    return message;
	}

	final boolean executeBatchMethod = nameMethod.equals("executeBatch") && args == null;
	if (executeBatchMethod) {
	    final Object invoke = timeInvocation.getInvoke();
	    int[] updateCounts = null;

	    final Class returnType = method.getReturnType();
	    if (invoke != null) {
		if (int[].class.equals(returnType)) {
		    updateCounts = (int[]) invoke;
		}
	    }

	    jdbcContext.getBatchContext().executeBatch(updateCounts);
	    jdbcContext.resetBatch();
	    return message;
	}

	final boolean executeMethod = nameMethod.startsWith("execute") && args != null && args.length >= 1;
	if (executeMethod) {
	    final String sql = (String) args[0];
	    final QueryImpl query = querySQLFactory.newQuery(jdbcContext, sql);
	    query.setMethodQuery(Query.METHOD_EXECUTE);
	    query.setTimeInvocation(timeInvocation);
	    final Integer updateCount = getUpdateCount(timeInvocation, method);
	    query.setUpdateCount(updateCount);

	    jdbcContext.addQuery(query, false);

	    query.execute();
	    message.setQuery(query);
	    mic.setQuery(query);

	    // execute retourne true boolean - GetResultSet
	    final Class returnType = method.getReturnType();
	    if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
		final Boolean invokeBoolean = (Boolean) timeInvocation.getInvoke();
		if (invokeBoolean.booleanValue()) {
		    query.initResultSetCollector(jdbcContext);
		    this.query = query;
		}
	    }

	    return message;
	}

	mic.setQuery(query);

	return message;
    }

    public Object wrap(final Object invoke, final Object[] args, final SqlOperationContext mic) {
	if (invoke != null) {
	    if (invoke instanceof ResultSet) {
		final JdbcContext jdbcContext = mic.getJdbcContext();

		final ResultSet resultSet = (ResultSet) invoke;

		Query query = mic.getQuery();
		if (query == null) {
		    final QueryImpl wrapperQuery = querySQLFactory.newQuery(jdbcContext, null);
		    wrapperQuery.execute();
		    wrapperQuery.initResultSetCollector(jdbcContext, resultSet);

		    query = wrapperQuery;
		}

		return Log4JdbcProxy.proxyResultSet(resultSet, jdbcContext, query);
	    }
	}
	return invoke;
    }

    protected Integer getUpdateCount(final TimeInvocation timeInvocation, final Method method) {
	Integer updateCount = null;
	final Object invoke = timeInvocation.getInvoke();
	final Class returnType = method.getReturnType();
	if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
	    updateCount = (Integer) invoke;
	} else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
	    final Boolean invokeBoolean = (Boolean) invoke;
	    if (timeInvocation.getTargetException() == null && !invokeBoolean.booleanValue()) {
		try {
		    updateCount = new Integer(statement.getUpdateCount());
		} catch (final SQLException e) {
		    return null;
		}
	    }
	}
	return updateCount;
    }
}
