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
package fr.ms.log4jdbc.context.internal;

import java.sql.Driver;

import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.SyncLong;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContext {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalConnectionNumber = syncLongFactory.newLong();

    private final static SyncLong openConnection = syncLongFactory.newLong();

    private long connectionNumber;

    private Driver driver;

    private String url;

    private final RdbmsSpecifics rdbmsSpecifics;

    private boolean autoCommit = true;

    private BatchContext batchContext;

    private TransactionContext transactionContext;

    {
	this.connectionNumber = totalConnectionNumber.incrementAndGet();
	openConnection.incrementAndGet();

	transactionContext = new TransactionContext();
	batchContext = new BatchContext(transactionContext);
    }

    public ConnectionContext(final Class clazz) {
	this.rdbmsSpecifics = getRdbms(clazz);
    }

    public ConnectionContext(final Driver driver, final String url) {
	this.driver = driver;
	this.url = url;
	this.rdbmsSpecifics = getRdbms(driver.getClass());
    }

    public QueryImpl addQuery(final QueryImpl query, final boolean batch) {

	if (!autoCommit) {
	    if (batch) {
		batchContext.addQuery(query);
	    } else {
		transactionContext.addQuery(query);
	    }
	}

	return query;
    }

    public long getConnectionNumber() {
	return connectionNumber;
    }

    public SyncLong getTotalConnectionNumber() {
	return totalConnectionNumber;
    }

    public SyncLong getOpenConnection() {
	return openConnection;
    }

    public Driver getDriver() {
	return driver;
    }

    public String getUrl() {
	return url;
    }

    public RdbmsSpecifics getRdbmsSpecifics() {
	return rdbmsSpecifics;
    }

    public boolean isAutoCommit() {
	return autoCommit;
    }

    public void setAutoCommit(final boolean autoCommit) {
	this.autoCommit = autoCommit;
    }

    public BatchContext getBatchContext() {
	return batchContext;
    }

    public TransactionContext getTransactionContext() {
	return transactionContext;
    }

    public void resetTransaction() {
	transactionContext.decrement();
	transactionContext = new TransactionContext();
	resetBatch();
    }

    public void resetBatch() {
	batchContext.decrement();
	batchContext = new BatchContext(transactionContext);
    }

    private final static RdbmsSpecifics getRdbms(final Class driverClass) {
	final String classType = driverClass.getName();
	RdbmsSpecifics rdbmsSpecifics = ServicesJDBC.getRdbmsSpecifics(classType);
	if (rdbmsSpecifics == null) {
	    rdbmsSpecifics = GenericRdbmsSpecifics.getInstance();
	}

	return rdbmsSpecifics;
    }
}
