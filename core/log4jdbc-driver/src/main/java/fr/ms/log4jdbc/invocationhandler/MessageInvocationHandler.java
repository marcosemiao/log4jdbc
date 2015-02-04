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
package fr.ms.log4jdbc.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import fr.ms.log4jdbc.MessageLogger;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.message.MessageHandlerImpl;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class MessageInvocationHandler implements InvocationHandler {

  private final InvocationHandler invocationHandler;

  private final JdbcContext jdbcContext;

  private final MessageLogger[] logs;

  private final MessageFactory messageFactory;

  private final boolean timeInvocationResult;

  public MessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory) {
    this(implementation, jdbcContext, logs, messageFactory, false);
  }

  public MessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory, final boolean timeInvocationResult) {
    this.invocationHandler = new TimeInvocationHandler(implementation);
    this.jdbcContext = jdbcContext;
    this.logs = logs;
    this.messageFactory = messageFactory;
    this.timeInvocationResult = timeInvocationResult;
  }

  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final TimeInvocation invokeTime = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

    final Object invoke = invokeTime.getInvoke();
    final Throwable targetException = invokeTime.getTargetException();

    MessageHandlerImpl message = null;
    Object wrap = null;

    if (logs != null && logs.length != 0) {
      for (int i = 0; i < logs.length; i++) {
        final MessageLogger log = logs[i];
        if (log != null && log.isEnabled()) {
          if (message == null) {
            message = messageFactory.transformMessage(proxy, method, args, invokeTime, jdbcContext, message);
            wrap = messageFactory.wrap(invoke, args, jdbcContext, message);
          }
          try {
            if (targetException == null) {
              log.buildLog(message, method, args, invoke);
            } else {
              log.buildLog(message, method, args, targetException);
            }
          } catch (final Throwable t) {
            t.printStackTrace();
          }
        }
      }
    }

    if (targetException != null) {
      throw targetException;
    }

    if (wrap == null) {
      wrap = messageFactory.wrap(invoke, args, jdbcContext, message);
    }

    if (timeInvocationResult) {
      invokeTime.setInvoke(wrap);
      return invokeTime;
    }
    return wrap;
  }
}
