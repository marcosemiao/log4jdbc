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
package fr.ms.log4jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import fr.ms.lang.ClassUtils;
import fr.ms.lang.reflect.ImplementationDecorator;
import fr.ms.lang.reflect.ImplementationDecorator.ImplementationProxy;
import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionDecorator implements ImplementationProxy {

	protected final Log4JdbcContext log4JdbcContext;

	private final Object sourceImpl;

	protected ConnectionDecorator(final Log4JdbcContext log4JdbcContext, final Object sourceImpl) {
		this.log4JdbcContext = log4JdbcContext;
		this.sourceImpl = sourceImpl;
	}

	public static Object proxyConnection(final Log4JdbcContext log4JdbcContext, final Object impl,
			final Object sourceImpl) {
		final ImplementationProxy ip = new ConnectionDecorator(log4JdbcContext, sourceImpl);

		return proxyConnection(ip, impl, sourceImpl);
	}

	public static Object proxyConnection(final ImplementationProxy ip, final Object impl, final Object sourceImpl) {
		final InvocationHandler ih = new ImplementationDecorator(impl, ip);

		final Class clazz = impl.getClass();
		final ClassLoader classLoader = clazz.getClassLoader();
		final Class[] interfaces = ClassUtils.findInterfaces(clazz);
		final Object wrap = Proxy.newProxyInstance(classLoader, interfaces, ih);

		return wrap;
	}

	public Object createProxy(final ImplementationDecorator origine, final Object invoke) {
		if (invoke instanceof Connection) {

			final Connection c = (Connection) invoke;

			final Connection wrapObject = Log4JdbcProxy.proxyConnection(c, log4JdbcContext, sourceImpl.getClass());
			return wrapObject;
		}
		return invoke;
	}
}
