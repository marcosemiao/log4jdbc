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
package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.util.CollectionsUtil;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Log4JdbcContextJDBC implements Log4JdbcContext {

	private final static TransactionContextFactory transactionContextFactory = new TransactionContextJDBCFactory();

	private final static Map context = CollectionsUtil.synchronizedMap(new WeakHashMap());

	public ConnectionContextJDBC newConnectionContext(final Connection connection, final Class clazz) {
		ConnectionContextJDBC connectionContextJDBC = (ConnectionContextJDBC) context.get(connection);

		if (connectionContextJDBC == null) {
			String url = null;
			try {
				url = connection.getMetaData().getURL();
			} catch (final SQLException e) {
			}
			connectionContextJDBC = new ConnectionContextJDBC(connection, transactionContextFactory, clazz, url);
			context.put(connection, connectionContextJDBC);
		}

		return connectionContextJDBC;
	}

	public ConnectionContextJDBC newConnectionContext(final Connection connection, final Driver driver,
			final String url) {
		ConnectionContextJDBC connectionContextJDBC = (ConnectionContextJDBC) context.get(connection);

		if (connectionContextJDBC == null) {
			final Class clazz = driver.getClass();
			connectionContextJDBC = new ConnectionContextJDBC(connection, transactionContextFactory, clazz, url);
			context.put(connection, connectionContextJDBC);
		}

		return connectionContextJDBC;
	}
}
