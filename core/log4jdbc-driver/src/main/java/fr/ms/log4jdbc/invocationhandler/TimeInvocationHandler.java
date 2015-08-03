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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TimeInvocationHandler implements InvocationHandler {

    private final Object implementation;

    public TimeInvocationHandler(final Object implementation) {
	this.implementation = implementation;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation message = new TimeInvocation();
	try {
	    final Object invoke = method.invoke(implementation, args);
	    message.setInvoke(invoke);
	} catch (final InvocationTargetException s) {
	    final Throwable targetException = s.getTargetException();
	    if (targetException == null) {
		throw s;
	    }
	    message.setTargetException(targetException);
	}

	message.calculExecTime(System.currentTimeMillis());

	return message;
    }
}
