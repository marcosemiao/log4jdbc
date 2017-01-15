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
package fr.ms.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.ms.util.StackTraceOlder;
import fr.ms.util.logging.Logger;
import fr.ms.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TimeInvocationHandler implements InvocationHandler {

	private final static Logger LOG = LoggerManager.getLogger(TimeInvocationHandler.class);

	private final static String nl = System.getProperty("line.separator");

	private final Object implementation;

	public TimeInvocationHandler(final Object implementation) {
		this.implementation = implementation;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final TimeInvocation timeInvoke = new TimeInvocation();
		try {
			Object invoke = null;

			if (invoke == null) {
				final Object[] argsUnProxy = unProxyLog4Jdbc(args);

				invoke = method.invoke(implementation, argsUnProxy);
			}

			if (LOG.isDebugEnabled()) {
				String logMessage = "Method : " + method + " - args Proxy : " + args + " - args  : " + args
						+ " - invoke : " + invoke;

				if (LOG.isTraceEnabled()) {
					LOG.trace(logMessage + nl + StackTraceOlder.returnStackTrace());
				} else {
					LOG.debug(logMessage);
				}
			}

			timeInvoke.setInvoke(invoke);
		} catch (final InvocationTargetException s) {
			final Throwable targetException = s.getTargetException();
			if (targetException == null) {
				throw s;
			}
			if (LOG.isErrorEnabled()) {
				LOG.error("Method : " + method + " - args Proxy : " + args + " - targetException : " + targetException);
			}
			timeInvoke.setTargetException(targetException);
		} finally {
			timeInvoke.finish();
		}

		return timeInvoke;
	}

	private final Object[] unProxyLog4Jdbc(final Object[] args) {
		Object[] dest = args;
		if (dest != null && dest.length > 0) {
			dest = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object obj = args[i];
				if (args[i] != null && Proxy.isProxyClass(args[i].getClass())) {
					final InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[i]);
					if (invocationHandler instanceof ProxyOperationInvocationHandler) {
						final ProxyOperationInvocationHandler handler = (ProxyOperationInvocationHandler) invocationHandler;
						obj = handler.getImplementation();
					}
				}
				dest[i] = obj;
			}
		}

		return dest;
	}

	public String toString() {
		return "TimeInvocationHandler [implementation=" + implementation + "]";
	}
}
