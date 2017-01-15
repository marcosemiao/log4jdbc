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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
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
public class StatementOperation implements Log4JdbcOperation {

	protected final ConnectionContextJDBC connectionContext;
	protected final TimeInvocation timeInvocation;
	protected final Method method;
	protected final Object[] args;

	private final QueryFactory queryFactory;

	protected StatementOperationFactory context;

	protected Statement statement;

	protected QueryImpl query;

	public StatementOperation(final QueryFactory queryFactory, final StatementOperationFactory context,
			final Statement statement, final ConnectionContextJDBC connectionContext,
			final TimeInvocation timeInvocation, final Method method, final Object[] args) {
		this.connectionContext = connectionContext;
		this.timeInvocation = timeInvocation;
		this.method = method;
		this.args = args;

		this.queryFactory = queryFactory;
		this.statement = statement;
		this.context = context;
	}

	public SqlOperation getOperation() {
		final String nameMethod = method.getName();
		final Object invoke = timeInvocation.getInvoke();

		List queriesBatch = null;
		if (nameMethod.equals("addBatch") && args != null && args.length >= 1) {
			final String sql = (String) args[0];
			addBatch(sql);
		} else if (nameMethod.startsWith("execute") && nameMethod.endsWith("Batch") && args == null) {
			queriesBatch = executeBatch(invoke);
		} else if (nameMethod.startsWith("execute") && args != null && args.length >= 1) {
			final String sql = (String) args[0];
			execute(sql);
		}

		if (nameMethod.startsWith("execute")) {
			// execute retourne true boolean - GetResultSet
			final Class returnType = method.getReturnType();
			if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
				final Boolean invokeBoolean = (Boolean) timeInvocation.getInvoke();
				if (invokeBoolean != null && invokeBoolean.booleanValue()) {
					query.createResultSetCollector(connectionContext);
				}
			}
		}

		// Create ResultSetCollector and Proxy ResultSet
		if (invoke != null && invoke instanceof ResultSet) {

			final ResultSet resultSet = (ResultSet) invoke;

			QueryImpl queryRS = query;
			if (queryRS == null) {
				queryRS = context.getQuery();
			}

			final ResultSetCollectorImpl resultSetCollector = queryRS.createResultSetCollector(connectionContext);

			resultSetCollector.setRs(resultSet);
		}

		final SqlOperationContext sqlOperationContext = new SqlOperationContext(timeInvocation, connectionContext,
				query, queriesBatch);
		return sqlOperationContext;
	}

	private void addBatch(final String sql) {
		query = queryFactory.newQuery(connectionContext, sql);
		query.setTimeInvocation(timeInvocation);
		query.setMethodQuery(Query.METHOD_BATCH);

		context.addQueryBatch(query);
		connectionContext.addQuery(query);
	}

	private List executeBatch(final Object invoke) {
		int[] updateCounts = null;

		final Class returnType = method.getReturnType();
		if (invoke != null) {
			if (int[].class.equals(returnType)) {
				updateCounts = (int[]) invoke;
			}

			final TransactionContextJDBC transactionContext = connectionContext.getTransactionContext();
			if (transactionContext != null) {
				transactionContext.executeBatch(updateCounts);
			}
		}

		final List queriesBatch = context.executeBatch();
		return queriesBatch;
	}

	private void execute(final String sql) {
		query = queryFactory.newQuery(connectionContext, sql);
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

		context.setQuery(query);
	}

	protected Integer getUpdateCount(final Method method) {
		Integer updateCount = null;
		final Object invoke = timeInvocation.getInvoke();
		if (invoke == null) {
			return updateCount;
		}

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

	public Object getInvoke() {
		Object invoke = timeInvocation.getInvoke();
		if (invoke != null) {
			if (invoke instanceof ResultSet) {

				final ResultSet resultSet = (ResultSet) invoke;

				QueryImpl queryRS = query;
				if (queryRS == null) {
					queryRS = context.getQuery();
				}

				if (queryRS == null) {
					queryRS = queryFactory.newQuery(connectionContext, null);
				}

				invoke = Log4JdbcProxy.proxyResultSet(resultSet, connectionContext, queryRS);
			}
		}
		return invoke;
	}
}
