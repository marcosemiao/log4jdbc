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
package fr.ms.log4jdbc.message.impl;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.formatter.SQLFormatter;
import fr.ms.log4jdbc.formatter.SQLFormatterFactory;
import fr.ms.log4jdbc.message.AbstractMessage;
import fr.ms.log4jdbc.message.MessageHandler;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.log4jdbc.writer.MessageWriter;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class ResultSetMessage extends AbstractMessage {

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final static String nl = System.getProperty("line.separator");

  private final MessageProcess generic = new GenericMessage();

  private final FormatQuery defaultFormatQuery = DefaultFormatQuery.getInstance();

  public MessageWriter newMessageWriter(final MessageHandler message, final Method method, final Object[] args,
      final Object invoke, final Throwable exception) {

    final boolean resultset = props.logRequeteSelectSQL() && props.logRequeteSelectResultSetSQL() && message != null
        && message.getQuery() != null && message.getQuery().getResultSetCollector() != null
        && message.getQuery().getResultSetCollector().isClosed();
    final boolean allmethod = props.logGenericMessage();

    if (resultset || allmethod) {
      final MessageWriter newMessageWriter = super.newMessageWriter(message, method, args, invoke, exception);

      return newMessageWriter;
    }

    return null;
  }

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Object invoke) {

    final boolean allmethod = props.logGenericMessage();

    if (!allmethod) {
      final Query query = message.getQuery();

      final StringBuffer sb = new StringBuffer();
      sb.append("Query Number : " + query.getQueryNumber() + " - State : " + query.getState());
      sb.append(nl);
      String sql = query.getSQLQuery(defaultFormatQuery);
      if (props.logRequeteFormatSQL()) {
        final SQLFormatter sqlFormatter = SQLFormatterFactory.getInstance();
        sql = sqlFormatter.prettyPrint(sql);
      }
      sb.append(sql);

      if (query.getResultSetCollector() != null && query.getResultSetCollector().isClosed()) {
        messageWriter.setResultSetCollector(query.getResultSetCollector());
      }
      messageWriter.traceMessage(sb.toString());
    } else {
      generic.buildLog(messageWriter, message, method, args, invoke);
    }
  }

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Throwable exception) {
    generic.buildLog(messageWriter, message, method, args, exception);
  }
}
