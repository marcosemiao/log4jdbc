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
package fr.ms.util.logging;

import java.io.PrintWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public interface Logger {

	boolean isTraceEnabled();

	boolean isDebugEnabled();

	boolean isInfoEnabled();

	boolean isWarnEnabled();

	boolean isErrorEnabled();

	boolean isFatalEnabled();

	void trace(String message);

	void trace(String message, Throwable t);
	
	void debug(String message);

	void debug(String message, Throwable t);

	void info(String message);

	void info(String message, Throwable t);

	void warn(String message);

	void warn(String message, Throwable t);

	void error(String message);

	void error(String message, Throwable t);

	void fatal(String message);

	void fatal(String message, Throwable t);

	PrintWriter getPrintWriter();
}
