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

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.message.AbstractMessage;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.sql.Query;
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
public class ConnectionMessage extends AbstractMessage {

	private final static String nl = System.getProperty("line.separator");

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final MessageProcess generic = new GenericMessage();

	public MessageWriter newMessageWriter(final SqlOperation message, final Method method, final Object[] args,
			final Object invoke, final Throwable exception) {

		final boolean transactionEnabled = props.logTransaction();
		final boolean batchEnabled = props.logBatch();
		final boolean allMethodEnabled = props.logGenericMessage();

		if (transactionEnabled || batchEnabled || allMethodEnabled) {
			final MessageWriter newMessageWriter = super.newMessageWriter(message, method, args, invoke, exception);

			return newMessageWriter;
		}

		return null;
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation sqlOperation, final Method method,
			final Object[] args, final Object invoke) {

		final boolean transactionEnabled = props.logTransaction();
		final boolean batchEnabled = props.logBatch();
		final boolean allMethodEnabled = props.logGenericMessage();

		if (transactionEnabled) {
			final Transaction transaction = sqlOperation.getTransaction();

			if (transaction != null && (transaction.getTransactionState().startsWith(Transaction.STATE_COMMIT)
					|| transaction.getTransactionState().startsWith(Transaction.STATE_ROLLBACK))) {
				final Query[] queriesTransaction = transaction.getQueriesTransaction();
				int commit = 0;
				int rollback = 0;

				final StringMaker queries = stringFactory.newString();

				for (int i = 0; i < queriesTransaction.length; i++) {
					final Query q = queriesTransaction[i];

					if (Query.STATE_ROLLBACK.equals(q.getState())) {
						rollback = rollback + 1;
					} else if (Query.STATE_COMMIT.equals(q.getState())) {
						commit = commit + 1;
					}

					queries.append(q.getQueryNumber());
					queries.append(". ");

					queries.append(q.getState());
					queries.append(" : ");

					queries.append(q.getSQLQuery());
					queries.append("  ");

					queries.append("{executed in ");
					queries.append(q.getExecTime());
					queries.append(" ms} ");

					queries.append(nl);
				}

				final StringMaker message = stringFactory.newString();

				message.append("commit : " + commit + " - rollback : " + rollback);
				message.append(nl);
				message.append("Transaction " + transaction.getTransactionNumber());
				message.append(". Total " + transaction.getOpenTransaction());
				message.append(" - Type : " + transaction.getTransactionType());
				message.append(" - State : " + transaction.getTransactionState());
				message.append(nl);
				message.append(nl);

				messageWriter.traceMessage(message.toString() + queries.toString());
			}
		} else if (batchEnabled) {
			// A implementer
		} else if (allMethodEnabled) {
			generic.buildLog(messageWriter, sqlOperation, method, args, invoke);
		}

	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Throwable exception) {
		generic.buildLog(messageWriter, message, method, args, exception);
	}
}
