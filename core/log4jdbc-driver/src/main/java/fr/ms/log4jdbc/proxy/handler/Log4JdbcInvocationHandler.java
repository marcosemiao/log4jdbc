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
import fr.ms.lang.reflect.ProxyOperationInvocationHandler;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationDecorator;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.FormatQueryFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Log4JdbcInvocationHandler extends ProxyOperationInvocationHandler {

	private final SqlOperationLogger[] logs;

	public Log4JdbcInvocationHandler(final Object implementation, final SqlOperationLogger[] logs,
			final ProxyOperationFactory factory) {
		super(implementation, factory);
		this.logs = logs;
	}

	public boolean preProcess() {
		boolean buildOperation = (logs != null && logs.length != 0);
		if (buildOperation) {
			buildOperation = false;
			for (int i = 0; i < logs.length; i++) {
				final SqlOperationLogger log = logs[i];
				buildOperation = buildOperation | log.isEnabled();
			}
		}
		return buildOperation;
	}

	public void postProcess(final ProxyOperation proxyOperation, final TimeInvocation timeInvocation,
			final Object proxy, final Method method, final Object[] args) {
		final Log4JdbcOperation log4JdbcOperation = (Log4JdbcOperation) proxyOperation;

		final SqlOperation sqlOperation = log4JdbcOperation.getOperation();

		final Object invoke = timeInvocation.getInvoke();
		final Throwable targetException = timeInvocation.getTargetException();
		for (int i = 0; i < logs.length; i++) {
			final SqlOperationLogger log = logs[i];

			if (log != null && log.isEnabled()) {
				try {
					final SqlOperation sqlOperationFormatQuery = getSqlOperation(sqlOperation, log);
					if (targetException == null) {
						log.buildLog(sqlOperationFormatQuery, method, args, invoke);
					} else {
						log.buildLog(sqlOperationFormatQuery, method, args, targetException);
					}
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	public static SqlOperation getSqlOperation(final SqlOperation sqlOperation, final SqlOperationLogger log) {
		if (log instanceof FormatQueryFactory) {
			final FormatQueryFactory formatQueryFactory = (FormatQueryFactory) log;

			final FormatQuery formatQuery = formatQueryFactory.getFormatQuery();

			if (formatQuery != null) {
				final SqlOperation wrap = new SqlOperationDecorator(sqlOperation, formatQuery);
				return wrap;
			}
		}

		return sqlOperation;
	}
}
