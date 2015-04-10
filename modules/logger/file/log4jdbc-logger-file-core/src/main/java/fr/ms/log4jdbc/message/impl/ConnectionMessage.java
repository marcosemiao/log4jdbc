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

import fr.ms.log4jdbc.message.AbstractMessage;
import fr.ms.log4jdbc.message.MessageHandler;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.Transaction;
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

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final MessageProcess generic = new GenericMessage();

  public MessageWriter newMessageWriter(final MessageHandler message, final Method method, final Object[] args,
      final Object invoke, final Throwable exception) {

    final boolean onlyConnect = props.logConnection() && !message.isAutoCommit();
    final boolean allMethod = props.logGenericMessage();
    final boolean exceptionMethod = (exception != null) && props.logRequeteException();

    if (onlyConnect || allMethod || exceptionMethod) {
      final MessageWriter newMessageWriter = super.newMessageWriter(message, method, args, invoke, exception);

      return newMessageWriter;
    }

    return null;
  }

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Object invoke) {

    final boolean allmethod = props.logGenericMessage();

    if (!allmethod) {
      final Transaction transaction = message.getTransaction();

      if (transaction != null
          && (Query.STATE_COMMIT.equals(transaction.getTransactionState()) || Query.STATE_ROLLBACK.equals(transaction
              .getTransactionState()))) {
        final Query[] queriesTransaction = transaction.getQueriesTransaction();
        int commit = 0;
        int rollback = 0;

        for (int i = 0; i < queriesTransaction.length; i++) {
          final Query q = queriesTransaction[i];

          if (Query.STATE_ROLLBACK.equals(q.getState())) {
            rollback = rollback + 1;
          } else if (Query.STATE_COMMIT.equals(q.getState())) {
            commit = commit + 1;
          }
        }

        final String m = "commit : " + commit + " - rollback : " + rollback;
        messageWriter.traceMessage(m);
      }
    } else {
      generic.buildLog(messageWriter, message, method, args, invoke);
    }
  }

  public void buildLog(final MessageWriter messageWriter, final MessageHandler message, final Method method,
      final Object[] args, final Throwable exception) {
    generic.buildLog(messageWriter, message, method, args, exception);
  }
}
