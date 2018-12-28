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
package fr.ms.log4jdbc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class StackTraceHolder extends Throwable {

	private static final long serialVersionUID = 1L;

	private StackTraceHolder() {
	}

	public static String returnStackTrace() {
		final StringWriter sw = new StringWriter();
		new StackTraceHolder().printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	@Override
	public String toString() {
		return "StackTrace : ";
	}
}
