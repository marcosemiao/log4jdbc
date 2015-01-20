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

import java.util.Iterator;

import fr.ms.log4jdbc.message.resultset.ResultSetCollector;
import fr.ms.log4jdbc.message.resultset.Row;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
class ResultSetPrinterIterator implements Iterator {

  private final static String nl = System.getProperty("line.separator");

  private final ResultSetCollector resultSetCollector;
  private final RdbmsSpecifics rdbms;

  private int maxRow;

  private int[] maxLength;

  private boolean header;

  private boolean allData;

  private boolean next = true;

  private int position = 0;

  private boolean nextFull;

  public ResultSetPrinterIterator(final ResultSetCollector resultSetCollector, final RdbmsSpecifics rdbms,
      final int maxRow) {
    this.resultSetCollector = resultSetCollector;
    this.rdbms = rdbms;
    this.maxRow = maxRow;

    if (resultSetCollector != null && resultSetCollector.isClosed() && resultSetCollector.getRows() != null
        && resultSetCollector.getRows().length != 0) {
      maxLength = ResultSetPrinterHelper.getMaxLength(resultSetCollector);

      if (maxRow > resultSetCollector.getRows().length) {
        nextFull = true;
      } else {
        nextFull = false;
      }
    } else {
      next = false;
    }
  }

  public boolean hasNext() {
    return next;
  }

  public Object next() {
    if (nextFull) {
      return nextFull();
    } else {
      return nextItr();
    }
  }

  private Object nextFull() {
    final StringBuffer sb = new StringBuffer();

    sb.append(ResultSetPrinterHelper.getHeader(maxLength, resultSetCollector));
    sb.append(nl);
    sb.append(ResultSetPrinterHelper.getData(maxLength, resultSetCollector, position,
        resultSetCollector.getRows().length, rdbms));
    sb.append(nl);
    sb.append(ResultSetPrinterHelper.getFooter(maxLength, resultSetCollector));

    next = false;

    return sb.toString();
  }

  private Object nextItr() {
    if (!header) {
      header = true;
      return ResultSetPrinterHelper.getHeader(maxLength, resultSetCollector);
    }

    final Row[] rows = resultSetCollector.getRows();

    if (rows != null && !allData) {

      final int maxElement = rows.length;
      if (position + maxRow > maxElement) {
        maxRow = maxElement - position;
        allData = true;
      }

      final String data = ResultSetPrinterHelper.getData(maxLength, resultSetCollector, position, maxRow, rdbms);

      position = position + maxRow;
      return data;
    }

    next = false;
    return ResultSetPrinterHelper.getFooter(maxLength, resultSetCollector);
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
