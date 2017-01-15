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

import javax.sql.XAConnection;

import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.datasource.AbstractRewriteDataSource;
import fr.ms.log4jdbc.datasource.XAConnectionDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class XADataSource extends AbstractRewriteDataSource implements javax.sql.XADataSource {

	private final static String PROPERTY = "fr.ms.log4jdbc.XADataSource";

	private final Log4JdbcContextXA log4JdbcContext = new Log4JdbcContextXA();

	private final javax.sql.XADataSource xaDataSource;

	public XADataSource() {
		this.xaDataSource = (javax.sql.XADataSource) newInstanceDataSource();
	}

	public XADataSource(final javax.sql.XADataSource xaDataSource) {
		this.xaDataSource = xaDataSource;
	}

	protected Object getImpl() {
		return xaDataSource;
	}

	protected String getDataSourceClassName() {
		final String className = System.getProperty(PROPERTY);
		if (className == null) {
			throw new IllegalArgumentException("System property " + PROPERTY + " is not set !!!");
		}
		return className;
	}

	public XAConnection getXAConnection() throws SQLException {
		final XAConnection xaConnection = xaDataSource.getXAConnection();
		final XAConnection wrap = (XAConnection) XAConnectionDecorator.proxyConnection(log4JdbcContext, xaConnection,
				xaDataSource);

		return wrap;
	}

	public XAConnection getXAConnection(final String user, final String password) throws SQLException {
		final XAConnection xaConnection = xaDataSource.getXAConnection(user, password);
		final XAConnection wrap = (XAConnection) XAConnectionDecorator.proxyConnection(log4JdbcContext, xaConnection,
				xaDataSource);

		return wrap;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return xaDataSource.getLogWriter();
	}

	public void setLogWriter(final PrintWriter out) throws SQLException {
		xaDataSource.setLogWriter(out);
	}

	public void setLoginTimeout(final int seconds) throws SQLException {
		xaDataSource.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return xaDataSource.getLoginTimeout();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return xaDataSource.getParentLogger();
	}
}
