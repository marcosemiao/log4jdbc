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
package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.ConnectionContextDefault;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextJDBC extends ConnectionContextDefault {

	private boolean cleanTransaction;

	protected boolean transactionEnabled;

	private final TransactionContextFactory transactionContextFactory;

	protected TransactionContextJDBC transactionContext;

	public ConnectionContextJDBC(final Connection connection, final TransactionContextFactory transactionContextFactory,
			final Class clazz, final String url) {
		super(connection, clazz, url);
		this.transactionContextFactory = transactionContextFactory;
	}

	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}

	public void setTransactionEnabled(final boolean transactionEnabled) {
		if (!transactionEnabled) {
			transactionContext = null;
		}

		this.transactionEnabled = transactionEnabled;
	}

	public QueryImpl addQuery(final QueryImpl query) {
		if (transactionEnabled) {
			if (transactionContext == null) {
				transactionContext = transactionContextFactory.newTransactionContext(getConnection(), this);
			}
			transactionContext.addQuery(query);
		}

		return query;
	}

	public void close() {
		cleanTransaction = true;
		super.close();
	}

	public void cleanContext() {
		if (cleanTransaction) {
			if (transactionContext != null) {
				transactionContext.close();
				transactionContext = null;
			}
			cleanTransaction = false;
		}
	}

	public TransactionContextJDBC getTransactionContext() {
		return transactionContext;
	}

	public void commit() {
		if (transactionContext != null) {
			transactionContext.commit();
		}
		cleanTransaction = true;
	}

	public void rollback(final Object savePoint) {
		if (transactionContext != null) {
			transactionContext.rollback(savePoint);
		}
		cleanTransaction = savePoint == null;
	}

	public void resetTransaction() {
		cleanTransaction = true;
	}

	public String toString() {
		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker buffer = stringFactory.newString();

		buffer.append("ConnectionContextJDBC [driverName=");
		buffer.append(driverName);
		buffer.append(", url=");
		buffer.append(url);
		buffer.append(", connectionNumber=");
		buffer.append(connectionNumber);
		buffer.append(", rdbmsSpecifics=");
		buffer.append(rdbmsSpecifics);
		buffer.append(", transactionContext=");
		buffer.append(transactionContext);
		buffer.append("]");

		return buffer.toString();
	}
}
