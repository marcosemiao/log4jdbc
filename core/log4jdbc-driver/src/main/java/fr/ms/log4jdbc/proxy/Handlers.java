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
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.ms.log4jdbc.MessageLogger;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.messagefactory.ConnectionHandler;
import fr.ms.log4jdbc.messagefactory.PreparedStatementHandler;
import fr.ms.log4jdbc.messagefactory.ResultSetHandler;
import fr.ms.log4jdbc.messagefactory.StatementHandler;
import fr.ms.log4jdbc.sql.QuerySQLFactory;
import fr.ms.log4jdbc.sql.ResultSetCollectorQuery;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class Handlers {

  public static Connection getConnection(final Connection connection, final JdbcContext jdbcContext) {
    final ClassLoader classLoader = Handlers.class.getClassLoader();
    final Class[] interfaces = new Class[]{Connection.class};

    final MessageFactory handler = new ConnectionHandler();

    final MessageLogger[] messageLogger = ServicesJDBC.getMessageLogger(MessageLogger.CONNECTION);
    final InvocationHandler wrapper = CreateInvocationHandler.create(connection, jdbcContext, messageLogger, handler);

    final Connection instance = (Connection) Proxy.newProxyInstance(classLoader, interfaces, wrapper);

    return instance;
  }

  public static Statement getStatement(final Statement statement, final JdbcContext jdbcContext,
      final QuerySQLFactory querySQLFactory) {
    final ClassLoader classLoader = Handlers.class.getClassLoader();
    final Class[] interfaces = new Class[]{Statement.class};

    final MessageFactory handler = new StatementHandler(statement, querySQLFactory);

    final MessageLogger[] messageLogger = ServicesJDBC.getMessageLogger(MessageLogger.STATEMENT);
    final InvocationHandler wrapper = CreateInvocationHandler.create(statement, jdbcContext, messageLogger, handler);

    final Statement instance = (Statement) Proxy.newProxyInstance(classLoader, interfaces, wrapper);

    return instance;
  }

  public static PreparedStatement getPreparedStatement(final PreparedStatement preparedStatement,
      final JdbcContext jdbcContext, final QuerySQLFactory querySQLFactory, final String sql) {
    final ClassLoader classLoader = Handlers.class.getClassLoader();
    final Class[] interfaces = new Class[]{PreparedStatement.class};

    final MessageFactory handler = new PreparedStatementHandler(preparedStatement, jdbcContext, sql, querySQLFactory);

    final MessageLogger[] messageLogger = ServicesJDBC.getMessageLogger(MessageLogger.PREPARED_STATEMENT);
    final InvocationHandler wrapper = CreateInvocationHandler.create(preparedStatement, jdbcContext, messageLogger,
        handler);

    final PreparedStatement instance = (PreparedStatement) Proxy.newProxyInstance(classLoader, interfaces, wrapper);

    return instance;
  }

  public static CallableStatement getCallableStatement(final CallableStatement callableStatement,
      final JdbcContext jdbcContext, final QuerySQLFactory querySQLFactory, final String sql) {
    final ClassLoader classLoader = Handlers.class.getClassLoader();
    final Class[] interfaces = new Class[]{PreparedStatement.class};

    final MessageFactory handler = new PreparedStatementHandler(callableStatement, jdbcContext, sql, querySQLFactory);

    final MessageLogger[] messageLogger = ServicesJDBC.getMessageLogger(MessageLogger.CALLABLE_STATEMENT);
    final InvocationHandler wrapper = CreateInvocationHandler.create(callableStatement, jdbcContext, messageLogger,
        handler);

    final CallableStatement instance = (CallableStatement) Proxy.newProxyInstance(classLoader, interfaces, wrapper);

    return instance;
  }

  public static ResultSet getResultSet(final ResultSet resultSet, final JdbcContext jdbcContext,
      final ResultSetCollectorQuery query) {
    final ClassLoader classLoader = Handlers.class.getClassLoader();
    final Class[] interfaces = new Class[]{ResultSet.class};

    final MessageFactory handler = new ResultSetHandler(jdbcContext, query, resultSet);

    final MessageLogger[] messageLogger = ServicesJDBC.getMessageLogger(MessageLogger.RESULT_SET);
    final InvocationHandler wrapper = CreateInvocationHandler.create(resultSet, jdbcContext, messageLogger, handler);

    final ResultSet instance = (ResultSet) Proxy.newProxyInstance(classLoader, interfaces, wrapper);

    return instance;
  }
}
