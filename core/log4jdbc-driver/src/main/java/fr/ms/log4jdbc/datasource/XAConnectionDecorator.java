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

import javax.transaction.xa.XAResource;

import fr.ms.lang.reflect.ImplementationDecorator;
import fr.ms.lang.reflect.ImplementationDecorator.ImplementationProxy;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.proxy.Log4JdbcProxyXA;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class XAConnectionDecorator extends ConnectionDecorator {

	private final Log4JdbcContextXA log4JdbcContext;

	protected XAConnectionDecorator(final Log4JdbcContextXA log4JdbcContext, final Object sourceImpl) {
		super(log4JdbcContext, sourceImpl);
		this.log4JdbcContext = log4JdbcContext;
	}

	public static Object proxyConnection(final Log4JdbcContextXA log4JdbcContext, final Object impl,
			final Object sourceImpl) {
		final ImplementationProxy ip = new XAConnectionDecorator(log4JdbcContext, sourceImpl);

		return proxyConnection(ip, impl, sourceImpl);
	}

	public Object createProxy(final ImplementationDecorator origine, Object invoke) {
		invoke = super.createProxy(origine, invoke);
		if (invoke instanceof XAResource) {

			final XAResource xaResource = (XAResource) invoke;

			final XAResource wrapObject = Log4JdbcProxyXA.proxyXAResource(xaResource, log4JdbcContext);

			return wrapObject;
		}
		return invoke;
	}
}
