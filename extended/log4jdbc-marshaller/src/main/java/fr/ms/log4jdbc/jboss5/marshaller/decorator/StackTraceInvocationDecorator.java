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
package fr.ms.log4jdbc.jboss5.marshaller.decorator;

import java.io.IOException;

import org.jboss.remoting.marshal.MarshallerDecorator;
import org.jboss.remoting.marshal.UnMarshallerDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class StackTraceInvocationDecorator implements MarshallerDecorator, UnMarshallerDecorator {

	public Object addDecoration(Object obj) throws IOException {
		if (!(obj instanceof StackTraceInvocationWrapper)) {
			obj = new StackTraceInvocationWrapper(obj);
		}

		return obj;
	}

	public Object removeDecoration(Object obj) throws IOException {
		if ((obj instanceof StackTraceInvocationWrapper)) {
			final StackTraceInvocationWrapper wrapper = (StackTraceInvocationWrapper) obj;
			final StackTraceElement[] stackTrace = wrapper.getStackTrace();
			StackTraceThreadLocal.setStacktrace(stackTrace);
			final String threadName = wrapper.getThreadName();
			StackTraceThreadLocal.setThreadName(threadName);
			obj = wrapper.getObj();
		}

		return obj;
	}
}
