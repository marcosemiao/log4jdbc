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
package fr.ms.log4jdbc.utils;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class StringUtils {

  private StringUtils() {
  }

  /***
   * Add space to the provided <code>String</code> to match the provided width
   * 
   * @param s the <code>String</code> we want to adjust
   * @param n the width of the returned <code>String</code>
   * @return a <code>String</code> matching the provided width
   */
  public static String padRight(final String s, final int n) {
    final StringBuffer sb = new StringBuffer();

    for (int i = 0; i < n; i++) {
      sb.append(s);
    }

    return sb.toString();
  }

  public static String padRight(final String start, final String s, final int n) {
    return start + padRight(s, n - start.length());
  }

  public static String replaceAll(final String str, final String replace, final String replacement) {
    final StringBuffer sb = new StringBuffer(str);
    int firstOccurrence = sb.toString().indexOf(replace);

    while (firstOccurrence != -1) {
      sb.replace(firstOccurrence, firstOccurrence + replace.length(), replacement);
      final int position = firstOccurrence + replacement.length();
      firstOccurrence = sb.toString().indexOf(replace, position);
    }

    return sb.toString();
  }

  public static String removePart(final String str, final String start, final String end) {
    final int startComment = str.indexOf(start);
    final int endComment = str.indexOf(end);

    if (startComment == -1 || endComment == -1 || startComment >= endComment) {
      return str;
    }

    final String e1 = str.substring(0, startComment);
    final String e2 = str.substring(endComment + 2, str.length());

    final String replace = e1 + e2;

    final String formatSql = removePart(replace, start, end);

    return formatSql;
  }
}
