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

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.Batch;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.BatchDecorator;
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

    private final SqlOperation messageHandler;

    private final FormatQuery formatQuery;

    public SqlOperationDecorator(final SqlOperation messageHandler, final FormatQuery formatQuery) {
	if (messageHandler == null || formatQuery == null) {
	    throw new NullPointerException();
	}
	this.messageHandler = messageHandler;
	this.formatQuery = formatQuery;
    }

    public Query getQuery() {
	final Query query = messageHandler.getQuery();
	if (query == null) {
	    return null;
	}
	return new QueryDecorator(query, messageHandler.getRdbms(), formatQuery);
    }

    public Transaction getTransaction() {
	final Transaction transaction = messageHandler.getTransaction();
	if (transaction == null) {
	    return null;
	}
	return new TransactionDecorator(transaction, messageHandler.getRdbms(), formatQuery);
    }

    public Batch getBatch() {
	final Batch batch = messageHandler.getBatch();
	if (batch == null) {
	    return null;
	}
	return new BatchDecorator(batch, messageHandler.getRdbms(), formatQuery);
    }

    public Date getDate() {
	return messageHandler.getDate();
    }

    public long getExecTime() {
	return messageHandler.getExecTime();
    }

    public long getConnectionNumber() {
	return messageHandler.getConnectionNumber();
    }

    public long getOpenConnection() {
	return messageHandler.getOpenConnection();
    }

    public Driver getDriver() {
	return messageHandler.getDriver();
    }

    public RdbmsSpecifics getRdbms() {
	return messageHandler.getRdbms();
    }

    public String getUrl() {
	return messageHandler.getUrl();
    }

    public boolean isAutoCommit() {
	return messageHandler.isAutoCommit();
    }

    public String toString() {
	return "WrapperMessageHandler [messageHandler=" + messageHandler + ", formatQuery=" + formatQuery + "]";
    }
}
