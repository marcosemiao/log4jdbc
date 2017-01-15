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
package fr.ms.log4jdbc.message.impl;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.message.AbstractMessage;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.log4jdbc.utils.QueryString;
import fr.ms.log4jdbc.writer.MessageWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class StatementMessage extends AbstractMessage {

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final MessageProcess generic = new GenericMessage();

	public MessageWriter newMessageWriter(final SqlOperation message, final Method method, final Object[] args,
			final Object invoke, final Throwable exception) {

		boolean onlyRequest = false;
		final boolean allMethod = props.logGenericMessage();

		final boolean exceptionMethod = (exception != null) && props.logRequeteException();

		if (message != null && message.getQuery() != null) {
			final Query query = message.getQuery();

			final String methodQuery = query.getMethodQuery();
			onlyRequest = (((Query.METHOD_EXECUTE.equals(methodQuery) && props.logRequeteExecuteSQL())
					|| (Query.METHOD_BATCH.equals(methodQuery) && props.logRequeteBatchSQL())) && logRequest(query));
		}
		if (onlyRequest || allMethod || exceptionMethod) {
			final MessageWriter newMessageWriter = super.newMessageWriter(message, method, args, invoke, exception);

			return newMessageWriter;
		}

		return null;
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Object invoke) {

		final boolean allmethod = props.logGenericMessage();

		if (!allmethod) {
			final Query query = message.getQuery();
			if (props.logRequeteSelectResultSetSQL() && query.getResultSetCollector() != null) {
				return;
			}

			final String messageQuery = QueryString.buildMessageQuery(query);
			messageWriter.traceMessage(messageQuery);
		} else {
			generic.buildLog(messageWriter, message, method, args, invoke);
		}
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Throwable exception) {
		generic.buildLog(messageWriter, message, method, args, exception);
	}

	private static boolean logRequest(final Query query) {
		final String sqlCommand = query.getTypeQuery();

		final boolean isLog = props.logRequeteAllSQL() || (props.logRequeteSelectSQL() && "select".equals(sqlCommand))
				|| (props.logRequeteInsertSQL() && "insert".equals(sqlCommand))
				|| (props.logRequeteUpdateSQL() && "update".equals(sqlCommand))
				|| (props.logRequeteDeleteSQL() && "delete".equals(sqlCommand))
				|| (props.logRequeteCreateSQL() && "create".equals(sqlCommand));

		return isLog;
	}
}
