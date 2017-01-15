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

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class CellImpl implements Cell {

	private final Column column;
	private final Row row;
	private Object value;

	public CellImpl(final Column column, final Row row, final Object value) {
		this.column = column;
		this.row = row;
		this.value = value;
	}

	public Column getColumn() {
		return column;
	}

	public Row getRow() {
		return row;
	}

	public Object getValue() {
		return value;
	}

	public void wasNull() {
		this.value = null;
	}

	public String toString() {
		return "CellImpl [column=" + getColumn() + ", value=" + getValue() + "]";
	}
}
