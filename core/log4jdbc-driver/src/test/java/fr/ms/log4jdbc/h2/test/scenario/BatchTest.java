package fr.ms.log4jdbc.h2.test.scenario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sqloperation.SqlMessage;
import fr.ms.log4jdbc.sqloperation.SqlOperationMessage;

public class BatchTest {

    private final SqlMessage messages = SqlMessage.getInstance();

    @After
    public void clear() {
	messages.clear();
    }

    @Test
    public void simpleBatchTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = DatabaseUtil.createConnection(true);
	    DatabaseUtil.createDatabase(connection);

	    clear();
	    connection.setAutoCommit(false);

	    statement = connection.createStatement();

	    final String query1 = "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');";
	    statement.addBatch(query1);

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack5', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack6', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack7', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack8', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack9', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack10', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack11', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack12', 'SQL', '1970-01-01');");

	    statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack13', 'SQL', '1970-01-01');");

	    final int[] count = statement.executeBatch();

	    Assert.assertEquals(11, count.length);

	    for (final int i : count) {
		Assert.assertEquals(1, i);
	    }

	    connection.commit();

	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    final SqlOperation sqlOperation0 = sqlMessages.get(0).getSqlOperation();
	    Assert.assertNull(sqlOperation0.getQuery());
	    Assert.assertNull(sqlOperation0.getTransaction());

	    final SqlOperation sqlOperation1 = sqlMessages.get(1).getSqlOperation();
	    Assert.assertNull(sqlOperation1.getQuery());
	    Assert.assertNull(sqlOperation1.getTransaction());

	    final SqlOperation sqlOperation2 = sqlMessages.get(2).getSqlOperation();
	    Assert.assertNotNull(sqlOperation2.getQuery());
	    Assert.assertEquals(Query.METHOD_BATCH, sqlOperation2.getQuery().getMethodQuery());
	    Assert.assertEquals(Query.STATE_NOT_EXECUTE, sqlOperation2.getQuery().getState());
	    Assert.assertEquals(query1, sqlOperation2.getQuery().getJDBCQuery());
	    Assert.assertEquals(query1, sqlOperation2.getQuery().getSQLQuery());
	    Assert.assertNotNull(sqlOperation2.getTransaction());
	    Assert.assertEquals("JDBC", sqlOperation2.getTransaction().getTransactionType());
	    Assert.assertEquals(Transaction.STATE_NOT_EXECUTE, sqlOperation2.getTransaction().getTransactionState());
	    Assert.assertEquals(1, sqlOperation2.getTransaction().getOpenTransaction());

	    final SqlOperation sqlOperation3 = sqlMessages.get(3).getSqlOperation();
	    final SqlOperation sqlOperation4 = sqlMessages.get(4).getSqlOperation();
	    final SqlOperation sqlOperation5 = sqlMessages.get(5).getSqlOperation();
	    final SqlOperation sqlOperation6 = sqlMessages.get(6).getSqlOperation();
	    final SqlOperation sqlOperation7 = sqlMessages.get(7).getSqlOperation();
	    final SqlOperation sqlOperation8 = sqlMessages.get(8).getSqlOperation();
	    final SqlOperation sqlOperation9 = sqlMessages.get(9).getSqlOperation();
	    final SqlOperation sqlOperation10 = sqlMessages.get(10).getSqlOperation();
	    final SqlOperation sqlOperation11 = sqlMessages.get(11).getSqlOperation();
	    final SqlOperation sqlOperation12 = sqlMessages.get(12).getSqlOperation();
	    final SqlOperation sqlOperation13 = sqlMessages.get(13).getSqlOperation();
	    final SqlOperation sqlOperation14 = sqlMessages.get(14).getSqlOperation();
	    final SqlOperation sqlOperation15 = sqlMessages.get(15).getSqlOperation();
	    final SqlOperation sqlOperation16 = sqlMessages.get(16).getSqlOperation();

	    final ResultSet resultSet = statement.executeQuery("SELECT * FROM PERSONNE;");

	    DatabaseUtil.printResultSet(resultSet);

	} finally {

	    if (statement != null) {
		statement.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	}
    }
}
