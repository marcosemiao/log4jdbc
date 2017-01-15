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
package fr.ms.log4jdbc.rdbms;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Db2RdbmsSpecificsTest {

	private final static RdbmsSpecifics instance = new Db2RdbmsSpecifics();

	@BeforeClass
	public static void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void isRdbmsTest() {
		boolean rdbms = instance.isRdbms(Object.class.getName());
		Assert.assertFalse(rdbms);

		rdbms = instance.isRdbms("com.ibm.db2.Driver");
		Assert.assertTrue(rdbms);

		rdbms = instance.isRdbms("COM.ibm.db2.Driver");
		Assert.assertTrue(rdbms);
	}

	@Test
	public void rdbmsNullObjectTest() {
		final DataRdbms data = instance.getData(null);

		Assert.assertEquals(data.getValue(), "NULL");
		Assert.assertEquals(data.getParameter(), "NULL");
	}

	@Test
	public void rdbmsStringTest() {
		final DataRdbms data = instance.getData("valeur");

		Assert.assertEquals(data.getValue(), "valeur");
		Assert.assertEquals(data.getParameter(), "'valeur'");
	}

	@Test
	public void rdbmsStringAvecEspaceTest() {
		final DataRdbms data = instance.getData("valeur espace");

		Assert.assertEquals(data.getValue(), "valeur espace");
		Assert.assertEquals(data.getParameter(), "'valeur espace'");
	}

	@Test
	public void rdbmsStringAvecEspaceDebutTest() {
		final DataRdbms data = instance.getData("  valeur espace   ");

		Assert.assertEquals(data.getValue(), "  valeur espace   ");
		Assert.assertEquals(data.getParameter(), "'  valeur espace   '");
	}

	@Test
	public void rdbmsTimeStampTest() {
		final Timestamp timestamp = new Timestamp(1446040543364L);

		final DataRdbms data = instance.getData(timestamp);

		Assert.assertEquals(data.getValue(), "2015-10-28 13:55:43.364000000");
		Assert.assertEquals(data.getParameter(), "'2015-10-28 13:55:43.364000000'");
	}

	@Test
	public void rdbmsDateTest() {
		final Date date = new Date(1446040543364L);

		final DataRdbms data = instance.getData(date);

		Assert.assertEquals(data.getValue(), "2015-10-28 13:55:43.364");
		Assert.assertEquals(data.getParameter(), "'2015-10-28 13:55:43.364'");
	}

	@Test
	public void rdbmsBooleanTest() {
		final Boolean trueValue = new Boolean(true);

		DataRdbms data = instance.getData(trueValue);

		Assert.assertEquals(data.getValue(), "1");
		Assert.assertEquals(data.getParameter(), "'1'");

		final Boolean falseValue = new Boolean(false);

		data = instance.getData(falseValue);

		Assert.assertEquals(data.getValue(), "0");
		Assert.assertEquals(data.getParameter(), "'0'");
	}

	@Test
	public void queryTypeTest() {
		String sql = null;
		String typeQuery = instance.getTypeQuery(sql);
		Assert.assertNull(typeQuery);

		sql = "  ";
		typeQuery = instance.getTypeQuery(sql);
		Assert.assertNull(typeQuery);

		sql = "toto";
		typeQuery = instance.getTypeQuery(sql);
		Assert.assertNull(typeQuery);

		sql = "select Price from products;";
		typeQuery = instance.getTypeQuery(sql);
		Assert.assertEquals(typeQuery, "select");

		sql = "/* super requete */ select Price from products;";
		typeQuery = instance.getTypeQuery(sql);
		Assert.assertEquals(typeQuery, "select");

		sql = "/* super requete */ select Price from products; /* genial */";
		typeQuery = instance.getTypeQuery(sql);
		Assert.assertEquals(typeQuery, "select");
	}

	@Test
	public void removeCommentTest() {
		String sql = null;
		String removeComment = instance.removeComment(sql);
		Assert.assertNull(removeComment);

		sql = "  ";
		removeComment = instance.removeComment(sql);
		Assert.assertEquals(removeComment, "");

		sql = "toto";
		removeComment = instance.removeComment(sql);
		Assert.assertEquals(removeComment, "toto");

		sql = "select Price from products;";
		removeComment = instance.removeComment(sql);
		Assert.assertEquals(removeComment, "select Price from products;");

		sql = "/* super requete */ select Price from products;";
		removeComment = instance.removeComment(sql);
		Assert.assertEquals(removeComment, "select Price from products;");

		sql = "/* super requete */ select Price from products; /* genial */";
		removeComment = instance.removeComment(sql);
		Assert.assertEquals(removeComment, "select Price from products;");
	}
}
