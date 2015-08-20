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

import fr.ms.lang.reflect.ProxyUtils;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.messagefactory.ConnectionHandler;
import fr.ms.log4jdbc.messagefactory.PreparedStatementHandler;
import fr.ms.log4jdbc.messagefactory.ResultSetHandler;
import fr.ms.log4jdbc.messagefactory.StatementHandler;
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
	final MessageFactory handler = new ConnectionHandler();

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.CONNECTION);
	final InvocationHandler wrapper = CreateInvocationHandler.create(connection, connectionContext, messageLogger, handler);

	final Connection instance = (Connection) ProxyUtils.newProxyInstance(connection, wrapper);

	return instance;
    }

    public static Statement proxyStatement(final Statement statement, final ConnectionContext connectionContext) {
	final QueryFactory queryFactory = QuerySQLFactory.getInstance();
	final MessageFactory handler = new StatementHandler(statement, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.STATEMENT);
	final InvocationHandler wrapper = CreateInvocationHandler.create(statement, connectionContext, messageLogger, handler);

	final Statement instance = (Statement) ProxyUtils.newProxyInstance(statement, wrapper);

	return instance;
    }

    public static PreparedStatement proxyPreparedStatement(final PreparedStatement preparedStatement, final ConnectionContext connectionContext, final String sql) {
	final QueryFactory queryFactory = QuerySQLFactory.getInstance();
	final MessageFactory handler = new PreparedStatementHandler(preparedStatement, connectionContext, sql, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.PREPARED_STATEMENT);
	final InvocationHandler wrapper = CreateInvocationHandler.create(preparedStatement, connectionContext, messageLogger, handler);

	final PreparedStatement instance = (PreparedStatement) ProxyUtils.newProxyInstance(preparedStatement, wrapper);

	return instance;
    }

    public static CallableStatement proxyCallableStatement(final CallableStatement callableStatement, final ConnectionContext connectionContext, final String sql) {
	final QueryFactory queryFactory = QueryNamedFactory.getInstance();
	final MessageFactory handler = new PreparedStatementHandler(callableStatement, connectionContext, sql, queryFactory);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.CALLABLE_STATEMENT);
	final InvocationHandler wrapper = CreateInvocationHandler.create(callableStatement, connectionContext, messageLogger, handler);

	final CallableStatement instance = (CallableStatement) ProxyUtils.newProxyInstance(callableStatement, wrapper);

	return instance;
    }

    public static ResultSet proxyResultSet(final ResultSet resultSet, final ConnectionContext connectionContext, final Query query) {
	final MessageFactory handler = new ResultSetHandler(query, resultSet);

	final SqlOperationLogger[] messageLogger = ServicesJDBC.getMessageLogger(SqlOperationLogger.RESULT_SET);
	final InvocationHandler wrapper = CreateInvocationHandler.create(resultSet, connectionContext, messageLogger, handler);

	final ResultSet instance = (ResultSet) ProxyUtils.newProxyInstance(resultSet, wrapper);

	return instance;
    }

}
