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
package fr.ms.log4jdbc.context;

import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.util.logging.Logger;
import fr.ms.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public abstract class TransactionContextDefault implements Transaction {

	private final static Logger LOG = LoggerManager.getLogger(TransactionContextDefault.class);

	private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

	private final static SyncLong totalTransactionNumber = syncLongFactory.newLong();

	private final static SyncLong openTransactionCurrent = syncLongFactory.newLong();

	private long transactionNumber;

	private long openTransaction;

	protected String state = Transaction.STATE_NOT_EXECUTE;

	{
		transactionNumber = totalTransactionNumber.incrementAndGet();
		openTransaction = openTransactionCurrent.incrementAndGet();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Nouvelle Transaction - transactionNumber : " + transactionNumber + " - openTransaction : "
					+ openTransaction);
		}
	}

	public void close() {
		long tNumber = openTransactionCurrent.decrementAndGet();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Fermeture Transaction - transactionNumber : " + tNumber);
		}
	}

	public long getOpenTransaction() {
		return openTransaction;
	}

	public long getTransactionNumber() {
		return transactionNumber;
	}

	public String getTransactionState() {
		return state;
	}
}
