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
package fr.ms.log4jdbc.proxy.xa.operation.factory;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.lang.reflect.ProxyOperation;
import fr.ms.log4jdbc.lang.reflect.ProxyOperationFactory;
import fr.ms.log4jdbc.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.proxy.xa.operation.XAResourceOperation;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class XAResourceOperationFactory implements ProxyOperationFactory {

	private final Log4JdbcContextXA log4JdbcContext;

	public XAResourceOperationFactory(final Log4JdbcContextXA log4JdbcContext) {
		this.log4JdbcContext = log4JdbcContext;
	}

	public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method,
			final Object[] args) {
		final ProxyOperation operation = new XAResourceOperation(log4JdbcContext, timeInvocation, proxy, method, args);

		return operation;
	}
}
