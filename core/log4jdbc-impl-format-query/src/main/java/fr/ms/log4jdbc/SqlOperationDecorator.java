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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.TransactionDecorator;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SqlOperationDecorator implements SqlOperation {

	private final SqlOperation sqlOperation;

	private final FormatQuery formatQuery;

	public SqlOperationDecorator(final SqlOperation sqlOperation, final FormatQuery formatQuery) {
		if (sqlOperation == null || formatQuery == null) {
			throw new NullPointerException();
		}
		this.sqlOperation = sqlOperation;
		this.formatQuery = formatQuery;
	}

	public Query getQuery() {
		final Query query = sqlOperation.getQuery();
		if (query == null) {
			return null;
		}
		return new QueryDecorator(query, sqlOperation.getRdbms(), formatQuery);
	}

	public Query[] getQueriesBatch() {
		final Query[] queriesBatch = sqlOperation.getQueriesBatch();
		if (queriesBatch == null) {
			return null;
		}

		final List queriesDecorator = new ArrayList(queriesBatch.length);

		for (int i = 0; i < queriesBatch.length; i++) {
			final Query query = queriesBatch[i];
			final Query queryDecorator = new QueryDecorator(query, sqlOperation.getRdbms(), formatQuery);

			queriesDecorator.add(queryDecorator);
		}

		return (Query[]) queriesDecorator.toArray(new Query[queriesDecorator.size()]);
	}

	public Transaction getTransaction() {
		final Transaction transaction = sqlOperation.getTransaction();
		if (transaction == null) {
			return null;
		}
		return new TransactionDecorator(transaction, sqlOperation.getRdbms(), formatQuery);
	}

	public Date getDate() {
		return sqlOperation.getDate();
	}

	public long getExecTime() {
		return sqlOperation.getExecTime();
	}

	public long getConnectionNumber() {
		return sqlOperation.getConnectionNumber();
	}

	public long getOpenConnection() {
		return sqlOperation.getOpenConnection();
	}

	public String getDriverName() {
		return sqlOperation.getDriverName();
	}

	public RdbmsSpecifics getRdbms() {
		return sqlOperation.getRdbms();
	}

	public String getTransactionIsolation() {
		return sqlOperation.getTransactionIsolation();
	}

	public String getUrl() {
		return sqlOperation.getUrl();
	}

	public int hashCode() {
		return sqlOperation.hashCode();
	}

	public boolean equals(final Object obj) {
		if (obj instanceof SqlOperationDecorator) {
			return sqlOperation.equals(((SqlOperationDecorator) obj).sqlOperation);
		}
		return sqlOperation.equals(obj);
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
