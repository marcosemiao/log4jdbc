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
package fr.ms.log4jdbc.context.jdbc;

import java.util.ArrayList;
import java.util.List;

import fr.ms.log4jdbc.lang.delegate.DefaultStringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.StringMakerFactory;
import fr.ms.log4jdbc.lang.ref.ReferenceFactory;
import fr.ms.log4jdbc.lang.ref.ReferenceObject;
import fr.ms.log4jdbc.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.TransactionContextDefault;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.util.CollectionsUtil;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TransactionContextJDBC extends TransactionContextDefault implements Cloneable {

	private Object savePoint = null;

	private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Transaction";

	private ReferenceObject refQueries = ReferenceFactory.newReference(REF_MESSAGE_FULL,
			CollectionsUtil.synchronizedList(new ArrayList()));

	public QueryImpl addQuery(final QueryImpl query) {
		if (savePoint != null) {
			query.setSavePoint(savePoint);
		}

		if (Query.METHOD_BATCH.equals(query.getMethodQuery()) && !Transaction.STATE_EXECUTE.equals(state)) {
			state = Transaction.STATE_NOT_EXECUTE;
		} else {
			state = Transaction.STATE_EXECUTE;
		}

		query.setTransaction(this);

		final List queries = (List) refQueries.get();
		if (queries != null) {
			queries.add(query);
		}

		return query;
	}

	public void rollback(final Object savePoint) {
		final List queriesTransaction = (List) refQueries.get();
		if (queriesTransaction == null) {
			return;
		}

		int rollback = -1;

		if (queriesTransaction.size() > 0) {
			for (int i = 0; i < queriesTransaction.size(); i++) {
				final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

				final Object savePointQuery = q.getSavePoint();

				if (savePoint == null) {
					q.setState(Query.STATE_ROLLBACK);
				} else {
					if (rollback == -1 && savePoint.equals(savePointQuery)) {
						rollback = i;
					}
					if (rollback != -1) {
						q.setState(Query.STATE_ROLLBACK);
					}
				}
			}
		}

		if (state != null) {
			state = Transaction.STATE_ROLLBACK;
		}
	}

	public void executeBatch(final int[] updateCounts) {
		final List queriesTransaction = (List) refQueries.get();
		if (queriesTransaction == null && updateCounts == null) {
			return;
		}

		int compteur = -1;
		int updateCountsSize = 0;

		if (updateCounts != null) {
			updateCountsSize = updateCounts.length;
			compteur = 0;
		}

		final List queriesBatch = new ArrayList();
		if (queriesTransaction.size() > 0) {
			for (int i = 0; i < queriesTransaction.size(); i++) {
				final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

				if (Query.STATE_NOT_EXECUTE.equals(q.getState())) {
					q.setState(Query.STATE_EXECUTE);

					if (compteur <= updateCountsSize) {
						q.setUpdateCount(new Integer(updateCounts[compteur]));
						queriesBatch.add(q);
						compteur++;
					}
				}
			}
		}

		state = Transaction.STATE_EXECUTE;
	}

	public void commit() {
		final List queriesTransaction = (List) refQueries.get();
		if (queriesTransaction == null) {
			return;
		}

		if (queriesTransaction.size() > 0) {
			for (int i = 0; i < queriesTransaction.size(); i++) {
				final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

				if (Query.STATE_EXECUTE.equals(q.getState())) {
					q.setState(Query.STATE_COMMIT);
				}
			}
		}

		if (state != null) {
			state = Transaction.STATE_COMMIT;
		}
	}

	public void setSavePoint(final Object savePoint) {
		this.savePoint = savePoint;
	}

	public Query[] getQueriesTransaction() {
		final List queriesTransaction = (List) refQueries.get();
		if (queriesTransaction == null) {
			return null;
		}
		return (Query[]) queriesTransaction.toArray(new Query[queriesTransaction.size()]);
	}

	public ReferenceObject getRefQueries() {
		return refQueries;
	}

	public String getTransactionType() {
		return "JDBC";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getTransactionState() == null) ? 0 : getTransactionState().hashCode());
		result = prime * result + (int) (getTransactionNumber() ^ (getTransactionNumber() >>> 32));
		return result;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TransactionContextJDBC other = (TransactionContextJDBC) obj;

		final List queriesTransaction = (List) refQueries.get();
		final List queriesTransactionObject = (List) other.refQueries.get();

		if (queriesTransaction == null && queriesTransactionObject != null) {
			return false;
		}
		if (queriesTransaction != null && queriesTransactionObject == null) {
			return false;
		}

		if (queriesTransaction != null && queriesTransactionObject != null) {
			if (queriesTransaction.size() != queriesTransactionObject.size()) {
				return false;
			}
		}

		if (getTransactionState() == null) {
			if (other.getTransactionState() != null) {
				return false;
			}
		} else if (!getTransactionState().equals(other.getTransactionState())) {
			return false;
		}
		if (getTransactionNumber() != other.getTransactionNumber()) {
			return false;
		}
		return true;
	}

	private TransactionContextJDBC clone;

	public Object clone() throws CloneNotSupportedException {
		if (!this.equals(clone)) {

			clone = null;
			clone = (TransactionContextJDBC) super.clone();

			final List queriesTransaction = (List) clone.refQueries.get();

			if (queriesTransaction == null) {
				clone.refQueries = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());
			} else {
				final List copies = new ArrayList(queriesTransaction.size());
				for (int i = 0; i < queriesTransaction.size(); i++) {
					final QueryImpl query = (QueryImpl) queriesTransaction.get(i);
					final QueryImpl queryclone = (QueryImpl) query.clone();
					queryclone.setTransaction(clone);
					copies.add(queryclone);
				}

				clone.refQueries = ReferenceFactory.newReference(REF_MESSAGE_FULL, copies);
			}
		}

		return clone;
	}

	public String toString() {
		final String nl = System.getProperty("line.separator");

		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker sb = stringFactory.newString();

		sb.append(getTransactionNumber() + ". " + getOpenTransaction());
		sb.append(nl);
		sb.append("	Type  : " + getTransactionType());
		sb.append(nl);
		sb.append("	State  : " + getTransactionState());
		sb.append(nl);

		if (getQueriesTransaction() != null && getQueriesTransaction().length > 0) {
			sb.append("*********************");
			sb.append(nl);
			sb.append(getQueriesTransaction().length + " queries");

			for (int i = 0; i < getQueriesTransaction().length; i++) {
				sb.append(nl);
				sb.append(getQueriesTransaction()[i]);
			}
		}

		return sb.toString();
	}
}
