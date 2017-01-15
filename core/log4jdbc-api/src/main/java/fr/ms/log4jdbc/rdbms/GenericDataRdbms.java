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
package fr.ms.log4jdbc.rdbms;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class GenericDataRdbms implements DataRdbms {

	private final String value;

	private String parameter;

	private String prefixe;

	private String suffixe;

	public GenericDataRdbms(final String value) {
		this.value = value;
	}

	public GenericDataRdbms(final String value, final String parameter) {
		this(value);
		this.parameter = parameter;
	}

	public GenericDataRdbms(final String prefixe, final String value, final String suffixe) {
		this(value);
		this.prefixe = prefixe;
		this.suffixe = suffixe;
	}

	public String getValue() {
		return value;
	}

	public String getParameter() {
		if (parameter != null) {
			return parameter + getValue() + parameter;
		}
		if (prefixe != null && suffixe != null) {
			return prefixe + getValue() + suffixe;
		}

		return getValue();
	}

	public String toString() {
		return "GenericDataRdbms [value=" + getValue() + ", parameter=" + getParameter() + "]";
	}
}
