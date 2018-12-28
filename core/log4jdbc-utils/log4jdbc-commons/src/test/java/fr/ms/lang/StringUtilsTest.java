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
package fr.ms.log4jdbc.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class StringUtilsTest {

	@Test
	public void padRightTest() {
		final String padRight = StringUtils.padRight("s", 10);

		Assert.assertEquals("ssssssssss", padRight);

	}

	@Test
	public void startPadRightTest() {
		final String padRight = StringUtils.padRight("debut", "s", 10);

		Assert.assertEquals("debutsssss", padRight);
	}

	@Test
	public void replaceAllTest() {
		String sql = "select Price\nfrom products;";
		String replace = StringUtils.replaceAll(sql, "\n", " ");

		Assert.assertEquals("select Price from products;", replace);

		sql = "select Price\rfrom products;";
		replace = StringUtils.replaceAll(sql, "\r", " ");

		Assert.assertEquals("select Price from products;", replace);

		sql = "select Price\\from products;";
		replace = StringUtils.replaceAll(sql, "\\", "\\\\");

		Assert.assertEquals("select Price\\\\from products;", replace);
	}

	@Test
	public void removePartTest() {
		String sql = null;
		String removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertNull(removePart);

		sql = "  ";
		removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertEquals(removePart, "  ");

		sql = "toto";
		removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertEquals(removePart, "toto");

		sql = "select Price from products;";
		removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertEquals(removePart, "select Price from products;");

		sql = "/* super requete */ select Price from products;";
		removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertEquals(removePart, " select Price from products;");

		sql = "/* super requete */ select Price from products; /* genial */";
		removePart = StringUtils.removePart(sql, "/*", "*/");
		Assert.assertEquals(removePart, " select Price from products; ");
		StringUtils.removePart(sql, "/*", "*/");

	}

	@Test
	public void beginPartTest() {
		String sql = null;
		int removePart = StringUtils.beginPart(sql, "/*", "*/", null, 0);
		Assert.assertEquals(removePart, -1);

		sql = "  ";
		removePart = StringUtils.beginPart(sql, "/*", "*/", null, 0);
		Assert.assertEquals(removePart, -1);

		sql = "toto";
		removePart = StringUtils.beginPart(sql, "/*", "*/", null, 0);
		Assert.assertEquals(removePart, -1);

		sql = "select Price from products;";
		removePart = StringUtils.beginPart(sql, "/*", "*/", null, 0);
		Assert.assertEquals(removePart, -1);

		sql = "/* super requete */ select Price from products;";
		removePart = StringUtils.beginPart(sql, "/*", "*/", null, 0);
		Assert.assertEquals(removePart, 19);

		sql = "/* super requete */ select Price from products; /* genial */";
		removePart = StringUtils.beginPart(sql, "/*", "*/", null, removePart);
		Assert.assertEquals(removePart, 19);
	}
}
