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
package fr.ms.log4jdbc.proxy;

import java.lang.reflect.InvocationHandler;

import fr.ms.log4jdbc.MessageLogger;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.invocationhandler.DevMessageInvocationHandler;
import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.invocationhandler.WrapperMessageInvocationHandler;
import fr.ms.log4jdbc.utils.SystemPropertyUtils;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
class CreateInvocationHandler {

  private final static boolean devMode = SystemPropertyUtils.getProperty("log4jdbc.devMode", false);

  static final InvocationHandler create(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory) {
    if (devMode) {
      final InvocationHandler wrapper = createDev(implementation, jdbcContext, logs, messageFactory);

      return wrapper;
    } else {
      final InvocationHandler wrapper = createProd(implementation, jdbcContext, logs, messageFactory);

      return wrapper;
    }
  }

  private static final InvocationHandler createDev(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory) {
    final InvocationHandler ih = new WrapperMessageInvocationHandler(implementation, jdbcContext, logs, messageFactory,
        true);

    final InvocationHandler wrapper = new DevMessageInvocationHandler(ih);

    return wrapper;
  }

  private static final InvocationHandler createProd(final Object implementation, final JdbcContext jdbcContext,
      final MessageLogger[] logs, final MessageFactory messageFactory) {
    final InvocationHandler wrapper = new WrapperMessageInvocationHandler(implementation, jdbcContext, logs,
        messageFactory);

    return wrapper;
  }
}
