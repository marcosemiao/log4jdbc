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
package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.ConnectionOperation;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;
import fr.ms.log4jdbc.sql.internal.QueryNamedFactory;
import fr.ms.log4jdbc.sql.internal.QuerySQLFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionOperationFactory implements ProxyOperationFactory {

	private boolean autoCommit;

	private final ConnectionContextJDBC connectionContext;

	public ConnectionOperationFactory(final ConnectionContextJDBC connectionContext, final Connection connection) {
		this.connectionContext = connectionContext;

		try {
			autoCommit = connection.getAutoCommit();
		} catch (Exception e) {
			autoCommit = true;
		}

		try {
			int transactionIsolation = connection.getTransactionIsolation();
			connectionContext.setTransactionIsolation(transactionIsolation);
		} catch (Exception e) {
		}
	}

	public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method,
			final Object[] args) {

		QueryImpl query = null;
		QueryFactory queryFactory = null;
		if (PreparedStatement.class.equals(method.getReturnType())) {
			queryFactory = QuerySQLFactory.getInstance();
		} else if (CallableStatement.class.equals(method.getReturnType())) {
			queryFactory = QueryNamedFactory.getInstance();
		}
		if (queryFactory != null) {
			final String sql = (String) args[0];
			query = queryFactory.newQuery(connectionContext, sql);
			query.setTimeInvocation(timeInvocation);
			query.setMethodQuery(Query.METHOD_UNKNOWN);
		}

		final ProxyOperation operation = new ConnectionOperation(this, connectionContext, timeInvocation, method, args,
				query);

		return operation;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(final boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean executeAutoCommit(final boolean autoCommit) {
		boolean commit = false;

		if (autoCommit && !this.autoCommit) {
			commit = true;
		}

		this.autoCommit = autoCommit;

		return commit;
	}
}
