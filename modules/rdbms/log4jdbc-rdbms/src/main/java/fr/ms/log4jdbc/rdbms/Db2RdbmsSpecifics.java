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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Db2RdbmsSpecifics implements RdbmsSpecifics {

	private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

	private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss'.'";

	private final static String DATE_PATTERN_MILLISECONDS = "yyyy-MM-dd HH:mm:ss.SSS";

	public boolean isRdbms(final String classType) {
		return classType.startsWith("com.ibm.db2") || classType.startsWith("COM.ibm.db2");
	}

	public DataRdbms getData(final Object object) {
		if (object instanceof Timestamp) {
			final Timestamp timestamp = (Timestamp) object;
			final DateFormat df = new SimpleDateFormat(DATE_PATTERN);
			final NumberFormat nf = new DecimalFormat("000000000");
			final String dateString = df.format(timestamp) + nf.format(timestamp.getNanos());
			return new GenericDataRdbms(dateString, "'");
		} else if (object instanceof Date) {
			final DateFormat df = new SimpleDateFormat(DATE_PATTERN_MILLISECONDS);
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
