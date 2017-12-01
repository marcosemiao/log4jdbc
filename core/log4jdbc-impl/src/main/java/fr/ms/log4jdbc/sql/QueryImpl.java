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

import java.util.Date;
import java.util.Map;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.resultset.ResultSetCollector;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.internal.QuerySQL;
import fr.ms.util.logging.Logger;
import fr.ms.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QueryImpl implements Query, Cloneable {

	private final static Logger LOG = LoggerManager.getLogger(QueryImpl.class);

	private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

	private final static SyncLong nbQueryTotal = syncLongFactory.newLong();

	private long queryNumber;
	private TimeInvocation timeInvocation;

	private String methodQuery;

	private final QuerySQL query;

	private Integer updateCount;

	private ResultSetCollectorImpl resultSetCollector;

	private String state;

	private Transaction transaction;

	private Object savePoint;

	private QueryImpl clone;

	public QueryImpl(final QuerySQL query) {
		this.query = query;
	}

	public Date getDate() {
		if (timeInvocation == null) {
			return null;
		}
		return timeInvocation.getStartDate();
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
		if (query == null) {
			return null;
		}
		return query.getJDBCQuery();
	}

	public Map getJDBCParameters() {
		if (query == null) {
			return null;
		}
		return query.getJDBCParameters();
	}

	public String getTypeQuery() {
		if (query == null) {
			return null;
		}
		return query.getTypeQuery();
	}

	public String getSQLQuery() {
		if (query == null) {
			return null;
		}
		return query.getSQLQuery();
	}

	public Integer getUpdateCount() {
		return updateCount;
	}

	public ResultSetCollectorImpl createResultSetCollector(final ConnectionContextJDBC connectionContext) {
		if (resultSetCollector == null) {
			resultSetCollector = new ResultSetCollectorImpl(this, connectionContext);
		}
		return resultSetCollector;
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

	public Object putParams(final Object key, final Object value) {
		if (query == null) {
			return null;
		}
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

	public void setMethodQuery(final String methodQuery) {
		this.methodQuery = methodQuery;
	}

	public void setState(final String state) {
		if (state == null) {
			throw new NullPointerException();
		}

		if (query != null && this.state == null) {
			this.queryNumber = nbQueryTotal.incrementAndGet();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Nouvelle Requete SQL avec le numero : " + this.queryNumber);
			}
		}

		this.state = state;
	}

	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}

	public void setSavePoint(final Object savePoint) {
		this.savePoint = savePoint;
	}

	public Object getSavePoint() {
		return savePoint;
	}

	public Object clone() throws CloneNotSupportedException {
		if (!this.equals(clone)) {
			clone = null;
			clone = (QueryImpl) super.clone();
		}

		return clone;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (queryNumber ^ (queryNumber >>> 32));
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		final QueryImpl other = (QueryImpl) obj;
		if (queryNumber != other.queryNumber) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}

		if (transaction == null) {
			if (other.transaction != null) {
				return false;
			}
		} else if (!transaction.equals(other.transaction)) {
			return false;
		}
		return true;
	}

	public String toString() {
		final String nl = System.getProperty("line.separator");

		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker sb = stringFactory.newString();

		sb.append(getQueryNumber() + ".");
		sb.append(nl);
		sb.append("	Method : " + getMethodQuery());
		sb.append(nl);
		sb.append("	State  : " + getState());
		sb.append(nl);
		sb.append("	Query  : " + getSQLQuery());

		return sb.toString();
	}
}
