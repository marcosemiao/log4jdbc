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

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TransactionDecorator implements Transaction {

	private final Transaction transaction;

	private final RdbmsSpecifics rdbms;

	private final FormatQuery formatQuery;

	public TransactionDecorator(final Transaction transaction, final RdbmsSpecifics rdbms,
			final FormatQuery formatQuery) {
		if (transaction == null || rdbms == null || formatQuery == null) {
			throw new NullPointerException();
		}
		this.transaction = transaction;
		this.rdbms = rdbms;
		this.formatQuery = formatQuery;
	}

	public Query[] getQueriesTransaction() {
		final Query[] queries = transaction.getQueriesTransaction();
		if (queries == null) {
			return null;
		}

		final Query[] result = new Query[queries.length];
		for (int i = 0; i < queries.length; i++) {
			final Query query = queries[i];
			final Query wrap = new QueryDecorator(query, rdbms, formatQuery);
			result[i] = wrap;
		}

		return result;
	}

	public String getTransactionState() {
		return transaction.getTransactionState();
	}

	public long getTransactionNumber() {
		return transaction.getTransactionNumber();
	}

	public long getOpenTransaction() {
		return transaction.getOpenTransaction();
	}

	public String getTransactionType() {
		return transaction.getTransactionType();
	}

	public int hashCode() {
		return transaction.hashCode();
	}

	public boolean equals(final Object obj) {
		if (obj instanceof TransactionDecorator) {
			return transaction.equals(((TransactionDecorator) obj).transaction);
		}
		return transaction.equals(obj);
	}

	public String toString() {
		final String nl = System.getProperty("line.separator");

		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker sb = stringFactory.newString();

		sb.append(getTransactionNumber() + ". " + getOpenTransaction());
		sb.append(nl);
		sb.append("	Type  : " + getTransactionType());
		sb.append(nl);
		sb.append("	State  : " + getTransactionState());
		sb.append(nl);

		if (getQueriesTransaction() != null && getQueriesTransaction().length > 0) {
			sb.append("*********************");
			sb.append(nl);
			sb.append(getQueriesTransaction().length + " queries");

			for (int i = 0; i < getQueriesTransaction().length; i++) {
				sb.append(nl);
				sb.append(getQueriesTransaction()[i]);
			}
		}

		return sb.toString();
	}

}
