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

import fr.ms.log4jdbc.message.resultset.ResultSetCollector;
import fr.ms.log4jdbc.message.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.Batch;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.ResultSetCollectorQuery;
import fr.ms.log4jdbc.sql.Transaction;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class EmptyQuery implements ResultSetCollectorQuery {

  private final static String MESSAGE = "resultSet-only";

  private final Date date = new Date();

  private ResultSetCollectorImpl resultSetCollector;

  public Date getDate() {
    return date;
  }

  public long getExecTime() {
    return -1;
  }

  public long getQueryNumber() {
    return -1;
  }

  public String getMethodQuery() {
    return MESSAGE;
  }

  public String getJDBCQuery() {
    return MESSAGE;
  }

  public String getJDBCQuery(final FormatQuery formatQuery) {
    return MESSAGE;
  }

  public Map getJDBCParameters() {
    return null;
  }

  public String getTypeQuery() {
    return MESSAGE;
  }

  public String getSQLQuery() {
    return MESSAGE;
  }

  public String getSQLQuery(final FormatQuery formatQuery) {
    return MESSAGE;
  }

  public Integer getUpdateCount() {
    return null;
  }

  public ResultSetCollector getResultSetCollector() {
    if (resultSetCollector == null || resultSetCollector.isMetaDataError()) {
      return null;
    }
    return resultSetCollector;
  }

  public String getState() {
    return MESSAGE;
  }

  public Transaction getTransaction() {
    return null;
  }

  public Batch getBatch() {
    return null;
  }

  public void setResultSetCollector(final ResultSetCollectorImpl resultSetCollector) {
    this.resultSetCollector = resultSetCollector;
  }

  public String toString() {
    return "EmptyQuery [resultSetCollector=" + resultSetCollector + "]";
  }
}