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
package fr.ms.log4jdbc.proxy;

import java.lang.reflect.InvocationHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.ms.lang.SystemPropertyUtils;
import fr.ms.lang.reflect.ProxyUtils;
import fr.ms.lang.reflect.TraceTimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.operator.OperationDecorator;
import fr.ms.log4jdbc.operator.ResultSetOperationInvocationHandler;
import fr.ms.log4jdbc.operator.impl.ConnectionDecorator;
import fr.ms.log4jdbc.operator.impl.PreparedStatementDecorator;
import fr.ms.log4jdbc.operator.impl.ResultSetDecorator;
import fr.ms.log4jdbc.operator.impl.StatementDecorator;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryNamedFactory;
import fr.ms.log4jdbc.sql.QuerySQLFactory;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class Log4JdbcProxy {

    private final static boolean devMode = SystemPropertyUtils.getProperty("log4jdbc.devMode", false);

    public static Connection proxyConnection(final Connection connection, final Driver driver, final String url) {
	final ConnectionContext connectionContext = new ConnectionContext(driver, url);
	final Connection wrap = Log4JdbcProxy.proxyConnection(connection, connectionContext);

	return wrap;
    }

    public static Connection proxyConnection(final Connection connection, final Class clazz) {
	final ConnectionContext connectionContext = new ConnectionContext(clazz);
	final Connection wrap = Log4JdbcProxy.proxyConnection(connection, connectionContext);

	return wrap;
    }

    public static Connection proxyConnection(final Connection connection, final ConnectionContext connectionContext) {
	final OperationDecorator handler = new ConnectionDecorator();

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.CONNECTION);
	final InvocationHandler wrapper = create(connection, connectionContext, messageLogger, handler);

	final Connection instance = (Connection) ProxyUtils.newProxyInstance(connection, wrapper);

	return instance;
    }

    public static Statement proxyStatement(final Statement statement, final ConnectionContext connectionContext) {
	final QueryFactory queryFactory = QuerySQLFactory.getInstance();
	final OperationDecorator handler = new StatementDecorator(statement, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.STATEMENT);
	final InvocationHandler wrapper = create(statement, connectionContext, messageLogger, handler);

	final Statement instance = (Statement) ProxyUtils.newProxyInstance(statement, wrapper);

	return instance;
    }

    public static PreparedStatement proxyPreparedStatement(final PreparedStatement preparedStatement, final ConnectionContext connectionContext,
	    final String sql) {
	final QueryFactory queryFactory = QuerySQLFactory.getInstance();
	final OperationDecorator handler = new PreparedStatementDecorator(preparedStatement, connectionContext, sql, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.PREPARED_STATEMENT);
	final InvocationHandler wrapper = create(preparedStatement, connectionContext, messageLogger, handler);

	final PreparedStatement instance = (PreparedStatement) ProxyUtils.newProxyInstance(preparedStatement, wrapper);

	return instance;
    }

    public static CallableStatement proxyCallableStatement(final CallableStatement callableStatement, final ConnectionContext connectionContext,
	    final String sql) {
	final QueryFactory queryFactory = QueryNamedFactory.getInstance();
	final OperationDecorator handler = new PreparedStatementDecorator(callableStatement, connectionContext, sql, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.CALLABLE_STATEMENT);
	final InvocationHandler wrapper = create(callableStatement, connectionContext, messageLogger, handler);

	final CallableStatement instance = (CallableStatement) ProxyUtils.newProxyInstance(callableStatement, wrapper);

	return instance;
    }

    public static ResultSet proxyResultSet(final ResultSet resultSet, final ConnectionContext connectionContext, final Query query) {
	final OperationDecorator handler = new ResultSetDecorator(query, resultSet);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.RESULT_SET);
	final InvocationHandler wrapper = create(resultSet, connectionContext, messageLogger, handler);

	final ResultSet instance = (ResultSet) ProxyUtils.newProxyInstance(resultSet, wrapper);

	return instance;
    }

    private static final InvocationHandler create(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	if (devMode) {
	    final InvocationHandler wrapper = createDev(implementation, connectionContext, logs, messageFactory);

	    return wrapper;
	} else {
	    final InvocationHandler wrapper = createProd(implementation, connectionContext, logs, messageFactory);

	    return wrapper;
	}
    }

    private static final InvocationHandler createDev(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	final InvocationHandler ih = new ResultSetOperationInvocationHandler(implementation, connectionContext, logs, messageFactory, true);

	final InvocationHandler wrapper = new TraceTimeInvocationHandler(ih);

	return wrapper;
    }

    private static final InvocationHandler createProd(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	final InvocationHandler wrapper = new ResultSetOperationInvocationHandler(implementation, connectionContext, logs, messageFactory);

	return wrapper;
    }
}
