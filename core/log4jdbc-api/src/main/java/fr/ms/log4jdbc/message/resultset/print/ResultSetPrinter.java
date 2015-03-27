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
import fr.ms.log4jdbc.rdbms.DataRdbms;
import fr.ms.log4jdbc.rdbms.GenericDataRdbms;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class ResultSetPrinter {

  public static Iterator printResultSet(final ResultSetCollector resultSetCollector, final int maxRows) {
    final RdbmsSpecifics rdbms = new RdbmsSpecifics() {

      public boolean isRdbms(final String classType) {
        return true;
      }

      public DataRdbms getData(final Object object) {
        if (object == null) {
          return new GenericDataRdbms("NULL");
        }
        return new GenericDataRdbms(object.toString());
      }

      public boolean isCaseSensitive() {
        return false;
      }

      public String removeComment(final String sql) {
        return sql;
      }

      public String getTypeQuery(final String sql) {
        return sql;
      }
    };

    return printResultSet(resultSetCollector, rdbms, maxRows);
  }

  public static Iterator printResultSet(final ResultSetCollector resultSetCollector, final RdbmsSpecifics rdbms,
      final int maxRows) {
    if (rdbms == null) {
      return printResultSet(resultSetCollector, maxRows);
    } else {
      return new ResultSetPrinterIterator(resultSetCollector, rdbms, maxRows);
    }
  }
}
