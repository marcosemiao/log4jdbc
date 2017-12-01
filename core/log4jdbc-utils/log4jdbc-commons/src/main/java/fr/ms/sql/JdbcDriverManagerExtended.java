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
package fr.ms.sql;

import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class JdbcDriverManagerExtended implements JdbcDriverManager {

	private final static JdbcDriverManager driverManager = new JdbcDriverManagerImpl();

	private final Vector jdbcDrivers = new Vector();

	public void setLogWriter(final PrintWriter out) {
		driverManager.setLogWriter(out);
	}

	public Enumeration getDrivers() {
		final Vector result = new Vector();

		java.sql.Driver d;
		for (int i = 0; i < jdbcDrivers.size(); i++) {
			final DriverInfo di = (DriverInfo) jdbcDrivers.elementAt(i);
			result.addElement(di.driver);
		}

		final Enumeration driverManagerDrivers = driverManager.getDrivers();
		while (driverManagerDrivers.hasMoreElements()) {
			d = (java.sql.Driver) driverManagerDrivers.nextElement();

			result.addElement(d);
		}

		return result.elements();
	}

	public synchronized void registerDriver(final Driver driver) throws SQLException {

		if (!Driver.class.equals(driver.getClass())) {
			final DriverInfo driverInfo = new DriverInfo(driver);
			if (!jdbcDrivers.contains(driverInfo)) {
				jdbcDrivers.addElement(driverInfo);
			}
		}
		driverManager.registerDriver(driver);
	}

	private final static class DriverInfo {
		private final Driver driver;
		private final Class driverClass;

		public DriverInfo(final Driver driver) {
			this.driver = driver;
			this.driverClass = driver.getClass();
		}

		public boolean equals(final Object other) {
			return (other instanceof DriverInfo) && this.driverClass == ((DriverInfo) other).driverClass;
		}

		public String toString() {
			return "driver[className=" + driverClass + "]";
		}
	}
}
