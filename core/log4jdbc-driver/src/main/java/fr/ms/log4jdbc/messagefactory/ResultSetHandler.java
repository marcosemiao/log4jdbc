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
package fr.ms.log4jdbc.messagefactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import fr.ms.log4jdbc.invocationhandler.MessageFactory;
import fr.ms.log4jdbc.invocationhandler.MessageInvocationHandler.MessageInvocationContext;
import fr.ms.log4jdbc.invocationhandler.TimeInvocation;
import fr.ms.log4jdbc.message.MessageHandlerImpl;
import fr.ms.log4jdbc.message.resultset.CellImpl;
import fr.ms.log4jdbc.message.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.Query;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ResultSetHandler implements MessageFactory {

    private final Query query;

    private final ResultSetCollectorImpl resultSetCollector;

    private final ResultSet rs;

    private int position = 0;

    private CellImpl lastCell;

    public ResultSetHandler(final Query query, final ResultSet rs) {
	this.query = query;
	this.rs = rs;
	this.resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
    }

    public MessageHandlerImpl transformMessage(final Object proxy, final Method method, final Object[] args, final MessageInvocationContext mic,
	    final MessageHandlerImpl message) {
	final TimeInvocation timeInvocation = mic.getInvokeTime();

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();
	final String nameMethod = method.getName();

	final boolean nextMethod = nameMethod.equals("next") && invoke != null && ((Boolean) invoke).booleanValue();
	if (nextMethod) {
	    if (position == -1) {
		try {
		    if (targetException == null) {
			position = rs.getRow();
		    } else {
			position = Integer.MAX_VALUE;
		    }
		} catch (final Throwable e) {
		    position = Integer.MAX_VALUE;
		}
	    } else {
		position++;
	    }

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean previousMethod = nameMethod.equals("previous") && invoke != null && ((Boolean) invoke).booleanValue();
	if (previousMethod) {
	    if (position == -1) {
		try {
		    if (targetException == null) {
			position = rs.getRow();
		    } else {
			position = Integer.MAX_VALUE;
		    }
		} catch (final Throwable e) {
		    position = Integer.MAX_VALUE;
		}
	    } else {
		position--;
	    }

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean firstMethod = nameMethod.equals("first") && invoke != null && ((Boolean) invoke).booleanValue();
	if (firstMethod) {
	    position = 1;

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean lastMethod = nameMethod.equals("last") && invoke != null && ((Boolean) invoke).booleanValue();
	if (lastMethod) {
	    try {
		if (targetException == null) {
		    position = rs.getRow();
		} else {
		    position = Integer.MAX_VALUE;
		}
	    } catch (final Throwable e) {
		position = Integer.MAX_VALUE;
	    }

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean beforeFirstMethod = nameMethod.equals("beforeFirst");
	if (beforeFirstMethod) {
	    position = 0;

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean afterLastMethod = nameMethod.equals("afterLast");
	if (afterLastMethod) {
	    position = -1;

	    if (!resultSetCollector.isClosed()) {
		message.setQuery(query);
	    }

	    return message;
	}

	final boolean wasNullMethod = nameMethod.equals("wasNull") && lastCell != null && invoke != null && ((Boolean) invoke).booleanValue();
	if (wasNullMethod) {
	    lastCell.wasNull();
	    return message;
	}

	final boolean getMetaDataMethod = nameMethod.startsWith("getMetaData") && invoke != null;
	if (getMetaDataMethod) {

	    if (resultSetCollector.getColumns().length == 0) {
		final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
		resultSetCollector.setColumnsDetail(resultSetMetaData);
	    }
	    return message;
	}

	final boolean closeMethod = nameMethod.startsWith("close") && !resultSetCollector.isClosed();
	if (closeMethod) {
	    message.setQuery(query);
	    resultSetCollector.close();
	    return message;
	}

	final boolean getValueColumn = nameMethod.startsWith("get") && targetException == null && args != null && args.length > 0;
	if (getValueColumn) {
	    final Class arg0Type = method.getParameterTypes()[0];
	    if (Integer.class.equals(arg0Type) || Integer.TYPE.equals(arg0Type)) {
		final Integer arg = (Integer) args[0];
		lastCell = resultSetCollector.addValueColumn(position, invoke, arg.intValue());
	    } else if (String.class.equals(arg0Type)) {
		final String arg = (String) args[0];
		lastCell = resultSetCollector.addValueColumn(position, invoke, arg);
	    }
	    return message;
	}

	return message;
    }

    public Object wrap(final Object invoke, final Object[] args, final MessageInvocationContext mic) {
	return invoke;
    }
}
