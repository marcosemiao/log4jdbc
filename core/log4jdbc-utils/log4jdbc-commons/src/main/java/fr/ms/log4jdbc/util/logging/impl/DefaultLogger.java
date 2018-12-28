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
package fr.ms.log4jdbc.util.logging.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import fr.ms.log4jdbc.lang.delegate.DefaultStringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.StringMakerFactory;
import fr.ms.log4jdbc.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.util.logging.Logger;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultLogger implements Logger {

	private final static String nl = System.getProperty("line.separator");

	private final static StringMakerFactory stringMakerFactory = DefaultStringMakerFactory.getInstance();

	private final PrintHandler printHandler;

	private final String name;

	private String level;

	private boolean trace;

	private boolean debug;

	private boolean info;

	private boolean warn;

	private boolean error;

	private boolean fatal;

	public DefaultLogger(final PrintHandler printHandler, final String level, final String name) {

		this.printHandler = printHandler;

		if (level != null) {
			if (level.equals("trace")) {
				trace = true;
				debug = true;
				info = true;
				error = true;
			} else if (level.equals("debug")) {
				debug = true;
				info = true;
				error = true;
			} else if (level.equals("info")) {
				info = true;
				error = true;
			} else if (level.equals("warn")) {
				warn = true;
				error = true;
			} else if (level.equals("error")) {
				error = true;
			} else if (level.equals("fatal")) {
				fatal = true;
			}

			this.level = level.toUpperCase();
		}

		this.name = name;
	}

	public boolean isTraceEnabled() {
		return trace;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public boolean isInfoEnabled() {
		return info;
	}

	public boolean isWarnEnabled() {
		return warn;
	}

	public boolean isErrorEnabled() {
		return error;
	}

	public boolean isFatalEnabled() {
		return fatal;
	}

	public void trace(final String message) {
		trace(message, null);
	}

	public void trace(final String message, final Throwable t) {
		if (isTraceEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.trace(formatMessage);
		}
	}

	public void debug(final String message) {
		debug(message, null);
	}

	public void debug(final String message, final Throwable t) {
		if (isDebugEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.debug(formatMessage);
		}
	}

	public void info(final String message) {
		info(message, null);
	}

	public void info(final String message, final Throwable t) {
		if (isInfoEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.info(formatMessage);
		}
	}

	public void warn(final String message) {
		warn(message, null);
	}

	public void warn(final String message, final Throwable t) {
		if (isWarnEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.warn(formatMessage);
		}
	}

	public void error(final String message) {
		error(message, null);
	}

	public void error(final String message, final Throwable t) {
		if (isErrorEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.error(formatMessage);
		}
	}

	public void fatal(final String message) {
		fatal(message, null);
	}

	public void fatal(final String message, final Throwable t) {
		if (isFatalEnabled()) {
			final String formatMessage = formatMessage(message) + getException(t);
			printHandler.fatal(formatMessage);
		}
	}

	private String getException(final Throwable t) {
		String exception = "";

		if (t != null) {
			final StringWriter errors = new StringWriter();
			t.printStackTrace(new PrintWriter(errors));

			exception = nl + t.getMessage() + nl + errors.toString();
		}

		return exception;
	}

	private String formatMessage(final String message) {
		final Date now = new Date();

		final StringMaker newMessage = stringMakerFactory.newString();
		newMessage.append("[");
		newMessage.append(now);
		newMessage.append("]");

		newMessage.append(" [");
		newMessage.append(level);
		newMessage.append("]");

		newMessage.append(" [");
		newMessage.append(name);
		newMessage.append("] ");

		newMessage.append(message);

		return newMessage.toString();
	}

	public PrintWriter getPrintWriter() {
		final Writer writerLogger = new WriterLogger(this);
		// PrintWriter (println) est synchronized et si le logger est desactivé,
		// cela detruit les perfs :( à revoir
		return new PrintWriter(writerLogger);
	}

	@Override
	public String toString() {
		return "DefaultLogger [name=" + name + ", level=" + level + "]";
	}
}
