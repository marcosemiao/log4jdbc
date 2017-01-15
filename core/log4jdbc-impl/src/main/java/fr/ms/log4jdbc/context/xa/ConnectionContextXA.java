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
package fr.ms.log4jdbc.context.xa;

import java.sql.Connection;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextXA extends ConnectionContextJDBC {

	public ConnectionContextXA(final Connection connection, final TransactionContextFactory transactionContextFactory,
			final Class clazz, final String url) {
		super(connection, transactionContextFactory, clazz, url);
	}

	public void setTransactionContextXA(final TransactionContextXA transactionContextXA) {
		setTransactionEnabled(transactionContextXA != null);
		this.transactionContext = transactionContextXA;
	}
}
