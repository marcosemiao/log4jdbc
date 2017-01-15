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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC;
import fr.ms.log4jdbc.datasource.AbstractRewriteDataSource;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DataSource extends AbstractRewriteDataSource implements javax.sql.DataSource {

	private final static String PROPERTY = "fr.ms.log4jdbc.DataSource";

	private final Log4JdbcContext log4JdbcContext = new Log4JdbcContextJDBC();

	private final javax.sql.DataSource dataSource;

	public DataSource() {
		this.dataSource = (javax.sql.DataSource) newInstanceDataSource();
	}

	public DataSource(final javax.sql.DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected Object getImpl() {
		return dataSource;
	}

	protected String getDataSourceClassName() {
		final String className = System.getProperty(PROPERTY);
		if (className == null) {
			throw new IllegalArgumentException("System property " + PROPERTY + " is not set !!!");
		}
		return className;
	}

	public Connection getConnection() throws SQLException {
		final Connection c = dataSource.getConnection();
		final Connection wrap = Log4JdbcProxy.proxyConnection(c, log4JdbcContext, dataSource.getClass());

		return wrap;
	}

	public Connection getConnection(final String username, final String password) throws SQLException {
		final Connection c = dataSource.getConnection(username, password);
		final Connection wrap = Log4JdbcProxy.proxyConnection(c, log4JdbcContext, dataSource.getClass());

		return wrap;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}

	public Object unwrap(final Class iface) throws SQLException {
		return dataSource.unwrap(iface);
	}

	public void setLogWriter(final PrintWriter out) throws SQLException {
		dataSource.setLogWriter(out);
	}

	public boolean isWrapperFor(final Class iface) throws SQLException {
		return dataSource.isWrapperFor(iface);
	}

	public void setLoginTimeout(final int seconds) throws SQLException {
		dataSource.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return dataSource.getParentLogger();
	}
}
