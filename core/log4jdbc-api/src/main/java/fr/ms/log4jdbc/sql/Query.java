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
package fr.ms.log4jdbc.sql;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
import java.util.Date;
import java.util.Map;

import fr.ms.log4jdbc.message.resultset.ResultSetCollector;

public interface Query {
  final static String STATE_NOT_EXECUTE = "STATE_NOT_EXECUTE";
  final static String STATE_EXECUTE = "STATE_EXECUTE";
  final static String STATE_COMMIT = "STATE_COMMIT";
  final static String STATE_ROLLBACK = "STATE_ROLLBACK";

  final static String METHOD_BATCH = "METHOD_BATCH";

  final static String METHOD_EXECUTE = "METHOD_EXECUTE";

  Date getDate();

  long getExecTime();

  long getQueryNumber();

  String getMethodQuery();

  String getJDBCQuery();

  Map getJDBCParameters();

  String getTypeQuery();

  String getSQLQuery(boolean withComment);

  Integer getUpdateCount();

  ResultSetCollector getResultSetCollector();

  String getState();

  Transaction getTransaction();

  Batch getBatch();
}
