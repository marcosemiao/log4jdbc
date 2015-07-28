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
package fr.ms.log4jdbc.message.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ms.log4jdbc.context.JdbcContext;
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
public class ResultSetCollectorImpl implements ResultSetCollector {

    private boolean metaDataError = false;
    private boolean metaData = false;

    private boolean close = false;

    private ResultSet rs;

    // Columns
    private boolean columnsUpdate;

    private final Map columnsDetailPerIndex = new HashMap();
    private final Map columnsDetailPerLabel = new HashMap();

    private Column[] columns;

    // Rows
    private boolean rowsUpdate;

    private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean ResultSet Rows";
    private final ReferenceObject refRows = ReferenceFactory.newReference(REF_MESSAGE_FULL, new HashMap());

    private RowImpl currentRow;

    private Row[] rows;

    private final boolean caseSensitive;

    public ResultSetCollectorImpl(final JdbcContext jdbcContext) {
	this.caseSensitive = jdbcContext.getRdbmsSpecifics().isCaseSensitive();
    }

    public boolean isClosed() {
	return close;
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
	return null;
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

    private RowImpl getRow(final int cursorPosition) {
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
	if (rowsDetail == null) {
	    return null;
	}
	RowImpl row = (RowImpl) rowsDetail.get(curPosition);
	if (row == null) {
	    row = new RowImpl(cursorPosition);
	    rowsDetail.put(curPosition, row);
	}

	return row;
    }

    public CellImpl addValueColumn(final int cursorPosition, final Object value, final int columnIndex) {
	if (metaDataError) {
	    return null;
	}
	currentRow = getRow(cursorPosition);
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
	currentRow = getRow(cursorPosition);
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

    public void close() {
	close = true;
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
}
