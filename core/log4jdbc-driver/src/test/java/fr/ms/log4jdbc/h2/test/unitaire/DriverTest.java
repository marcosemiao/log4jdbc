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
package fr.ms.log4jdbc.h2.test.unitaire;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.ms.log4jdbc.h2.DatabaseUtil;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DriverTest {

	@Test
	public void minorVersionTest() throws SQLException {
		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		final int minorVersion = driver.getMinorVersion();

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		final int minorVersionProxy = driverProxy.getMinorVersion();

		Assert.assertEquals(minorVersion, minorVersionProxy);
	}

	@Test
	public void majorVersionTest() throws SQLException {
		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		final int majorVersion = driver.getMajorVersion();

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		final int majorVersionProxy = driverProxy.getMajorVersion();

		Assert.assertEquals(majorVersion, majorVersionProxy);
	}

	@Test
	public void acceptsURLTest() throws SQLException {
		final String url = DatabaseUtil.getURL(false);
		final String urlProxy = DatabaseUtil.getURL(true);

		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		boolean acceptsURL = driver.acceptsURL(url);

		Assert.assertTrue(acceptsURL);

		acceptsURL = driver.acceptsURL(urlProxy);

		Assert.assertFalse(acceptsURL);

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		boolean acceptsURLProxy = driverProxy.acceptsURL(urlProxy);

		Assert.assertTrue(acceptsURLProxy);

		acceptsURLProxy = driverProxy.acceptsURL(url);

		Assert.assertFalse(acceptsURLProxy);
	}

	@Test
	public void jdbcCompliantTest() throws SQLException {
		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		final boolean jdbcCompliant = driver.jdbcCompliant();

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		final boolean jdbcCompliantProxy = driverProxy.jdbcCompliant();

		Assert.assertEquals(jdbcCompliant, jdbcCompliantProxy);
	}

	@Test
	public void propertyInfoTest() throws SQLException {
		final String url = DatabaseUtil.getURL(false);
		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		final DriverPropertyInfo[] info = driver.getPropertyInfo(url, null);

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		final DriverPropertyInfo[] infoProxy = driverProxy.getPropertyInfo(url, null);

		Assert.assertArrayEquals(info, infoProxy);
	}

	// H2 not implemented getParentLogger Method
	@Ignore
	@Test
	public void parentLoggerTest() throws SQLException {
		// Real Driver
		final Driver driver = DatabaseUtil.getDriver(false);

		final Logger parentLogger = driver.getParentLogger();

		// Proxy Driver
		final Driver driverProxy = DatabaseUtil.getDriver(true);

		final Logger parentLoggerProxy = driverProxy.getParentLogger();

		Assert.assertEquals(parentLogger, parentLoggerProxy);
	}
}
