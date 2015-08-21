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
package fr.ms.log4jdbc.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.lang.reflect.TimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationDecorator;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
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
public class MessageInvocationHandler implements InvocationHandler {

    private final InvocationHandler invocationHandler;

    private final ConnectionContext connectionContext;

    private final SqlOperationLogger[] logs;

    private final MessageFactory messageFactory;

    private final boolean timeInvocationResult;

    public MessageInvocationHandler(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final MessageFactory messageFactory) {
	this(implementation, connectionContext, logs, messageFactory, false);
    }

    public MessageInvocationHandler(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final MessageFactory messageFactory, final boolean timeInvocationResult) {
	this.invocationHandler = new TimeInvocationHandler(implementation);
	this.connectionContext = connectionContext;
	this.logs = logs;
	this.messageFactory = messageFactory;
	this.timeInvocationResult = timeInvocationResult;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation invokeTime = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final Object invoke = invokeTime.getInvoke();
	final Throwable targetException = invokeTime.getTargetException();

	final SqlOperationContext mic = new SqlOperationContext(invokeTime, connectionContext);

	SqlOperationImpl sqlOperation = null;

	if (logs != null && logs.length != 0) {
	    for (int i = 0; i < logs.length; i++) {
		final SqlOperationLogger log = logs[i];
		if (log != null && log.isEnabled()) {
		    if (sqlOperation == null) {
			sqlOperation = messageFactory.transformMessage(proxy, method, args, mic, sqlOperation);
		    }
		    try {
			final SqlOperation message = getMessage(sqlOperation, log);
			if (targetException == null) {
			    log.buildLog(message, method, args, invoke);
			} else {
			    log.buildLog(message, method, args, targetException);
			}
		    } catch (final Throwable t) {
			t.printStackTrace();
		    }
		}
	    }
	}

	if (targetException != null) {
	    throw targetException;
	}

	final Object wrap = messageFactory.wrap(invoke, args, mic);

	if (timeInvocationResult) {
	    invokeTime.setInvoke(wrap);
	    return invokeTime;
	}
	return wrap;
    }

    private static SqlOperation getMessage(final SqlOperationImpl sqlOperation, final SqlOperationLogger log) {
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
