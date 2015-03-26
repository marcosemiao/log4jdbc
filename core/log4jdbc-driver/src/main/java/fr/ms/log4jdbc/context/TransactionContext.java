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
public class TransactionContext implements Cloneable {

  private final static LongSync totalTransactionNumber = new LongSync();

  private final static LongSync openTransaction = new LongSync();

  private String state;

  private boolean transactionInit = false;
  private long transactionNumber;

  private Object savePoint = null;

  private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Transaction";
  private ReferenceObject refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());

  public void addQuery(final WrapperQuery query) {
    addQuery(query, false);
  }

  public void addQuery(final WrapperQuery query, final boolean batch) {
    query.setState(Query.STATE_EXECUTE);
    if (savePoint != null) {
      query.setSavePoint(savePoint);
    }

    final List queriesTransaction = (List) refQueriesTransaction.get();
    if (queriesTransaction != null) {
      queriesTransaction.add(query);
    }

    query.setTransactionContext(this);

    if (!transactionInit) {
      transactionInit = true;
      transactionNumber = totalTransactionNumber.incrementAndGet();
      openTransaction.incrementAndGet();
    }

    if (batch) {
      state = Query.STATE_NOT_EXECUTE;
    } else {
      state = Query.STATE_EXECUTE;
    }
  }

  public void rollback(final Object savePoint) {
    final List queriesTransaction = (List) refQueriesTransaction.get();
    if (queriesTransaction == null) {
      return;
    }

    int rollback = -1;

    if (queriesTransaction.size() > 0) {
      for (int i = 0; i < queriesTransaction.size(); i++) {
        final WrapperQuery q = (WrapperQuery) queriesTransaction.get(i);

        final Object savePointQuery = q.getSavePoint();

        if (savePoint != null && rollback == -1 && savePoint.equals(savePointQuery)) {
          rollback = i;
        }
        if (rollback != -1) {
          q.setState(Query.STATE_ROLLBACK);
        }
      }
    }

    state = Query.STATE_ROLLBACK;
  }

  public void commit() {
    final List queriesTransaction = (List) refQueriesTransaction.get();
    if (queriesTransaction == null) {
      return;
    }

    if (queriesTransaction.size() > 0) {
      for (int i = 0; i < queriesTransaction.size(); i++) {
        final WrapperQuery q = (WrapperQuery) queriesTransaction.get(i);

        if (Query.STATE_EXECUTE.equals(q.getState())) {
          q.setState(Query.STATE_COMMIT);
        }
      }
    }

    state = Query.STATE_COMMIT;
  }

  public void setSavePoint(final Object savePoint) {
    this.savePoint = savePoint;
  }

  public long getOpenTransaction() {
    return openTransaction.getValue();
  }

  public void decrement() {
    if (transactionInit) {
      openTransaction.decrementAndGet();
    }
  }

  public long getTransactionNumber() {
    return transactionNumber;
  }

  public Query[] getQueriesTransaction() {
    final List queriesTransaction = (List) refQueriesTransaction.get();
    if (queriesTransaction == null) {
      return null;
    }
    return (Query[]) queriesTransaction.toArray(new Query[queriesTransaction.size()]);
  }

  public String getState() {
    return state;
  }

  public Object clone() throws CloneNotSupportedException {
    final TransactionContext t = (TransactionContext) super.clone();
    final List queriesTransaction = (List) refQueriesTransaction.get();
    if (queriesTransaction == null) {
      t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());
    } else {
      t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList(queriesTransaction));
    }
    return t;
  }
}
