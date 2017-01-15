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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class MySqlRdbmsSpecifics implements RdbmsSpecifics {

	private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

	private final static String TIME_PATTERN = "HH:mm:ss";

	private final static String SQL_DATE_PATTERN = "yyyy-MM-dd";

	private final static String UTIL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public boolean isRdbms(final String classType) {
		return classType.startsWith("com.mysql");
	}

	public DataRdbms getData(final Object object) {
		if (object instanceof java.sql.Time) {
			final DateFormat df = new SimpleDateFormat(TIME_PATTERN);
			final String dateString = df.format(object);
			return new GenericDataRdbms(dateString, "'");
		}

		if (object instanceof java.sql.Date) {
			final DateFormat df = new SimpleDateFormat(SQL_DATE_PATTERN);
			final String dateString = df.format(object);
			return new GenericDataRdbms(dateString, "'");
		}

		if (object instanceof java.util.Date) { // (includes java.sql.Timestamp)
			final DateFormat df = new SimpleDateFormat(UTIL_DATE_PATTERN);
			final String dateString = df.format(object);
			return new GenericDataRdbms(dateString, "'");
		}

		return genericRdbms.getData(object);
	}

	public String getTypeQuery(final String sql) {
		return genericRdbms.getTypeQuery(sql);
	}

	public int beginQuery(String sql, int index) {
		return genericRdbms.beginQuery(sql, index);
	}

	public String removeComment(final String sql) {
		return genericRdbms.removeComment(sql);
	}

	public boolean isCaseSensitive() {
		return genericRdbms.isCaseSensitive();
	}
}
