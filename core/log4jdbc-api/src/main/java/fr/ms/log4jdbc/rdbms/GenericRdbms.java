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
public class GenericRdbms implements RdbmsSpecifics {

  private final static RdbmsSpecifics instance = new GenericRdbms();

  private final static String dateFormat = "MM/dd/yyyy HH:mm:ss.SSS";

  private final boolean caseSensitive = getProperty("log4jdbc.rdms.caseSensitive", false);

  // Constructeur Privé
  private GenericRdbms() {
  }

  public boolean isRdbms(final String classType) {
    return true;
  }

  public String formatSql(String sql) {
    sql = removeComment(sql).trim();
    if (!sql.endsWith(";")) {
      sql = sql + ";";
    }
    return sql;
  }

  private static String removeComment(final String sql) {
    final int startComment = sql.indexOf("/*");
    final int endComment = sql.indexOf("*/");

    if (startComment == -1 || endComment == -1 || startComment >= endComment) {
      return sql;
    }

    final String e1 = sql.substring(0, startComment);
    final String e2 = sql.substring(endComment + 2, sql.length());

    final String replace = e1 + e2;

    final String formatSql = removeComment(replace);

    return formatSql;
  }

  public String formatParameter(final Object object) {
    if (object == null) {
      return "NULL";
    }

    if (object instanceof String) {
      return "'" + escapeString((String) object) + "'";
    } else if (object instanceof Date) {
      return "'" + new SimpleDateFormat(dateFormat).format(object) + "'";
    } else if (object instanceof Boolean) {
      return ((Boolean) object).booleanValue() ? "1" : "0";
    } else {
      return object.toString();
    }
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

  public String getTypeQuery(String sql) {
    return sql.substring(0, 6).toLowerCase();
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public static RdbmsSpecifics getInstance() {
    return instance;
  }

  private boolean getProperty(final String key, final boolean defaultValue) {

    final String property = System.getProperty(key, new Boolean(defaultValue).toString());

    return Boolean.valueOf(property).booleanValue();
  }
}
