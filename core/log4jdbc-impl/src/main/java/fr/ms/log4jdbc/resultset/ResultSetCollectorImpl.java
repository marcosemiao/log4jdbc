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
package fr.ms.log4jdbc.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ms.log4jdbc.lang.delegate.DefaultStringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.StringMakerFactory;
import fr.ms.log4jdbc.lang.ref.ReferenceFactory;
import fr.ms.log4jdbc.lang.ref.ReferenceObject;
import fr.ms.log4jdbc.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.util.CollectionsUtil;
import fr.ms.log4jdbc.util.logging.Logger;
import fr.ms.log4jdbc.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ResultSetCollectorImpl implements ResultSetCollector {

	private final static Logger LOG = LoggerManager.getLogger(ResultSetCollectorImpl.class);

	private boolean metaDataError = false;
	private boolean metaData = false;

	private ResultSet rs;

	private String state = ResultSetCollector.STATE_OPEN;

	// Columns
	private boolean columnsUpdate;

	private final Map columnsDetailPerIndex = CollectionsUtil.synchronizedMap(new HashMap());
	private final Map columnsDetailPerLabel = CollectionsUtil.synchronizedMap(new HashMap());

	private Column[] columns;

	// Rows
	private boolean rowsUpdate;

	private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean ResultSet Rows";
	private final ReferenceObject refRows = ReferenceFactory.newReference(REF_MESSAGE_FULL,
			CollectionsUtil.synchronizedMap(new HashMap()));

	private RowImpl currentRow;

	private Row[] rows;

	private final boolean caseSensitive;

	private final QueryImpl query;

	private TimeInvocation timeInvocationCloseRs;

	public ResultSetCollectorImpl(final QueryImpl query, final ConnectionContextJDBC connectionContext) {
		this.query = query;
		this.caseSensitive = connectionContext.getRdbmsSpecifics().isCaseSensitive();
	}

	public Date getDate() {
		if (query == null) {
			return null;
		}
		return query.getDate();
	}

	public long getExecTime() {
		if (timeInvocationCloseRs == null) {
			return -1;
		}
		final long endDate = timeInvocationCloseRs.getEndDate().getTime();
		final long beginDate = getDate().getTime();
		return endDate - beginDate;
	}

	public String getState() {
		return state;
	}

	public Column[] getColumns() {
		if (!columnsUpdate) {
			final Collection values = columnsDetailPerIndex.values();
			final List list = new ArrayList(values);
			Collections.sort(list);
			final List listColumns = Collections.unmodifiableList(list);
			columns = (Column[]) listColumns.toArray(new Column[listColumns.size()]);
			columnsUpdate = true;
		}
		return columns;
	}

	public Row[] getRows() {
		final Map rowsDetail = (Map) refRows.get();
		if (rowsDetail == null) {
			return null;
		}

		if (!rowsUpdate) {
			final Collection values = rowsDetail.values();
			final List list = new ArrayList(values);
			Collections.sort(list);
			final List listRows = Collections.unmodifiableList(list);
			rows = (Row[]) listRows.toArray(new Row[listRows.size()]);
			rowsUpdate = true;
		}
		return rows;
	}

	public Row getCurrentRow() {
		return currentRow;
	}

	public void setColumnsDetail(final ResultSetMetaData resultSetMetaData) {
		try {
			columnsUpdate = false;
			metaData = true;
			final int columnCount = resultSetMetaData.getColumnCount();
			for (int column = 1; column <= columnCount; column++) {
				final String tableName = getUpperCase(resultSetMetaData.getTableName(column));
				final String name = getUpperCase(resultSetMetaData.getColumnName(column));
				final String label = getUpperCase(resultSetMetaData.getColumnLabel(column));

				final ColumnImpl columnDetail = new ColumnImpl(column, tableName, name, label);
				columnsDetailPerIndex.put(new Integer(column), columnDetail);
				columnsDetailPerLabel.put(label, columnDetail);
				columnsDetailPerLabel.put(tableName + "." + label, columnDetail);
			}
		} catch (final SQLException e) {
			metaDataError = true;
		}
	}

	public RowImpl getRow(final int cursorPosition) {
		if (rs != null && !metaData) {
			try {
				setColumnsDetail(rs.getMetaData());
			} catch (final SQLException e) {
				metaDataError = true;
			}
		}

		final Integer curPosition = new Integer(cursorPosition);

		rowsUpdate = false;

		final Map rowsDetail = (Map) refRows.get();
		RowImpl row = null;

		if (rowsDetail != null) {
			row = (RowImpl) rowsDetail.get(curPosition);
		}

		if (row == null) {
			row = new RowImpl(cursorPosition);
			if (rowsDetail != null) {
				rowsDetail.put(curPosition, row);
			}
		}

		if (currentRow == null || !currentRow.equals(row)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Changement de ligne du ResultSet : Ancien " + currentRow + " - Nouvelle : " + row);
			}
			currentRow = row;
		}

		return currentRow;
	}

	public CellImpl addValueColumn(final int cursorPosition, final Object value, final int columnIndex) {
		if (metaDataError) {
			return null;
		}

		final RowImpl currentRow = getRow(cursorPosition);
		if (currentRow == null) {
			return null;
		}

		final Integer colIndex = new Integer(columnIndex);
		final ColumnImpl columnDetail = (ColumnImpl) columnsDetailPerIndex.get(colIndex);
		if (columnDetail != null) {
			final CellImpl cell = currentRow.addValueColumn(columnDetail, value);
			return cell;
		}
		return null;
	}

	public CellImpl addValueColumn(final int cursorPosition, final Object value, final String columnLabel) {
		if (metaDataError) {
			return null;
		}

		final RowImpl currentRow = getRow(cursorPosition);
		if (currentRow == null) {
			return null;
		}

		final ColumnImpl columnDetail = (ColumnImpl) columnsDetailPerLabel.get(getUpperCase(columnLabel));
		if (columnDetail != null) {
			final CellImpl cell = currentRow.addValueColumn(columnDetail, value);
			return cell;
		}
		return null;
	}

	public boolean isMetaDataError() {
		return metaDataError;
	}

	public QueryImpl close(final TimeInvocation timeInvocationCloseRs) {
		if (state.equals(ResultSetCollector.STATE_OPEN)) {
			this.timeInvocationCloseRs = timeInvocationCloseRs;
			this.state = ResultSetCollector.STATE_CLOSE;
			return query;
		} else if (state.equals(ResultSetCollector.STATE_CLOSE)) {
			this.state = ResultSetCollector.STATE_CLOSED;
		}

		return null;
	}

	private String getUpperCase(String name) {
		if (!caseSensitive) {
			name = name.toUpperCase();
		}
		return name;
	}

	public void setRs(final ResultSet rs) {
		if (this.rs == null) {
			this.rs = rs;
		}
	}

	public String toString() {
		final String nl = System.getProperty("line.separator");

		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker sb = stringFactory.newString();

		sb.append("	State  : " + getState());

		return sb.toString();
	}
}
