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
package fr.ms.log4jdbc;

import java.util.List;

import fr.ms.log4jdbc.lang.delegate.DefaultStringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.StringMakerFactory;
import fr.ms.log4jdbc.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SqlOperationContext extends SqlOperationDefault implements SqlOperation {

	private final ConnectionContextJDBC connectionContext;

	private final long openConnection;

	private QueryImpl query;

	private QueryImpl[] queriesBatch;

	private TransactionContextJDBC transaction;

	public SqlOperationContext(final TimeInvocation timeInvocation, final ConnectionContextJDBC connectionContext) {
		super(timeInvocation);

		this.connectionContext = connectionContext;
		this.openConnection = connectionContext.getOpenConnection().get();

		this.transaction = connectionContext.getTransactionContext();

		if (this.transaction != null) {
			try {
				this.transaction = (TransactionContextJDBC) this.transaction.clone();
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		connectionContext.cleanContext();
	}

	public SqlOperationContext(final TimeInvocation timeInvocation, final ConnectionContextJDBC connectionContext,
			final QueryImpl query) {
		this(timeInvocation, connectionContext);

		if (query != null) {
			try {
				this.query = (QueryImpl) query.clone();
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	public SqlOperationContext(final TimeInvocation timeInvocation, final ConnectionContextJDBC connectionContext,
			final QueryImpl query, final List queriesBatch) {
		this(timeInvocation, connectionContext, query);

		if (queriesBatch != null && !queriesBatch.isEmpty()) {
			try {
				this.queriesBatch = new QueryImpl[queriesBatch.size()];
				for (int i = 0; i < queriesBatch.size(); i++) {
					this.queriesBatch[i] = (QueryImpl) ((QueryImpl) queriesBatch.get(i)).clone();
				}
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

	}

	public long getConnectionNumber() {
		return connectionContext.getConnectionNumber();
	}

	public long getOpenConnection() {
		return openConnection;
	}

	public String getDriverName() {
		return connectionContext.getDriverName();
	}

	public String getUrl() {
		return connectionContext.getUrl();
	}

	public RdbmsSpecifics getRdbms() {
		return connectionContext.getRdbmsSpecifics();
	}

	public String getTransactionIsolation() {
		return connectionContext.getTransactionIsolation();
	}

	public Query getQuery() {
		return query;
	}

	public Query[] getQueriesBatch() {
		return queriesBatch;
	}

	public void setQuery(final QueryImpl query) {
		this.query = query;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public String toString() {
		final String nl = System.getProperty("line.separator");

		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker sb = stringFactory.newString();

		sb.append(getDate());
		sb.append(nl);
		sb.append(getConnectionNumber() + ". " + getOpenConnection() + " - executed : " + getExecTime() + " ms");
		sb.append(nl);
		sb.append("DriverName : " + getDriverName() + " - url : " + getUrl());
		sb.append(nl);
		if (getQuery() != null) {
			sb.append("*******************************************");
			sb.append(nl);
			sb.append("Query : ");
			sb.append(getQuery());
			sb.append(nl);
		}
		if (getTransaction() != null) {
			sb.append("*******************************************");
			sb.append(nl);
			sb.append("Transaction : ");
			sb.append(nl);
			sb.append(getTransaction());
			sb.append(nl);
		}

		return sb.toString();
	}
}
