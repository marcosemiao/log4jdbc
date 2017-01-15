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

import javax.transaction.xa.XAResource;

import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.ProxyUtils;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.proxy.xa.operation.factory.XAResourceOperationFactory;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class Log4JdbcProxyXA {

	public static XAResource proxyXAResource(final XAResource xaResource, final Log4JdbcContextXA log4JdbcContext) {
		final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.XA_RESOURCE);

		final ProxyOperationFactory factory = new XAResourceOperationFactory(log4JdbcContext);

		final InvocationHandler handler = Log4JdbcProxy.createHandler(xaResource, logs, factory);

		final XAResource instance = (XAResource) ProxyUtils.newProxyInstance(xaResource, handler);

		return instance;
	}
}
