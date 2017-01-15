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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import fr.ms.lang.ClassUtils;
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
public class ImplementationDecorator implements InvocationHandler {

	private final static Logger LOG = LoggerManager.getLogger(ImplementationDecorator.class);

	private final Object impl;

	private final ImplementationProxy ip;

	public ImplementationDecorator(final Object impl, final ImplementationProxy ip) {
		if (ip == null) {
			throw new NullPointerException();
		}
		this.impl = impl;
		this.ip = ip;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (LOG.isDebugEnabled()) {
			final String declaringClass = method.getDeclaringClass().getName();
			final String name = method.getName();

			LOG.debug(declaringClass + "." + name);
		}

		final Object invoke = method.invoke(impl, args);

		final Object createProxy = ip.createProxy(this, invoke);
		if (createProxy != invoke) {
			return createProxy;
		}

		if (invoke != null) {
			final Class clazz = invoke.getClass();
			final Class returnType = method.getReturnType();
			final boolean isPrimitive = clazz.isPrimitive() && returnType.isPrimitive();
			final boolean isInterface = returnType.isInterface();
			if (!isPrimitive && isInterface) {
				final boolean isArray = returnType.isArray();
				if (isArray) {
					final Object[] invokes = (Object[]) invoke;
					if (invokes.length == 0) {
						return invokes;
					}

					final List liste = new ArrayList();
					for (int i = 0; i < invokes.length; i++) {
						final Object invokeOnly = invokes[i];
						final Object wrapObject = createProxy(invokeOnly);
						liste.add(wrapObject);
					}

					return liste.toArray((Object[]) Array.newInstance(returnType.getComponentType(), liste.size()));
				} else {
					final Object wrapObject = createProxy(invoke);
					return wrapObject;
				}
			}
		}

		return invoke;
	}

	private Object createProxy(final Object impl) {
		final Class clazz = impl.getClass();
		final ClassLoader classLoader = clazz.getClassLoader();
		final Class[] interfaces = ClassUtils.findInterfaces(clazz);

		if (interfaces == null || interfaces.length == 0) {
			return impl;
		}
		final InvocationHandler ih = new ImplementationDecorator(impl, ip);

		final Object proxy = Proxy.newProxyInstance(classLoader, interfaces, ih);

		return proxy;
	}

	public static interface ImplementationProxy {
		public Object createProxy(final ImplementationDecorator origine, final Object invoke);
	}
}
