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
package fr.ms.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class TimeInvocationHandler implements InvocationHandler {

    private static final Method OBJECT_EQUALS;

    static {
	try {
	    OBJECT_EQUALS = Object.class.getMethod("equals",
		    new Class[] { Object.class });
	} catch (final NoSuchMethodException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    private final Object implementation;

    public TimeInvocationHandler(final Object implementation) {
	this.implementation = implementation;
    }

    public Object invoke(final Object proxy, final Method method,
	    final Object[] args) throws Throwable {
	final TimeInvocation timeInvoke = new TimeInvocation();
	try {
	    Object invoke = null;

	    if (OBJECT_EQUALS.equals(method)
		    && Proxy.isProxyClass(args[0].getClass())) {
		final boolean isEquals = (proxy == args[0]);
		if (isEquals) {
		    invoke = Boolean.valueOf(isEquals);
		}
	    }

	    if (invoke == null) {
		invoke = method.invoke(implementation, args);
	    }

	    timeInvoke.setInvoke(invoke);
	} catch (final InvocationTargetException s) {
	    final Throwable targetException = s.getTargetException();
	    if (targetException == null) {
		throw s;
	    }
	    timeInvoke.setTargetException(targetException);
	} finally {
	    timeInvoke.finish();
	}

	return timeInvoke;
    }
}
