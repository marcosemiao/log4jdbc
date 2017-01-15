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
package fr.ms.log4jdbc.writer.resultset;

import java.util.Iterator;

import fr.ms.lang.StringUtils;
import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.resultset.Cell;
import fr.ms.log4jdbc.resultset.Column;
import fr.ms.log4jdbc.resultset.ResultSetCollector;
import fr.ms.log4jdbc.resultset.Row;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ResultSetPrinterIterator implements Iterator {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final static String nl = System.getProperty("line.separator");

	private final ResultSetCollector resultSetCollector;

	private final ResultSetPrinterFormatCell formatCell;

	private int maxRow;

	private int[] maxLength;

	private boolean header;

	private boolean allData;

	private boolean next = true;

	private int position = 0;

	private boolean nextFull;

	public ResultSetPrinterIterator(final ResultSetCollector resultSetCollector,
			final ResultSetPrinterFormatCell formatCell, final int maxRow) {
		this.resultSetCollector = resultSetCollector;
		this.formatCell = formatCell;
		this.maxRow = maxRow;

		final Row[] rows = resultSetCollector != null && resultSetCollector.isClosed() ? resultSetCollector.getRows()
				: null;

		if (rows != null && rows.length != 0) {
			maxLength = getMaxLength();

			if (maxRow > rows.length) {
				nextFull = true;
			} else {
				nextFull = false;
			}
		} else {
			next = false;
		}
	}

	public boolean hasNext() {
		return next;
	}

	public Object next() {
		if (nextFull) {
			return nextFull();
		} else {
			return nextItr();
		}
	}

	private Object nextFull() {
		final StringMaker sb = stringFactory.newString();

		sb.append(getHeader());
		sb.append(nl);
		final Row[] rows = resultSetCollector.getRows();
		if (rows != null) {
			sb.append(getData(position, rows.length));
		}
		sb.append(nl);
		sb.append(getFooter());

		next = false;

		return sb.toString();
	}

	private Object nextItr() {
		if (!header) {
			header = true;
			return getHeader();
		}

		final Row[] rows = resultSetCollector.getRows();

		if (rows != null && !allData) {

			final int maxElement = rows.length;
			if (position + maxRow > maxElement) {
				maxRow = maxElement - position;
				allData = true;
			}

			final String data = getData(position, maxRow);

			position = position + maxRow;
			return data;
		}

		next = false;
		return getFooter();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	private int[] getMaxLength() {
		final Column[] columnsDetail = resultSetCollector.getColumns();
		final Row[] rowsDetail = resultSetCollector.getRows();

		final int columnCount = columnsDetail.length;
		final int maxLength[] = new int[columnCount];

		for (int column = 1; column <= columnCount; column++) {
			final Column columnDetail = columnsDetail[column - 1];
			maxLength[column - 1] = columnDetail.getLabel().length();
		}

		if (rowsDetail != null && rowsDetail.length != 0) {
			for (int i = 0; i < rowsDetail.length; i++) {

				for (int column = 1; column <= columnCount; column++) {
					final Cell cell = rowsDetail[i].getValue(column);
					final Cell cellDecorator = new CellDecorator(cell);

					final Object obj = cellDecorator.getValue();
					final String value = formatCell.formatValue(obj);
					if (value != null) {
						final int length = value.length();
						if (length > maxLength[column - 1]) {
							maxLength[column - 1] = length;
						}
					}

				}
			}
		}

		if (columnsDetail != null && columnsDetail.length != 0 && rowsDetail != null && rowsDetail.length != 0) {
			for (int column = 1; column <= columnCount; column++) {
				maxLength[column - 1] = maxLength[column - 1] + 1;
			}
		}

		return maxLength;
	}

	private String getHeader() {

		final Column[] columnsDetail = resultSetCollector.getColumns();
		final int columnCount = columnsDetail.length;

		final StringMaker sb = stringFactory.newString();

		sb.append(System.getProperty("line.separator"));
		sb.append("|");
		for (int column = 1; column <= columnCount; column++) {
			sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
		}

		sb.append(System.getProperty("line.separator"));
		sb.append("|");
		for (int column = 1; column <= columnCount; column++) {
			final Column columnDetail = columnsDetail[column - 1];
			sb.append(StringUtils.padRight(columnDetail.getLabel(), " ", maxLength[column - 1]) + "|");
		}

		sb.append(System.getProperty("line.separator"));
		sb.append("|");
		for (int column = 1; column <= columnCount; column++) {
			sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
		}

		return sb.toString();
	}

	private String getData(final int position, final int length) {

		final Row[] rowsDetail = resultSetCollector.getRows();
		final Column[] columnsDetail = resultSetCollector.getColumns();
		final int columnCount = columnsDetail.length;

		final StringMaker sb = stringFactory.newString();

		if (rowsDetail != null && rowsDetail.length != 0) {
			for (int i = position; i < position + length; i++) {
				int colIndex = 0;
				sb.append("|");
				for (int column = 1; column <= columnCount; column++) {
					final Cell cell = rowsDetail[i].getValue(column);
					final Cell cellDecorator = new CellDecorator(cell);

					final Object obj = cellDecorator.getValue();
					final String value = formatCell.formatValue(obj);

					sb.append(StringUtils.padRight(value, " ", maxLength[colIndex]) + "|");
					colIndex++;
				}
				if (i < position + length - 1) {
					sb.append(System.getProperty("line.separator"));
				}
			}
		}
		return sb.toString();
	}

	private String getFooter() {

		final Column[] columnsDetail = resultSetCollector.getColumns();
		final int columnCount = columnsDetail.length;

		final StringMaker sb = stringFactory.newString();

		sb.append("|");
		for (int column = 1; column <= columnCount; column++) {
			sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
		}

		sb.append(System.getProperty("line.separator"));

		return sb.toString();
	}

	private final static class CellDecorator implements Cell {

		private final static String UNREAD = "LOG4JDBC-UNREAD";

		private final Cell cell;

		public CellDecorator(final Cell cell) {
			this.cell = cell;
		}

		public Column getColumn() {
			if (cell == null) {
				return null;
			} else {
				return cell.getColumn();
			}
		}

		public Row getRow() {
			if (cell == null) {
				return null;
			} else {
				return cell.getRow();
			}
		}

		public Object getValue() {
			if (cell == null) {
				return UNREAD;
			} else {
				return cell.getValue();
			}
		}
	}
}
