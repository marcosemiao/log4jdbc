package fr.ms.log4jdbc.h2;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

	public final static String URL_H2 = "jdbc:h2:mem:test";// ;TRACE_LEVEL_SYSTEM_OUT=3";

	public final static String URL_H2_PROXY = "jdbc:log4" + URL_H2;

	public final static String USER = "SA";

	public final static String PASSWORD = "SA";

	public static final String getURL(final boolean proxy) {
		if (proxy) {
			return URL_H2_PROXY;
		} else {
			return URL_H2;
		}
	}

	public static final Driver getDriver(final String url) throws SQLException {
		return DriverManager.getDriver(url);
	}

	public static final Driver getDriver(final boolean proxy) throws SQLException {
		final String url = getURL(proxy);

		return getDriver(url);
	}

	public static final Connection createConnection(final boolean proxy) throws SQLException {
		final String url = getURL(proxy);
		return DriverManager.getConnection(url, USER, PASSWORD);
	}

	public static final void createDatabase(final Connection connection) throws SQLException {

		Statement statement = null;

		try {

			statement = connection.createStatement();

			statement.execute("DROP ALL OBJECTS DELETE FILES");
			statement.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
			statement.execute("RUNSCRIPT FROM 'classpath:data.sql'");
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static final void printResultSet(final ResultSet rs) throws SQLException {
		final ResultSetMetaData rsmd = rs.getMetaData();
		final int columnsNumber = rsmd.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) {
					System.out.print(",  ");
				}
				final String columnValue = rs.getString(i);
				System.out.print(columnValue + " " + rsmd.getColumnName(i));
			}
			System.out.println("");
		}
	}
}
