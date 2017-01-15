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
package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.ResultSetOperation;
import fr.ms.log4jdbc.resultset.CellImpl;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ResultSetOperationFactory implements ProxyOperationFactory {

	private final ConnectionContextJDBC connectionContext;

	public final QueryImpl query;

	public final ResultSet rs;

	public Integer position;

	public CellImpl lastCell;

	public ResultSetOperationFactory(final ConnectionContextJDBC connectionContext, final ResultSet rs,
			final QueryImpl query) {
		this.connectionContext = connectionContext;
		this.rs = rs;
		this.query = query;

		position = new Integer(0);
		try {
			position = new Integer(rs.getRow());
		} catch (final Throwable e) {
		}
	}

	public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method,
			final Object[] args) {
		final ProxyOperation operation = new ResultSetOperation(this, connectionContext, timeInvocation, method, args);

		return operation;
	}

	public QueryImpl next(final boolean valid) {
		if (valid) {
			addPosition(1);
			return getQuery();
		} else {
			position = null;
			return null;
		}
	}

	public QueryImpl previous(final boolean valid) {
		if (valid) {
			if (position == null) {
				changePosition(Integer.MAX_VALUE);
			} else {
				addPosition(-1);
			}
			return getQuery();
		} else {
			position = null;
			return null;
		}
	}

	public QueryImpl first(final boolean valid) {
		if (valid) {
			position = new Integer(1);
			return getQuery();
		} else {
			position = null;
			return null;
		}
	}

	public QueryImpl last(final boolean valid) {
		if (valid) {
			changePosition(Integer.MAX_VALUE);
			return getQuery();
		} else {
			position = null;
			return null;
		}
	}

	public void beforeFirst() {
		position = null;
	}

	public void afterLast() {
		position = null;
	}

	public void wasNull() {
		lastCell.wasNull();
	}

	public void getMetaData(final Object invoke) {
		final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

		if (resultSetCollector.getColumns().length == 0) {
			final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
			resultSetCollector.setColumnsDetail(resultSetMetaData);
		}
	}

	public void addValueColumn(final Class clazz, final Object[] args, final Object invoke) {
		final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
		if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
			final Integer arg = (Integer) args[0];
			lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg.intValue());
		} else if (String.class.equals(clazz)) {
			final String arg = (String) args[0];
			lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg);
		}
	}

	public QueryImpl close() {
		final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

		if (!resultSetCollector.isClosed()) {
			resultSetCollector.close();

			return query;
		}

		return null;
	}

	private void addPosition(final int cursorPosition) {

		int newValue = 1;
		if (position != null) {
			newValue = position.intValue() + cursorPosition;
		}
		changePosition(newValue);
	}

	private void changePosition(final int cursorPosition) {
		try {
			position = new Integer(rs.getRow());
		} catch (final SQLException e) {
			position = new Integer(cursorPosition);
		}
	}

	private int getPosition() {
		if (position == null) {
			changePosition(1);
		}

		return position.intValue();
	}

	private QueryImpl getQuery() {
		final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
		resultSetCollector.getRow(position.intValue());

		if (!resultSetCollector.isClosed()) {
			return query;
		}
		return null;
	}
}
