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

import fr.ms.lang.SystemPropertyUtils;
import fr.ms.lang.reflect.TraceTimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.invocationhandler.WrapperMessageInvocationHandler;
import fr.ms.log4jdbc.operator.OperationDecorator;

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

    static final InvocationHandler create(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	if (devMode) {
	    final InvocationHandler wrapper = createDev(implementation, connectionContext, logs, messageFactory);

	    return wrapper;
	} else {
	    final InvocationHandler wrapper = createProd(implementation, connectionContext, logs, messageFactory);

	    return wrapper;
	}
    }

    private static final InvocationHandler createDev(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	final InvocationHandler ih = new WrapperMessageInvocationHandler(implementation, connectionContext, logs, messageFactory, true);

	final InvocationHandler wrapper = new TraceTimeInvocationHandler(ih);

	return wrapper;
    }

    private static final InvocationHandler createProd(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final OperationDecorator messageFactory) {
	final InvocationHandler wrapper = new WrapperMessageInvocationHandler(implementation, connectionContext, logs, messageFactory);

	return wrapper;
    }
}
