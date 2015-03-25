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
package fr.ms.log4jdbc.utils;

import fr.ms.log4jdbc.sql.Batch;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.Transaction;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class QueryString {

  private final static String nl = System.getProperty("line.separator");

  public final static String buildMessageQuery(final Query query, final FormatQuery formatQuery) {
    final StringBuffer sb = new StringBuffer();

    sb.append("Query Number : " + query.getQueryNumber() + " - State : " + query.getState());

    final Integer updateCount = query.getUpdateCount();
    if (updateCount != null) {
      sb.append(" - Update Count : " + updateCount);
    }

    sb.append(nl);

    final Transaction transaction = query.getTransaction();
    if (transaction != null) {
      sb.append("Transaction Number : " + transaction.getTransactionNumber() + " - State : "
          + transaction.getTransactionState());
      sb.append(nl);
    }

    final Batch batch = query.getBatch();
    if (batch != null) {
      sb.append("Batch Number : " + batch.getBatchNumber() + " - State : " + batch.getBatchState());
      sb.append(nl);
    }

    sb.append(nl);

    final String sql = query.getSQLQuery(formatQuery);
    sb.append(sql);

    sb.append(nl);

    return sb.toString();
  }
}
