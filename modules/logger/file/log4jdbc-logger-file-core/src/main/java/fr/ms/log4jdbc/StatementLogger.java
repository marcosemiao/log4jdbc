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
package fr.ms.log4jdbc;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.message.impl.StatementMessage;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.log4jdbc.writer.MessageWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class StatementLogger extends AbstractLogger implements SqlOperationLogger {

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	public StatementLogger() {
		super(new StatementMessage());
	}

	public boolean isLogger(final String typeLogger) {
		return SqlOperationLogger.STATEMENT.equals(typeLogger)
				|| SqlOperationLogger.PREPARED_STATEMENT.equals(typeLogger)
				|| SqlOperationLogger.CALLABLE_STATEMENT.equals(typeLogger);
	}

	public boolean isEnabled() {
		return props.logEnabled()
				&& (props.logGenericMessage() || props.logRequeteSelectSQL() || props.logRequeteInsertSQL()
						|| props.logRequeteUpdateSQL() || props.logRequeteDeleteSQL() || props.logRequeteCreateSQL());
	}

	public void buildLog(final SqlOperation message, final Method method, final Object[] args, final Object invoke) {
		final MessageProcess wrapper = getInstance();

		final MessageWriter newMessageWriter = wrapper.newMessageWriter(message, method, args, invoke, null);

		if (newMessageWriter != null) {
			wrapper.buildLog(newMessageWriter, message, method, args, invoke);
		}
	}

	public void buildLog(final SqlOperation message, final Method method, final Object[] args,
			final Throwable exception) {
		final MessageProcess wrapper = getInstance();

		final MessageWriter newMessageWriter = wrapper.newMessageWriter(message, method, args, null, exception);

		if (newMessageWriter != null) {
			wrapper.buildLog(newMessageWriter, message, method, args, exception);
		}
	}
}
