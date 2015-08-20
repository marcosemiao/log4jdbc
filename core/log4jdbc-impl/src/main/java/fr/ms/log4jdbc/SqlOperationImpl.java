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

import java.sql.Driver;
import java.util.Date;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.Batch;
import fr.ms.log4jdbc.context.BatchImpl;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.TransactionImpl;
import fr.ms.log4jdbc.context.internal.BatchContext;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.context.internal.TransactionContext;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SqlOperationImpl implements SqlOperation {

    private final TimeInvocation timeInvocation;

    private final ConnectionContext connectionContext;

    private final long openConnection;

    private Query query;

    private Batch batch;

    private Transaction transaction;

    public SqlOperationImpl(final SqlOperationContext mic) {
	this(mic.getInvokeTime(), mic.getconnectionContext());
    }

    public SqlOperationImpl(final TimeInvocation timeInvocation, final ConnectionContext connectionContext) {
	this.timeInvocation = timeInvocation;
	this.connectionContext = connectionContext;
	openConnection = connectionContext.getOpenConnection().get();

	try {
	    final BatchContext batchContext = (BatchContext) connectionContext.getBatchContext().clone();
	    batch = new BatchImpl(batchContext);
	} catch (final CloneNotSupportedException e) {
	    // Rien
	}

	try {
	    final TransactionContext transactionContext = (TransactionContext) connectionContext.getTransactionContext().clone();
	    transaction = new TransactionImpl(transactionContext);
	} catch (final CloneNotSupportedException e) {
	    // Rien
	}
    }

    public Date getDate() {
	return timeInvocation.getStartDate();
    }

    public long getExecTime() {
	return timeInvocation.getExecTime();
    }

    public long getConnectionNumber() {
	return connectionContext.getConnectionNumber();
    }

    public long getOpenConnection() {
	return openConnection;
    }

    public Driver getDriver() {
	return connectionContext.getDriver();
    }

    public RdbmsSpecifics getRdbms() {
	return connectionContext.getRdbmsSpecifics();
    }

    public String getUrl() {
	return connectionContext.getUrl();
    }

    public Query getQuery() {
	return query;
    }

    public void setQuery(final Query query) {
	this.query = query;
    }

    public boolean isAutoCommit() {
	return connectionContext.isAutoCommit();
    }

    public Transaction getTransaction() {
	if (isAutoCommit()) {
	    return null;
	}
	return transaction;
    }

    public Batch getBatch() {
	if (isAutoCommit()) {
	    return null;
	}
	return batch;
    }

    public String toString() {
	return "SqlOperationImpl [getDate()=" + getDate() + ", getExecTime()=" + getExecTime() + ", getConnectionNumber()=" + getConnectionNumber()
		+ ", getOpenConnection()=" + getOpenConnection() + ", getDriver()=" + getDriver() + ", getRdbms()=" + getRdbms() + ", getUrl()=" + getUrl()
		+ ", getQuery()=" + getQuery() + ", isAutoCommit()=" + isAutoCommit() + ", getTransaction()=" + getTransaction() + ", getBatch()=" + getBatch()
		+ "]";
    }
}
