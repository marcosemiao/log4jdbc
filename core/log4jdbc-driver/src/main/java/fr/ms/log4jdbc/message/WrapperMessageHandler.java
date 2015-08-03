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
package fr.ms.log4jdbc.message;

import java.sql.Driver;
import java.util.Date;

import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Batch;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.Transaction;
import fr.ms.log4jdbc.sql.WrapperBatch;
import fr.ms.log4jdbc.sql.WrapperQuery;
import fr.ms.log4jdbc.sql.WrapperTransaction;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class WrapperMessageHandler implements MessageHandler {

    private final MessageHandler messageHandler;

    private final FormatQuery formatQuery;

    public WrapperMessageHandler(final MessageHandler messageHandler, final FormatQuery formatQuery) {
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
	return new WrapperQuery(query, messageHandler.getRdbms(), formatQuery);
    }

    public Transaction getTransaction() {
	final Transaction transaction = messageHandler.getTransaction();
	if (transaction == null) {
	    return null;
	}
	return new WrapperTransaction(transaction, messageHandler.getRdbms(), formatQuery);
    }

    public Batch getBatch() {
	final Batch batch = messageHandler.getBatch();
	if (batch == null) {
	    return null;
	}
	return new WrapperBatch(batch, messageHandler.getRdbms(), formatQuery);
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
