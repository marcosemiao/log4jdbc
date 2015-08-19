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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.internal.JdbcContext;
import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionHandler implements MessageFactory {

    public SqlOperationImpl transformMessage(final Object proxy, final Method method, final Object[] args, final SqlOperationContext mic,
	    final SqlOperationImpl message) {
	final TimeInvocation timeInvocation = mic.getInvokeTime();
	final JdbcContext jdbcContext = mic.getJdbcContext();

	final Object invoke = timeInvocation.getInvoke();

	final String nameMethod = method.getName();

	boolean commitMethod = nameMethod.equals("commit");

	final boolean setAutoCommitMethod = nameMethod.equals("setAutoCommit");
	if (setAutoCommitMethod) {
	    final Boolean autoCommit = (Boolean) args[0];
	    final boolean etatActuel = jdbcContext.isAutoCommit();
	    jdbcContext.setAutoCommit(autoCommit.booleanValue());

	    if (etatActuel == false && jdbcContext.isAutoCommit()) {
		commitMethod = true;
	    }
	}

	if (commitMethod) {
	    jdbcContext.getTransactionContext().commit();
	    jdbcContext.resetTransaction();
	}

	final boolean rollbackMethod = nameMethod.equals("rollback");
	if (rollbackMethod) {
	    jdbcContext.getTransactionContext().rollback(invoke);
	    if (invoke == null) {
		jdbcContext.resetTransaction();
	    }
	}

	final boolean setSavepointMethod = nameMethod.equals("setSavepoint");
	if (setSavepointMethod) {
	    jdbcContext.getTransactionContext().setSavePoint(invoke);
	}

	final boolean closeMethod = nameMethod.equals("close");
	if (closeMethod) {
	    jdbcContext.getOpenConnection().decrementAndGet();
	}
	return message;
    }

    public Object wrap(final Object invoke, final Object[] args, final SqlOperationContext mic) {
	if (invoke != null) {
	    final JdbcContext jdbcContext = mic.getJdbcContext();
	    if (invoke instanceof CallableStatement) {
		final CallableStatement callableStatement = (CallableStatement) invoke;
		final String sql = (String) args[0];
		return Log4JdbcProxy.proxyCallableStatement(callableStatement, jdbcContext, sql);
	    } else if (invoke instanceof PreparedStatement) {
		final PreparedStatement preparedStatement = (PreparedStatement) invoke;
		final String sql = (String) args[0];
		return Log4JdbcProxy.proxyPreparedStatement(preparedStatement, jdbcContext, sql);
	    } else if (invoke instanceof Statement) {
		final Statement statement = (Statement) invoke;
		return Log4JdbcProxy.proxyStatement(statement, jdbcContext);
	    }
	}
	return invoke;
    }
}
