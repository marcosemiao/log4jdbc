package fr.ms.log4jdbc.h2.test.unitaire;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.sql.Date;

import org.junit.After;
import org.junit.Test;

import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.sqloperation.SqlMessage;
import fr.ms.log4jdbc.sqloperation.SqlOperationMessage;

public class PreparedStatementTest {

	private final SqlMessage messages = SqlMessage.getInstance();

	@After
	public void clear() {
		messages.clear();
	}

	@Test
	public void simpleTransactionTest() throws SQLException, IOException {

		Connection connection = DatabaseUtil.createConnection(true);
		DatabaseUtil.createDatabase(connection);

		PreparedStatement prepareStatement = connection
				.prepareStatement("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES (?, ?, ?)");

		String a = "super";
		prepareStatement.setCharacterStream(1, new StringReader(a), a.length());

		String b = "super";
		prepareStatement.setCharacterStream(2, new StringReader(b), b.length());

		prepareStatement.setDate(3, new Date(System.currentTimeMillis()));

		prepareStatement.execute();

		System.out.println(messages);

		List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();
		int index = sqlMessages.size() - 1;
		SqlOperationMessage message = sqlMessages.get(index);

		String sqlQuery = message.getSqlOperation().getQuery().getSQLQuery();

		Map jdbcParameters = message.getSqlOperation().getQuery().getJDBCParameters();
		Set entrySet = jdbcParameters.entrySet();

		Iterator entries = entrySet.iterator();
		while (entries.hasNext()) {
			Entry thisEntry = (Entry) entries.next();
			Object key = thisEntry.getKey();
			Object value = thisEntry.getValue();
			
			System.out.println("type : " + value);
			if (value instanceof Reader)
			{
				Reader reader = (Reader) value;
				int intValueOfChar;
			    String targetString = "";
			    while ((intValueOfChar = reader.read()) != -1) {
			        targetString += (char) intValueOfChar;
			    }
			    
			    value = targetString;
			}
			System.out.println(key + " : " + value);
		}
		
		
		Statement createStatement = connection.createStatement();
		
		ResultSet resultSet = createStatement.executeQuery("select * from PERSONNE");
		
		DatabaseUtil.printResultSet(resultSet);
	}
}
