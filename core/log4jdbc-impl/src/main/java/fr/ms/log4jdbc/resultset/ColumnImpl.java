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
public class ColumnImpl implements Column, Comparable {

	private final int index;
	private final String table;
	private final String name;
	private final String label;

	public ColumnImpl(final int index, final String table, final String name, final String label) {
		this.index = index;
		this.table = table;
		this.name = name;
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public String getTable() {
		return table;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		final ColumnImpl other = (ColumnImpl) obj;
		if (index != other.index) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (table == null) {
			if (other.table != null) {
				return false;
			}
		} else if (!table.equals(other.table)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "ColumnImpl [index=" + index + ", table=" + table + ", name=" + name + ", label=" + label + "]";
	}

	public int compareTo(final Object obj) {
		final ColumnImpl o = (ColumnImpl) obj;
		return this.index - o.index;
	}
}
