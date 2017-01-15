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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class JdbcDriverManagerImpl implements JdbcDriverManager {

	public void setLogWriter(final PrintWriter out) {
		DriverManager.setLogWriter(out);
	}

	public Enumeration getDrivers() {
		return DriverManager.getDrivers();
	}

	public void registerDriver(final Driver driver) throws SQLException {
		final boolean driverLoad = driverLoad(driver);
		if (!driverLoad) {
			DriverManager.registerDriver(driver);
		}
	}

	private static boolean driverLoad(final Driver driver) {
		final Class clazz = driver.getClass();
		return driverLoad(clazz);
	}

	private static boolean driverLoad(final Class clazz) {
		final Enumeration drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {
			final java.sql.Driver driver = (java.sql.Driver) drivers.nextElement();

			final Class c = driver.getClass();
			if (c.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}
