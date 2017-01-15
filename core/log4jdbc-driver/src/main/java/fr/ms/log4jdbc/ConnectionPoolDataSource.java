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

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.PooledConnection;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC;
import fr.ms.log4jdbc.datasource.AbstractRewriteDataSource;
import fr.ms.log4jdbc.datasource.ConnectionDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionPoolDataSource extends AbstractRewriteDataSource implements javax.sql.ConnectionPoolDataSource {

	private final static String PROPERTY = "fr.ms.log4jdbc.ConnectionPoolDataSource";

	private final Log4JdbcContext log4JdbcContext = new Log4JdbcContextJDBC();

	private final javax.sql.ConnectionPoolDataSource connectionPoolDataSource;

	public ConnectionPoolDataSource() {
		this.connectionPoolDataSource = (javax.sql.ConnectionPoolDataSource) newInstanceDataSource();
	}

	public ConnectionPoolDataSource(final javax.sql.ConnectionPoolDataSource xaDataSource) {
		this.connectionPoolDataSource = xaDataSource;
	}

	protected Object getImpl() {
		return connectionPoolDataSource;
	}

	protected String getDataSourceClassName() {
		final String className = System.getProperty(PROPERTY);
		if (className == null) {
			throw new IllegalArgumentException("System property " + PROPERTY + " is not set !!!");
		}
		return className;
	}

	public PooledConnection getPooledConnection() throws SQLException {
		final PooledConnection pooledConnection = connectionPoolDataSource.getPooledConnection();

		final PooledConnection wrap = (PooledConnection) ConnectionDecorator.proxyConnection(log4JdbcContext,
				pooledConnection, connectionPoolDataSource);

		return wrap;
	}

	public PooledConnection getPooledConnection(final String user, final String password) throws SQLException {
		final PooledConnection pooledConnection = connectionPoolDataSource.getPooledConnection(user, password);
		final PooledConnection wrap = (PooledConnection) ConnectionDecorator.proxyConnection(log4JdbcContext,
				pooledConnection, connectionPoolDataSource);

		return wrap;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return connectionPoolDataSource.getLogWriter();
	}

	public void setLogWriter(final PrintWriter out) throws SQLException {
		connectionPoolDataSource.setLogWriter(out);
	}

	public void setLoginTimeout(final int seconds) throws SQLException {
		connectionPoolDataSource.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return connectionPoolDataSource.getLoginTimeout();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return connectionPoolDataSource.getParentLogger();
	}
}
