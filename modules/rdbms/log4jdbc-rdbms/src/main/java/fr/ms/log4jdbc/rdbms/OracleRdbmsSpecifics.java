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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ms.lang.StringUtils;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class OracleRdbmsSpecifics implements RdbmsSpecifics {

	private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

	private final static String TIMESTAMP_PATTERN = "MM/dd/yyyy HH:mm:ss.SSS";

	private final static String DATE_PATTERN = "MM/dd/yyyy HH:mm:ss";

	public boolean isRdbms(final String classType) {
		return classType.startsWith("oracle.jdbc");
	}

	public DataRdbms getData(final Object object) {
		if (object instanceof Timestamp) {
			final DateFormat df = new SimpleDateFormat(TIMESTAMP_PATTERN);
			final String dateString = df.format(object);
			return new GenericDataRdbms("to_timestamp('", dateString, "', 'mm/dd/yyyy hh24:mi:ss.ff3')");
		}

		if (object instanceof Date) {
			final DateFormat df = new SimpleDateFormat(DATE_PATTERN);
			final String dateString = df.format(object);
			return new GenericDataRdbms("to_date('", dateString, "', 'mm/dd/yyyy hh24:mi:ss')");
		}

		return genericRdbms.getData(object);
	}

	public String getTypeQuery(final String sql) {
		return genericRdbms.getTypeQuery(sql);
	}

	public int beginQuery(String sql, int index) {
		return genericRdbms.beginQuery(sql, index);
	}

	public String removeComment(String sql) {
		sql = StringUtils.removePart(sql, "/*", "*/", "/*+");

		if (sql != null) {
			sql = sql.trim();
		}

		return sql;
	}

	public boolean isCaseSensitive() {
		return genericRdbms.isCaseSensitive();
	}
}
