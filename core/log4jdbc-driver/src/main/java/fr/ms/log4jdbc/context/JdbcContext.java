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
package fr.ms.log4jdbc.context;

import java.sql.Driver;

import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.impl.WrapperQuery;
import fr.ms.log4jdbc.utils.LongSync;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class JdbcContext {

  private final static LongSync totalConnectionNumber = new LongSync();

  private final static LongSync openConnection = new LongSync();

  private long connectionNumber;

  private Driver driver;

  private final String url;

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

  public JdbcContext(final RdbmsSpecifics rdbmsSpecifics, final String url) {
    this.rdbmsSpecifics = rdbmsSpecifics;
    this.url = url;
  }

  public JdbcContext(final Class driverClass, final String url) {
    this.rdbmsSpecifics = getRdbms(driverClass);
    this.url = url;
  }

  public JdbcContext(final Driver driver, final String url) {
    this.driver = driver;
    this.url = url;
    this.rdbmsSpecifics = getRdbms(driver);
  }

  public WrapperQuery addQuery(final WrapperQuery query, final boolean batch) {
    if (!autoCommit) {
      transactionContext.addQuery(query);
    }

    if (batch && !autoCommit) {
      batchContext.addQuery(query);
    }
    return query;
  }

  public long getConnectionNumber() {
    return connectionNumber;
  }

  public LongSync getTotalConnectionNumber() {
    return totalConnectionNumber;
  }

  public LongSync getOpenConnection() {
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

  private final static RdbmsSpecifics getRdbms(final Driver driver) {
    return getRdbms(driver.getClass());
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
