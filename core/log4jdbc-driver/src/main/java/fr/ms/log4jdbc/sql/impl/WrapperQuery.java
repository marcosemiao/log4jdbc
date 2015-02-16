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
package fr.ms.log4jdbc.sql.impl;

import java.util.Date;
import java.util.Map;

import fr.ms.log4jdbc.context.BatchContext;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.context.TransactionContext;
import fr.ms.log4jdbc.invocationhandler.TimeInvocation;
import fr.ms.log4jdbc.message.resultset.ResultSetCollector;
import fr.ms.log4jdbc.message.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Batch;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QuerySQLFactory;
import fr.ms.log4jdbc.sql.ResultSetCollectorQuery;
import fr.ms.log4jdbc.sql.Transaction;
import fr.ms.log4jdbc.utils.LongSync;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class WrapperQuery implements ResultSetCollectorQuery {

  private final static LongSync NbQueryTotal = new LongSync();

  private final long queryNumber;
  private TimeInvocation timeInvocation;

  private String methodQuery;

  private final QuerySQL query;

  private Integer updateCount;

  private ResultSetCollectorImpl resultSetCollector;

  private String state = Query.STATE_COMMIT;

  private Batch batch;

  private Transaction transaction;

  private Object savePoint;

  WrapperQuery(final QuerySQL query) {
    this.queryNumber = NbQueryTotal.incrementAndGet();
    this.query = query;
  }

  public Date getDate() {
    if (timeInvocation == null) {
      return null;
    }
    return timeInvocation.getExecDate();
  }

  public long getExecTime() {
    if (timeInvocation == null) {
      return -1;
    }
    return timeInvocation.getExecTime();
  }

  public long getQueryNumber() {
    return queryNumber;
  }

  public String getMethodQuery() {
    return methodQuery;
  }

  public String getJDBCQuery() {
    return query.getJDBCQuery();
  }

  public Map getJDBCParameters() {
    return query.getJDBCParameters();
  }

  public String getTypeQuery() {
    return query.getTypeQuery();
  }

  public String getSQLQuery(final boolean withComment) {
    return query.getSQLQuery(withComment);
  }

  public Integer getUpdateCount() {
    return updateCount;
  }

  public ResultSetCollector getResultSetCollector() {
    if (resultSetCollector == null || resultSetCollector.isMetaDataError()) {
      return null;
    }
    return resultSetCollector;
  }

  public String getState() {
    return state;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public Batch getBatch() {
    return batch;
  }

  public Object putParams(final Object key, final Object value) {
    return query.putParams(key, value);
  }

  public void setTimeInvocation(final TimeInvocation timeInvocation) {
    this.timeInvocation = timeInvocation;
  }

  public void setUpdateCount(final Integer updateCount) {
    if (updateCount != null && updateCount.intValue() >= 0) {
      this.updateCount = updateCount;
    }
  }

  public void setResultSetCollector(final ResultSetCollectorImpl resultSetCollector) {
    this.resultSetCollector = resultSetCollector;
  }

  public void setMethodQuery(final String methodQuery) {
    this.methodQuery = methodQuery;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public void setBatchContext(final BatchContext batchContext) {
    this.batch = new BatchImpl(batchContext);
  }

  public void setTransactionContext(final TransactionContext transactionContext) {
    this.transaction = new TransactionImpl(transactionContext);
  }

  public void setSavePoint(final Object savePoint) {
    this.savePoint = savePoint;
  }

  public Object getSavePoint() {
    return savePoint;
  }

  public String toString() {
    final StringBuffer builder = new StringBuffer();
    builder.append("WrapperQuery [queryNumber=");
    builder.append(queryNumber);
    builder.append(", state=");
    builder.append(state);
    builder.append(", query=");
    builder.append(query);
    builder.append("]");
    return builder.toString();
  }

  public static QuerySQLFactory getQuerySQL() {
    final QuerySQLFactory factory = new QuerySQLFactory() {

      public WrapperQuery newQuerySQL(final JdbcContext jdbcContext, final String jdbcQuery) {
        final RdbmsSpecifics rdms = jdbcContext.getRdbmsSpecifics();
        final QuerySQL query = new QuerySQL(rdms, jdbcQuery);
        final WrapperQuery wrapper = new WrapperQuery(query);
        return wrapper;
      }

    };
    return factory;
  }

  public static QuerySQLFactory getQuerySQLNamed() {
    final QuerySQLFactory factory = new QuerySQLFactory() {

      public WrapperQuery newQuerySQL(final JdbcContext jdbcContext, final String jdbcQuery) {
        final RdbmsSpecifics rdms = jdbcContext.getRdbmsSpecifics();
        final QuerySQLNamed query = new QuerySQLNamed(rdms, jdbcQuery);
        final WrapperQuery wrapper = new WrapperQuery(query);
        return wrapper;
      }

    };
    return factory;
  }
}
