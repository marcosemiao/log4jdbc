package fr.ms.log4jdbc.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

import fr.ms.log4jdbc.sqloperation.SqlMessage;

public class SelectTest {

	private final SqlMessage messages = SqlMessage.getInstance();

	@After
	public void clear() {
		messages.clear();
	}

	@Test
	public void getResultetTest() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = DatabaseUtil.createConnection(true);
			DatabaseUtil.createDatabase(connection);

			connection.setAutoCommit(false);

			statement = connection.createStatement();

			statement.execute("SELECT * FROM PERSONNE;");

			resultSet = statement.getResultSet();

			DatabaseUtil.printResultSet(resultSet);
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
}
