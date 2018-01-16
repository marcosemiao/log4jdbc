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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.ms.log4jdbc.rdbms.WrapObject;
import fr.ms.util.CollectionsUtil;
import fr.ms.util.Service;
import fr.ms.util.StackTraceHolder;
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
public class WrapInvocationHandler implements InvocationHandler {

	private final static Logger LOG = LoggerManager.getLogger(WrapInvocationHandler.class);

	private final static String nl = System.getProperty("line.separator");

	private final static Map wrapMap = new HashMap();
	private final Object implementation;

	static {
		final Iterator providers = Service.providers(WrapObject.class, WrapInvocationHandler.class.getClassLoader());

		final List list = CollectionsUtil.convert(providers);

		for (int i = 0; i < list.size(); i++) {
			WrapObject wrap = (WrapObject) list.get(i);
			
			wrapMap.put(wrap.type(), wrap);
		}

	}

	public WrapInvocationHandler(final Object implementation) {
		this.implementation = implementation;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class[] parameterTypes = method.getParameterTypes();
		Class returnType = method.getReturnType();

		Object invoke = null;
		Throwable targetException = null;
		try {
			args = unProxyLog4Jdbc(args);

			args = wrapObject(parameterTypes, args);

			invoke = method.invoke(implementation, args);

			invoke = wrapObject(returnType, invoke);

			if (LOG.isDebugEnabled()) {
				String logMessage = "Method : " + method + " - args Proxy : " + args + " - args  : " + args
						+ " - invoke : " + invoke;

				if (LOG.isTraceEnabled()) {
					LOG.trace(logMessage + nl + StackTraceHolder.returnStackTrace());
				} else {
					LOG.debug(logMessage);
				}
			}
		} catch (final InvocationTargetException s) {
			targetException = s.getTargetException();
			if (targetException == null) {
				throw s;
			}
			if (LOG.isErrorEnabled()) {
				LOG.error("Method : " + method + " - args Proxy : " + args + " - targetException : " + targetException);
			}
		}

		final WrapInvocation wrapInvocation = new WrapInvocation(args, invoke, targetException);
		return wrapInvocation;
	}

	private static Object[] wrapObject(Class[] clazz, Object[] args) {
		if (args!=null && args.length>0)
		{
			for (int i = 0; i < clazz.length; i++) {
				Class classe = clazz[i];
				Object obj = args[i];
				
				args[i] = wrapObject(classe,obj);
			}
		}
		return args;
	}

	private static Object wrapObject(Class clazz, Object arg) {
		if (arg!=null)
		{
			WrapObject wrap = (WrapObject) wrapMap.get(clazz);
			if (wrap!=null)
			{
				Object wrapObject = wrap.wrap(arg);
				arg = wrapObject;
			}
		}
		return arg;
	}

	private static final Object[] unProxyLog4Jdbc(final Object[] args) {
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
}