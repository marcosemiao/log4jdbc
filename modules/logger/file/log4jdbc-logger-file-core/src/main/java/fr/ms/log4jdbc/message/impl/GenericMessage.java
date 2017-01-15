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
import java.sql.SQLException;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.message.AbstractMessage;
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
public class GenericMessage extends AbstractMessage {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final static String nl = System.getProperty("line.separator");

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Object invoke) {
		final String name = method.getName();
		final String declaringClass = method.getDeclaringClass().getName();

		final String genericMessage = getMethodCall(declaringClass + "." + name, args);
		messageWriter.traceMessage(genericMessage);
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Throwable exception) {
		final String name = method.getName();
		final String declaringClass = method.getDeclaringClass().getName();

		final StringMaker genericMessage = stringFactory.newString();

		if (props.logRequeteException()) {
			final Query query = message != null ? message.getQuery() : null;
			if (query != null) {

				genericMessage.append("Requete SQL : ");
				genericMessage.append(nl);
				final String sql = QueryString.buildMessageQuery(query);
				genericMessage.append(sql);
				genericMessage.append(nl);
			}

			final Query[] queriesBatch = message != null ? message.getQueriesBatch() : null;
			if (queriesBatch != null) {
				genericMessage.append(queriesBatch.length);
				genericMessage.append(" queries Batch ");
				genericMessage.append(nl);
				for (int i = 0; i < queriesBatch.length; i++) {
					final Query queryBatch = queriesBatch[i];

					genericMessage.append(queryBatch.getQueryNumber());
					genericMessage.append(" : ");
					genericMessage.append(queryBatch.getSQLQuery());
					genericMessage.append(nl);
				}
			}
		}
		String methodCall = getMethodCall(declaringClass + "." + name, args);
		genericMessage.append(methodCall);

		if (exception instanceof SQLException) {
			final SQLException sqlException = (SQLException) exception;
			genericMessage.append(nl);
			nextSQLException(genericMessage, sqlException);
			genericMessage.append(nl);
		} else {
			genericMessage.append(nl);
			genericMessage.append(" - Exception : ");
			genericMessage.append(exception);
			genericMessage.append(nl);
		}

		messageWriter.traceMessage(genericMessage.toString());
	}

	private void nextSQLException(final StringMaker newString, final SQLException sqlException) {

		final SQLException nextException = sqlException.getNextException();
		if (nextException != null) {
			newString.append("Exception :");
			newString.append(nl);
			newString.append(nextException);
			nextSQLException(newString, nextException);
		} else {
			newString.append("Exception :");
			newString.append(nl);
			newString.append(sqlException);
		}
	}

	public static String getMethodCall(final String methodName, final Object[] args) {
		final StringMaker sb = stringFactory.newString();
		sb.append(methodName);
		sb.append("(");

		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof String) {
					arg = "\"" + arg + "\"";
				}
				sb.append(arg);
				if (i < args.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(");");

		return sb.toString();
	}
}
