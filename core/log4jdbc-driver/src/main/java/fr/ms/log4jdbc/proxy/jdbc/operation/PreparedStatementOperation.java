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
package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.Statement;

import fr.ms.log4jdbc.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class PreparedStatementOperation extends StatementOperation {

	private final PreparedStatementOperationFactory context;

	public PreparedStatementOperation(final QueryFactory queryFactory, final PreparedStatementOperationFactory context,
			final Statement statement, final ConnectionContextJDBC connectionContext,
			final TimeInvocation timeInvocation, final Method method, final Object[] args) {
		super(queryFactory, context, statement, connectionContext, timeInvocation, method, args);
		this.context = context;
	}

	public SqlOperation getOperation() {
		final String nameMethod = method.getName();

		if (nameMethod.equals("addBatch") && args == null) {
			addBatch();
		} else if (nameMethod.equals("setNull") && args != null && args.length >= 1) {
			setNull(args);
		} else if (nameMethod.startsWith("set") && args != null && args.length >= 2) {
			set(args);
		} else if (nameMethod.startsWith("execute") && !nameMethod.equals("executeBatch") && args == null) {
			execute();
		}

		return super.getOperation();
	}

	private void addBatch() {
		query = context.getQuery();
		query.setTimeInvocation(timeInvocation);
		query.setMethodQuery(Query.METHOD_BATCH);

		context.addQueryBatch(query);

		connectionContext.addQuery(query);

		context.createNewQuery();
	}

	private void setNull(final Object[] args) {
		final QueryImpl queryContext = context.getQuery();
		final Object param = args[0];
		queryContext.putParams(param, null);
	}

	private void set(final Object[] args) {
		final QueryImpl queryContext = context.getQuery();
		final Object param = args[0];
		Object value = args[1];

		queryContext.putParams(param, value);
	}

	private void execute() {
		query = context.getQuery();

		query.setTimeInvocation(timeInvocation);
		query.setMethodQuery(Query.METHOD_EXECUTE);
		if (connectionContext.isTransactionEnabled()) {
			query.setState(Query.STATE_EXECUTE);
		} else {
			query.setState(Query.STATE_COMMIT);
		}

		final Integer updateCount = getUpdateCount(method);
		query.setUpdateCount(updateCount);

		connectionContext.addQuery(query);

		context.createNewQuery();
	}
}
