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
package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TraceTimeInvocationOperationFactory implements ProxyOperationFactory {

	private final ProxyOperationFactory factory;

	public TraceTimeInvocationOperationFactory(final ProxyOperationFactory factory) {
		this.factory = factory;
	}

	public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method,
			final Object[] args) {
		final ProxyOperation newLog4JdbcOperation = factory.newOperation(timeInvocation, proxy, method, args);

		final ProxyOperation decorator = new TraceTimeInvocationOperation((Log4JdbcOperation) newLog4JdbcOperation,
				timeInvocation);

		return decorator;
	}

	private final class TraceTimeInvocationOperation implements Log4JdbcOperation {

		private final Log4JdbcOperation operation;

		private final TimeInvocation timeInvocation;

		public TraceTimeInvocationOperation(final Log4JdbcOperation newLog4JdbcOperation,
				final TimeInvocation timeInvocation) {
			this.operation = newLog4JdbcOperation;
			this.timeInvocation = timeInvocation;
		}

		public SqlOperation getOperation() {
			return operation.getOperation();
		}

		public Object getInvoke() {
			timeInvocation.getWrapInvocation().setInvoke(operation.getInvoke());
			return timeInvocation;
		}
	}
}
