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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.ms.lang.ref.ReferenceFactory;
import fr.ms.lang.ref.ReferenceObject;
import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.StatementOperation;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;
import fr.ms.util.CollectionsUtil;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class StatementOperationFactory implements ProxyOperationFactory {

	protected final QueryFactory queryFactory;

	protected final ConnectionContextJDBC connectionContext;

	protected final Statement statement;

	protected QueryImpl query;

	private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Batch";

	protected ReferenceObject refQueriesBatch = ReferenceFactory.newReference(REF_MESSAGE_FULL,
			CollectionsUtil.synchronizedList(new ArrayList()));

	public StatementOperationFactory(final ConnectionContextJDBC connectionContext, final Statement statement,
			final QueryFactory queryFactory) {
		this.connectionContext = connectionContext;
		this.statement = statement;
		this.queryFactory = queryFactory;
	}

	
	public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method,
			final Object[] args) {
		final ProxyOperation operation = new StatementOperation(queryFactory, this, statement, connectionContext,
				timeInvocation, method, args);

		return operation;
	}

	public void setQuery(final QueryImpl query) {
		this.query = query;
	}

	public QueryImpl getQuery() {
		return query;
	}

	public void addQueryBatch(final QueryImpl query) {
		final List queriesBatch = (List) refQueriesBatch.get();
		if (queriesBatch != null) {
			queriesBatch.add(query);
		}
	}

	public List executeBatch() {
		List queriesBatch = (List) refQueriesBatch.get();
		if (queriesBatch != null && queriesBatch.isEmpty()) {
			queriesBatch = null;
		}

		refQueriesBatch = ReferenceFactory.newReference(REF_MESSAGE_FULL,
				CollectionsUtil.synchronizedList(new ArrayList()));

		return queriesBatch;
	}

	/*
	 * https://docs.oracle.com/javase/9/docs/api/java/sql/Statement.html Note:When a
	 * Statement object is closed, its current ResultSet object, if one exists, is
	 * also closed.
	 */
	public QueryImpl close(final TimeInvocation timeInvocation) {
		final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

		if (resultSetCollector != null) {
			return resultSetCollector.close(timeInvocation);
		}

		return null;
	}
}
