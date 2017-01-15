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

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.resultset.ResultSetCollector;
import fr.ms.log4jdbc.resultset.Row;
import fr.ms.log4jdbc.sql.Query;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class QueryString {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final static String nl = System.getProperty("line.separator");

	public final static String buildMessageQuery(final Query query) {
		return buildMessageQuery(query, null);
	}

	public final static String buildMessageQuery(final Query query, final Long resultSetExecTime) {
		final StringMaker sb = stringFactory.newString();

		sb.append("Query Number : " + query.getQueryNumber() + " - State : " + query.getState());

		final Integer updateCount = query.getUpdateCount();
		if (updateCount != null) {
			sb.append(" - Update Count : " + updateCount);
		}

		final ResultSetCollector resultSetCollector = query.getResultSetCollector();

		if (resultSetCollector != null) {
			if (query.getResultSetCollector() != null && query.getResultSetCollector().getCurrentRow() != null) {
				sb.append(" - Current ResultSet Line Number : "
						+ query.getResultSetCollector().getCurrentRow().getPosition());
			}

			if (resultSetExecTime != null) {
				if (resultSetCollector.isClosed()) {
					final Row[] rows = resultSetCollector.getRows();
					if (rows != null) {
						sb.append(" - Result Count : ");
						sb.append(rows.length);
						sb.append(" - ResultSet Exec Time : ");
						sb.append(resultSetExecTime);
						sb.append(" ms");
					}
				}
			}
		}

		sb.append(nl);

		final Transaction transaction = query.getTransaction();
		if (transaction != null) {
			sb.append("Transaction Number : " + transaction.getTransactionNumber());
			sb.append(" - Type : " + transaction.getTransactionType());
			sb.append(" - State : " + transaction.getTransactionState());
			sb.append(nl);
		}

		final String sql = query.getSQLQuery();

		if (sql != null) {
			sb.append(nl);

			sb.append(sql);

			sb.append(nl);
		}

		return sb.toString();
	}
}
