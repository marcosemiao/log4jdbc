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

import fr.ms.log4jdbc.message.AbstractMessage;
import fr.ms.log4jdbc.message.MessageHandler;
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
public class GenericMessage extends AbstractMessage {

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final static String nl = System.getProperty("line.separator");

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Object invoke) {
    final String name = method.getName();
    final String declaringClass = method.getDeclaringClass().getName();

    final String genericMessage = getMethodCall(declaringClass + "." + name, args);
    messageWriter.traceMessage(genericMessage);
  }

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Throwable exception) {
    final String name = method.getName();
    final String declaringClass = method.getDeclaringClass().getName();

    String genericMessage = "";

    if (props.logRequeteException()) {
      final Query query = message != null ? message.getQuery() : null;
      if (query != null) {
        final String sql = query.getSQLQuery();
        genericMessage = "Requete SQL : " + sql + nl;
      }
    }
    genericMessage = genericMessage + getMethodCall(declaringClass + "." + name, args);
    genericMessage = genericMessage + " - Exception : " + exception;

    messageWriter.traceMessage(genericMessage);
  }

  public static String getMethodCall(final String methodName, final Object[] args) {
    final StringBuffer sb = new StringBuffer();
    sb.append(methodName);
    sb.append("(");

    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        final Object arg = args[i];
        sb.append(arg);
        if (i < args.length - 1) {
          sb.append(",");
        }
      }
    }
    sb.append(");");

    return sb.toString();
  }
}
