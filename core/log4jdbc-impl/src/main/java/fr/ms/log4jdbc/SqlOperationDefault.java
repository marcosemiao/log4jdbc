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
package fr.ms.log4jdbc;

import java.util.Date;

import fr.ms.log4jdbc.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SqlOperationDefault implements SqlOperation {

	private final TimeInvocation timeInvocation;

	public SqlOperationDefault(final TimeInvocation timeInvocation) {
		this.timeInvocation = timeInvocation;
	}

	public Date getDate() {
		return timeInvocation.getStartDate();
	}

	public long getExecTime() {
		return timeInvocation.getExecTime();
	}

	public long getConnectionNumber() {
		return -1;
	}

	public long getOpenConnection() {
		return -1;
	}

	public String getDriverName() {
		return null;
	}

	public RdbmsSpecifics getRdbms() {
		return null;
	}

	public String getTransactionIsolation() {
		return null;
	}

	public String getUrl() {
		return null;
	}

	public Query getQuery() {
		return null;
	}

	public Query[] getQueriesBatch() {
		return null;
	}

	public Transaction getTransaction() {
		return null;
	}
}
