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
package fr.ms.log4jdbc.jboss5.writer;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.jboss5.marshaller.decorator.StackTraceThreadLocal;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.log4jdbc.utils.Log4JdbcStackTrace;
import fr.ms.log4jdbc.writer.MessageWriterStackTraceImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class JBossMessageWriter extends MessageWriterStackTraceImpl {

	private final static String nl = System.getProperty("line.separator");

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private StackTraceElement[] stackTrace;

	private final String threadName = StackTraceThreadLocal.getThreadName();

	public JBossMessageWriter(final SqlOperation message) {
		super(message);
		if (props.logStackTrace()) {
			stackTrace = StackTraceThreadLocal.getStackTrace();
		}
	}

	@Override
	public String traceFooter() {
		if (stackTrace != null) {
			final String stackTraceFilter = Log4JdbcStackTrace.getStackTraceFilter(stackTrace);
			if (stackTraceFilter != null) {
				return stackTraceFilter + "Remote EJB : " + threadName + nl + super.traceFooter();
			}
		}
		return super.traceFooter();
	}
}
