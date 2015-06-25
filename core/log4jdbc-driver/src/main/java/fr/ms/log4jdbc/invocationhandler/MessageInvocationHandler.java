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

import fr.ms.log4jdbc.MessageLogger;
import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.message.MessageHandler;
import fr.ms.log4jdbc.message.MessageHandlerImpl;
import fr.ms.log4jdbc.message.WrapperMessageHandler;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.FormatQueryFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

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

    private final JdbcContext jdbcContext;

    private final MessageLogger[] logs;

    private final MessageFactory messageFactory;

    private final boolean timeInvocationResult;

    public MessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext, final MessageLogger[] logs, final MessageFactory messageFactory) {
	this(implementation, jdbcContext, logs, messageFactory, false);
    }

    public MessageInvocationHandler(final Object implementation, final JdbcContext jdbcContext, final MessageLogger[] logs,
	    final MessageFactory messageFactory, final boolean timeInvocationResult) {
	this.invocationHandler = new TimeInvocationHandler(implementation);
	this.jdbcContext = jdbcContext;
	this.logs = logs;
	this.messageFactory = messageFactory;
	this.timeInvocationResult = timeInvocationResult;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation invokeTime = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final Object invoke = invokeTime.getInvoke();
	final Throwable targetException = invokeTime.getTargetException();

	final MessageInvocationContext mic = new MessageInvocationContext(invokeTime, jdbcContext);

	MessageHandlerImpl messageImpl = null;

	if (logs != null && logs.length != 0) {
	    for (int i = 0; i < logs.length; i++) {
		final MessageLogger log = logs[i];
		if (log != null && log.isEnabled()) {
		    if (messageImpl == null) {
			messageImpl = messageFactory.transformMessage(proxy, method, args, mic, messageImpl);
		    }
		    try {
			final MessageHandler message = getMessage(messageImpl, log);
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

    private static MessageHandler getMessage(final MessageHandlerImpl messageImpl, final MessageLogger log) {
	if (log instanceof FormatQueryFactory) {
	    final FormatQueryFactory formatQueryFactory = (FormatQueryFactory) log;

	    final FormatQuery formatQuery = formatQueryFactory.getFormatQuery();

	    if (formatQuery != null) {
		final MessageHandler wrap = new WrapperMessageHandler(messageImpl, formatQuery);
		return wrap;
	    }
	}

	return messageImpl;
    }

    public class MessageInvocationContext {
	private final TimeInvocation invokeTime;
	private final JdbcContext jdbcContext;

	private QueryImpl query;

	public MessageInvocationContext(final TimeInvocation invokeTime, final JdbcContext jdbcContext) {
	    this.invokeTime = invokeTime;
	    this.jdbcContext = jdbcContext;
	}

	public TimeInvocation getInvokeTime() {
	    return invokeTime;
	}

	public JdbcContext getJdbcContext() {
	    return jdbcContext;
	}

	public QueryImpl getQuery() {
	    return query;
	}

	public void setQuery(final QueryImpl query) {
	    this.query = query;
	}
    }
}
