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

import java.util.ArrayList;
import java.util.List;

import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.impl.WrapperQuery;
import fr.ms.log4jdbc.utils.LongSync;
import fr.ms.log4jdbc.utils.reference.ReferenceFactory;
import fr.ms.log4jdbc.utils.reference.ReferenceObject;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class BatchContext implements Cloneable {

  private final static LongSync totalBatchNumber = new LongSync();

  private final static LongSync openBatch = new LongSync();

  private String state;

  private boolean batchInit = false;

  private long batchNumber;

  private final TransactionContext transactionContext;

  private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Batch";
  private ReferenceObject refQueriesBatch = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());

  public BatchContext(final TransactionContext transactionContext) {
    this.transactionContext = transactionContext;
  }

  public void addQuery(final WrapperQuery query) {
    transactionContext.addQuery(query);
    query.setState(Query.STATE_NOT_EXECUTE);

    final List queriesBatch = (List) refQueriesBatch.get();
    if (queriesBatch != null) {
      queriesBatch.add(query);
    }

    query.setBatchContext(this);

    if (!batchInit) {
      batchInit = true;
      batchNumber = totalBatchNumber.incrementAndGet();
      openBatch.incrementAndGet();
    }

    state = Query.STATE_NOT_EXECUTE;
  }

  public void executeBatch(final int[] updateCounts) {
    final List queriesBatch = (List) refQueriesBatch.get();
    if (queriesBatch == null) {
      return;
    }

    if (queriesBatch.size() > 0) {

      for (int i = 0; i < queriesBatch.size(); i++) {
        final WrapperQuery q = (WrapperQuery) queriesBatch.get(i);
        q.setState(Query.STATE_EXECUTE);
        if (updateCounts != null) {
          q.setUpdateCount(new Integer(updateCounts[i]));
        }
      }
    }

    state = Query.STATE_EXECUTE;
  }

  public Query[] getQueriesBatch() {
    final List queriesBatch = (List) refQueriesBatch.get();
    if (queriesBatch == null) {
      return null;
    }
    return (Query[]) queriesBatch.toArray(new Query[queriesBatch.size()]);
  }

  public long getOpenBatch() {
    return openBatch.getValue();
  }

  public void decrement() {
    if (batchInit) {
      openBatch.decrementAndGet();
    }
  }

  public long getBatchNumber() {
    return batchNumber;
  }

  public String getState() {
    return state;
  }

  public Object clone() throws CloneNotSupportedException {
    final BatchContext b = (BatchContext) super.clone();
    final List queriesBatch = (List) refQueriesBatch.get();
    if (queriesBatch == null) {
      b.refQueriesBatch = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());
    } else {
      b.refQueriesBatch = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList(queriesBatch));
    }
    return b;
  }
}
