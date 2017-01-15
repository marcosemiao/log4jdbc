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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class RowImpl implements Row, Comparable {

	private final int position;

	private final Map values = new HashMap();

	public RowImpl(final int position) {
		this.position = position;
	}

	public CellImpl addValueColumn(final ColumnImpl columnDetail, final Object value) {
		final CellImpl cell = new CellImpl(columnDetail, this, value);

		final Integer index = new Integer(columnDetail.getIndex());
		final String label = columnDetail.getLabel();

		final Object object = values.get(index);

		if (object == null) {
			values.put(index, cell);
			values.put(label, cell);
		}

		return cell;
	}

	public int getPosition() {
		return position;
	}

	public Cell getValue(final int columnIndex) {
		final Integer index = new Integer(columnIndex);
		return (Cell) values.get(index);
	}

	public Cell getValue(final String columnLabel) {
		return (Cell) values.get(columnLabel);
	}

	public int compareTo(final Object obj) {
		final RowImpl o = (RowImpl) obj;
		return this.position - o.position;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position;
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
		final RowImpl other = (RowImpl) obj;
		if (position != other.position) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "RowImpl [position=" + position + ", values=" + values + "]";
	}
}
