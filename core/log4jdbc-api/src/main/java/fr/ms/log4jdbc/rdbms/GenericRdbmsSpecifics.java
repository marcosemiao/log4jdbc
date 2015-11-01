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

import fr.ms.lang.StringUtils;
import fr.ms.lang.SystemPropertyUtils;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class GenericRdbmsSpecifics implements RdbmsSpecifics {

    private final static RdbmsSpecifics instance = new GenericRdbmsSpecifics();

    private final static String DATE_PATTERN = "MM/dd/yyyy HH:mm:ss'.'";

    private final static String DATE_PATTERN_MILLISECONDS = "MM/dd/yyyy HH:mm:ss.SSS";

    private final boolean caseSensitive = SystemPropertyUtils.getProperty("log4jdbc.rdms.caseSensitive", false);

    private GenericRdbmsSpecifics() {
    }

    public static RdbmsSpecifics getInstance() {
	return instance;
    }

    public boolean isRdbms(final String classType) {
	return true;
    }

    public DataRdbms getData(final Object object) {
	if (object == null) {
	    return new GenericDataRdbms("NULL");
	}

	if (object instanceof String) {
	    final String s = (String) object;
	    return new EscapeStringDataRdbms(s, "'");
	} else if (object instanceof Timestamp) {
	    final Timestamp timestamp = (Timestamp) object;
	    final DateFormat df = new SimpleDateFormat(DATE_PATTERN);
	    final NumberFormat nf = new DecimalFormat("000000000");
	    final String dateString = df.format(timestamp) + nf.format(timestamp.getNanos());
	    return new GenericDataRdbms(dateString, "'");
	} else if (object instanceof Date) {
	    final Date date = (Date) object;
	    final DateFormat df = new SimpleDateFormat(DATE_PATTERN_MILLISECONDS);
	    return new GenericDataRdbms(df.format(date), "'");
	} else if (object instanceof Boolean) {
	    return new GenericDataRdbms(((Boolean) object).booleanValue() ? "1" : "0", "'");
	} else {
	    return new GenericDataRdbms(object.toString());
	}
    }

    public String getTypeQuery(String sql) {
	sql = removeComment(sql);
	if (sql == null) {
	    return null;
	}
	sql = sql.trim();
	if (sql.length() < 6) {
	    return null;
	}
	return sql.substring(0, 6).toLowerCase();
    }

    public String removeComment(final String sql) {
	return StringUtils.removePart(sql, "/*", "*/");
    }

    public boolean isCaseSensitive() {
	return caseSensitive;
    }

    private final static class EscapeStringDataRdbms implements DataRdbms {
	private final String value;

	private final String parameter;

	public EscapeStringDataRdbms(final String value, final String parameter) {
	    this.value = value;
	    this.parameter = parameter + escapeString(value) + parameter;
	}

	public String getValue() {
	    return value;
	}

	public String getParameter() {
	    return parameter;
	}

	public String toString() {
	    return "EscapeStringDataRdbms [value=" + getValue() + ", parameter=" + getParameter() + "]";
	}

	private static String escapeString(final String in) {
	    String out = "";
	    for (int i = 0, j = in.length(); i < j; i++) {
		final char c = in.charAt(i);
		if (c == '\'') {
		    out = out + c;
		}
		out = out + c;
	    }
	    return out.toString();
	}
    }
}
