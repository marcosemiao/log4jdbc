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

import fr.ms.log4jdbc.context.BatchContext;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.context.TransactionContext;
import fr.ms.log4jdbc.invocationhandler.MessageInvocationHandler.MessageInvocationContext;
import fr.ms.log4jdbc.invocationhandler.TimeInvocation;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Batch;
import fr.ms.log4jdbc.sql.BatchImpl;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.Transaction;
import fr.ms.log4jdbc.sql.TransactionImpl;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class MessageHandlerImpl implements MessageHandler {

  private final TimeInvocation timeInvocation;

  private final JdbcContext jdbcContext;

  private final long openConnection;

  private Query query;

  private Batch batch;

  private Transaction transaction;

  public MessageHandlerImpl(final MessageInvocationContext mic) {
    this(mic.getInvokeTime(), mic.getJdbcContext());
  }

  public MessageHandlerImpl(final TimeInvocation timeInvocation, final JdbcContext jdbcContext) {
    this.timeInvocation = timeInvocation;
    this.jdbcContext = jdbcContext;
    openConnection = jdbcContext.getOpenConnection().getValue();

    try {
      final BatchContext batchContext = (BatchContext) jdbcContext.getBatchContext().clone();
      batch = new BatchImpl(batchContext);
    } catch (final CloneNotSupportedException e) {
      // Rien
    }

    try {
      final TransactionContext transactionContext = (TransactionContext) jdbcContext.getTransactionContext().clone();
      transaction = new TransactionImpl(transactionContext);
    } catch (final CloneNotSupportedException e) {
      // Rien
    }
  }

  public Date getDate() {
    return timeInvocation.getExecDate();
  }

  public long getExecTime() {
    return timeInvocation.getExecTime();
  }

  public long getConnectionNumber() {
    return jdbcContext.getConnectionNumber();
  }

  public long getOpenConnection() {
    return openConnection;
  }

  public Driver getDriver() {
    return jdbcContext.getDriver();
  }

  public RdbmsSpecifics getRdbms() {
    return jdbcContext.getRdbmsSpecifics();
  }

  public String getUrl() {
    return jdbcContext.getUrl();
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery(final Query query) {
    this.query = query;
  }

  public boolean isAutoCommit() {
    return jdbcContext.isAutoCommit();
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
    return "MessageHandlerImpl [getDate()=" + getDate() + ", getExecTime()=" + getExecTime()
        + ", getConnectionNumber()=" + getConnectionNumber() + ", getOpenConnection()=" + getOpenConnection()
        + ", getDriver()=" + getDriver() + ", getRdbms()=" + getRdbms() + ", getUrl()=" + getUrl() + ", getQuery()="
        + getQuery() + ", isAutoCommit()=" + isAutoCommit() + ", getTransaction()=" + getTransaction()
        + ", getBatch()=" + getBatch() + "]";
  }
}
