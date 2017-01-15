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
package fr.ms.log4jdbc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ms.lang.SystemPropertyUtils;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class Log4JdbcStackTrace {

	private final static boolean stacktrace_filter = SystemPropertyUtils.getProperty("log4jdbc.stacktrace.filter",
			true);

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final static String nl = System.getProperty("line.separator");

	public static String getStackTraceFilter(StackTraceElement[] stackTrace) {

		final StringBuilder sb = new StringBuilder();
		if (props.logStackTrace()) {

			final List<String> logStackTraceStartPackages = logStackTraceStartPackages();
			if (!logStackTraceStartPackages.isEmpty()) {
				stackTrace = Log4JdbcStackTrace.getStackTrace(stackTrace, logStackTraceStartPackages);
			}
			for (final StackTraceElement stackTraceElement : stackTrace) {
				sb.append(stackTraceElement);
				sb.append(nl);
			}
		}
		return sb.toString();
	}

	private static List<String> logStackTraceStartPackages() {
		final String property = props.getStacktraceStartPackages();
		if (property == null || property.isEmpty()) {
			return new ArrayList<String>();
		}
		final String[] split = property.split(",");
		final List<String> asList = Arrays.asList(split);
		return asList;
	}

	public static StackTraceElement[] getStackTrace(final StackTraceElement[] stackTrace, final List<String> packages) {

		final List<StackTraceElement> stackTracePackage = new ArrayList<StackTraceElement>();

		for (final StackTraceElement stackTraceElement : stackTrace) {
			final String className = stackTraceElement.getClassName();
			for (final String p : packages) {
				final boolean matches = className.startsWith(p.trim());
				if (matches) {
					stackTracePackage.add(stackTraceElement);
					break;
				}
			}
		}

		return stackTracePackage.toArray(new StackTraceElement[0]);
	}

	public static StackTraceElement[] getStackTrace() {
		final Throwable t = new Throwable();
		t.fillInStackTrace();

		final StackTraceElement[] stackTrace = t.getStackTrace();

		final StackTraceElement[] stackTraceDump = getStackTrace(stackTrace);
		return stackTraceDump;
	}

	private static StackTraceElement[] getStackTrace(final StackTraceElement[] stackTrace) {
		if (stackTrace == null || !stacktrace_filter) {
			return stackTrace;
		}
		int position = 0;
		for (position = 0; position < stackTrace.length; position++) {
			final String className = stackTrace[position].getClassName();
			if (!className.startsWith("fr.ms.log4jdbc")) {
				break;
			}
		}
		position = position + 2;
		final int stackTraceLength = stackTrace.length - position;

		if (stackTraceLength < 1) {
			return stackTrace;
		}
		final StackTraceElement[] stackTraceDump = new StackTraceElement[stackTraceLength];

		System.arraycopy(stackTrace, position, stackTraceDump, 0, stackTraceLength);

		return stackTraceDump;
	}
}
