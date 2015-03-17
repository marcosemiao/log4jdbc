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
package fr.ms.log4jdbc.message.resultset.print;

import fr.ms.log4jdbc.message.resultset.Cell;
import fr.ms.log4jdbc.message.resultset.Column;
import fr.ms.log4jdbc.message.resultset.ResultSetCollector;
import fr.ms.log4jdbc.message.resultset.Row;
import fr.ms.log4jdbc.rdbms.DataRdbms;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.utils.StringUtils;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
final class ResultSetPrinterHelper {

  static String getHeader(final int maxLength[], final ResultSetCollector resultSetCollector) {

    final Column[] columnsDetail = resultSetCollector.getColumns();
    final int columnCount = columnsDetail.length;

    final StringBuffer sb = new StringBuffer();

    sb.append(System.getProperty("line.separator"));
    sb.append("|");
    for (int column = 1; column <= columnCount; column++) {
      sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
    }

    sb.append(System.getProperty("line.separator"));
    sb.append("|");
    for (int column = 1; column <= columnCount; column++) {
      final Column columnDetail = columnsDetail[column - 1];
      sb.append(StringUtils.padRight(columnDetail.getLabel(), " ", maxLength[column - 1]) + "|");
    }

    sb.append(System.getProperty("line.separator"));
    sb.append("|");
    for (int column = 1; column <= columnCount; column++) {
      sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
    }

    return sb.toString();
  }

  static String getData(final int maxLength[], final ResultSetCollector resultSetCollector, final int position,
      final int length, final RdbmsSpecifics rdbms) {

    final Row[] rowsDetail = resultSetCollector.getRows();
    final Column[] columnsDetail = resultSetCollector.getColumns();
    final int columnCount = columnsDetail.length;

    final StringBuffer sb = new StringBuffer();

    if (rowsDetail != null && rowsDetail.length != 0) {
      for (int i = position; i < position + length; i++) {
        int colIndex = 0;
        sb.append("|");
        for (int column = 1; column <= columnCount; column++) {
          final Cell cell = rowsDetail[i].getValue(column);

          String value;
          if (cell == null) {
            value = "UNREAD";
          } else {
            final DataRdbms data = rdbms.getData(cell.getValue());
            value = data.getValue();
          }
          sb.append(StringUtils.padRight(value, " ", maxLength[colIndex]) + "|");
          colIndex++;
        }
        if (i < position + length - 1) {
          sb.append(System.getProperty("line.separator"));
        }
      }
    }
    return sb.toString();
  }

  static String getFooter(final int maxLength[], final ResultSetCollector resultSetCollector) {

    final Column[] columnsDetail = resultSetCollector.getColumns();
    final int columnCount = columnsDetail.length;

    final StringBuffer sb = new StringBuffer();

    sb.append("|");
    for (int column = 1; column <= columnCount; column++) {
      sb.append(StringUtils.padRight("-", maxLength[column - 1]) + "|");
    }

    sb.append(System.getProperty("line.separator"));

    return sb.toString();
  }

  static int[] getMaxLength(final ResultSetCollector resultSetCollector) {
    final Column[] columnsDetail = resultSetCollector.getColumns();
    final Row[] rowsDetail = resultSetCollector.getRows();

    final int columnCount = columnsDetail.length;
    final int maxLength[] = new int[columnCount];

    for (int column = 1; column <= columnCount; column++) {
      final Column columnDetail = columnsDetail[column - 1];
      maxLength[column - 1] = columnDetail.getLabel().length();
    }

    if (rowsDetail != null && rowsDetail.length != 0) {
      for (int i = 0; i < rowsDetail.length; i++) {

        for (int column = 1; column <= columnCount; column++) {
          final Cell cell = rowsDetail[i].getValue(column);
          if (cell != null && cell.getValue() != null) {

            final int length = cell.getValue().toString().length();
            if (length > maxLength[column - 1]) {
              maxLength[column - 1] = length;
            }
          }
        }
      }
    }

    if (columnsDetail != null && columnsDetail.length != 0 && rowsDetail != null && rowsDetail.length != 0) {
      for (int column = 1; column <= columnCount; column++) {
        maxLength[column - 1] = maxLength[column - 1] + 1;
      }
    }

    return maxLength;
  }
}
