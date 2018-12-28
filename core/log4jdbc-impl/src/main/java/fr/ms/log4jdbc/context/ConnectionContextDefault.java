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

import java.sql.Connection;

import fr.ms.log4jdbc.lang.delegate.DefaultStringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.DefaultSyncLongFactory;
import fr.ms.log4jdbc.lang.delegate.StringMakerFactory;
import fr.ms.log4jdbc.lang.delegate.SyncLongFactory;
import fr.ms.log4jdbc.lang.reflect.ReflectionUtils;
import fr.ms.log4jdbc.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.serviceloader.RdbmsSpecificsServiceLoader;
import fr.ms.log4jdbc.util.logging.Logger;
import fr.ms.log4jdbc.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextDefault {

	private final static Logger LOG = LoggerManager.getLogger(TransactionContextDefault.class);

	private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

	private final static SyncLong totalConnectionNumber = syncLongFactory.newLong();

	private final static SyncLong openConnection = syncLongFactory.newLong();

	private final Connection connection;

	protected long connectionNumber;

	protected String driverName;

	protected String url;

	protected final RdbmsSpecifics rdbmsSpecifics;

	protected String transactionIsolation = "UNKNOWN";

	{
		this.connectionNumber = totalConnectionNumber.incrementAndGet();
		final long openNumber = openConnection.incrementAndGet();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Nouvelle Connection - connectionNumber : " + connectionNumber + " - openConnection : "
					+ openNumber);
		}
	}

	public ConnectionContextDefault(final Connection connection, final Class clazz, final String url) {
		this.connection = connection;
		this.driverName = clazz.getName();
		this.rdbmsSpecifics = getRdbms(driverName);
		this.url = url;
	}

	public void close() {
		final long oNumber = openConnection.decrementAndGet();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Fermeture Connnection - transactionNumber : " + oNumber);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public long getConnectionNumber() {
		return connectionNumber;
	}

	public SyncLong getTotalConnectionNumber() {
		return totalConnectionNumber;
	}

	public SyncLong getOpenConnection() {
		return openConnection;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getUrl() {
		return url;
	}

	public RdbmsSpecifics getRdbmsSpecifics() {
		return rdbmsSpecifics;
	}

	public String getTransactionIsolation() {
		return transactionIsolation;
	}

	public void setTransactionIsolation(final int transactionIsolation) {
		final String constantName = ReflectionUtils.constantName(Connection.class, transactionIsolation);
		this.transactionIsolation = constantName;
	}

	@Override
	public String toString() {
		final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
		final StringMaker buffer = stringFactory.newString();

		buffer.append("ConnectionContextDefault [driverName=");
		buffer.append(driverName);
		buffer.append(", url=");
		buffer.append(url);
		buffer.append(", connectionNumber=");
		buffer.append(connectionNumber);
		buffer.append(", rdbmsSpecifics=");
		buffer.append(rdbmsSpecifics);
		buffer.append(", transactionIsolation=");
		buffer.append(transactionIsolation);
		buffer.append("]");

		return buffer.toString();
	}

	private final static RdbmsSpecifics getRdbms(final String driverClass) {
		RdbmsSpecifics rdbmsSpecifics = RdbmsSpecificsServiceLoader.getRdbmsSpecifics(driverClass);
		if (rdbmsSpecifics == null) {
			rdbmsSpecifics = GenericRdbmsSpecifics.getInstance();
		}

		return rdbmsSpecifics;
	}
}
