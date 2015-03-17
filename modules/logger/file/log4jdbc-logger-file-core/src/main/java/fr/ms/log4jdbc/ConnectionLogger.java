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
package fr.ms.log4jdbc;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.message.MessageHandler;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.message.impl.ConnectionMessage;
import fr.ms.log4jdbc.thread.ThreadMessageProcess;
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
public class ConnectionLogger implements MessageLogger {

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final MessageProcess messageProcess = new ConnectionMessage();

  public boolean isLogger(final String typeLogger) {
    return MessageLogger.CONNECTION.equals(typeLogger);
  }

  public boolean isEnabled() {
    return props.logEnabled() && (props.logConnection() || props.logGenericMessage());
  }

  public void buildLog(final MessageHandler message, final Method method, final Object[] args, final Object invoke) {
    MessageProcess wrapper = messageProcess;

    if (props.logProcessThread()) {
      wrapper = new ThreadMessageProcess(messageProcess);
    }

    final MessageWriter newMessageWriter = wrapper.newMessageWriter(message, method, args, invoke, null);

    if (newMessageWriter != null) {
      wrapper.buildLog(newMessageWriter, message, method, args, invoke);
    }
  }

  public void buildLog(final MessageHandler message, final Method method, final Object[] args, final Throwable exception) {
    MessageProcess wrapper = messageProcess;

    if (props.logProcessThread()) {
      wrapper = new ThreadMessageProcess(messageProcess);
    }

    final MessageWriter newMessageWriter = wrapper.newMessageWriter(message, method, args, null, exception);

    if (newMessageWriter != null) {
      wrapper.buildLog(newMessageWriter, message, method, args, exception);
    }
  }
}
