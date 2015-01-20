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
public class WrapperMessageInvocationHandler implements InvocationHandler {

  private final InvocationHandler invocationHandler;

  public WrapperMessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory) {
    this(implementation, jdbcContext, logs, messageFactory, false);
  }

  public WrapperMessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory, final boolean timeInvocationResult) {
    final MessageFactory wrapper = new WrapperMessageFactory(messageFactory);
    invocationHandler = new MessageInvocationHandler(implementation, jdbcContext, logs, wrapper, timeInvocationResult);
  }

  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    return invocationHandler.invoke(proxy, method, args);
  }

  private final static class WrapperMessageFactory implements MessageFactory {

    private final MessageFactory messageFactory;

    WrapperMessageFactory(final MessageFactory messageFactory) {
      this.messageFactory = messageFactory;
    }

    public MessageHandlerImpl transformMessage(final Object proxy, final Method method, final Object[] args,
        final TimeInvocation timeInvocation, final JdbcContext jdbcContext, MessageHandlerImpl message) {
      message = new MessageHandlerImpl(timeInvocation, jdbcContext);

      message = messageFactory.transformMessage(proxy, method, args, timeInvocation, jdbcContext, message);

      return message;
    }

    public Object wrap(final Object invoke, final Object[] args, final JdbcContext jdbcContext,
        final MessageHandlerImpl message) {
      return messageFactory.wrap(invoke, args, jdbcContext, message);
    }
  }
}
