package fr.ms.log4jdbc.h2.test.scenario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sqloperation.SqlMessage;
import fr.ms.log4jdbc.sqloperation.SqlOperationMessage;

public class RollBackTest {

    private final SqlMessage messages = SqlMessage.getInstance();

    @After
    public void clear() {
	messages.clear();
    }

    @Test
    public void rollbackTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = DatabaseUtil.createConnection(true);
	    DatabaseUtil.createDatabase(connection);

	    connection.setAutoCommit(false);

	    statement = connection.createStatement();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	    statement.execute("UPDATE PERSONNE set PRENOM = 'RollBack4Update' where PRENOM = 'RollBack4';");

	    statement.execute("DELETE PERSONNE where PRENOM = 'RollBack2';");

	    messages.clear();

	    connection.rollback();

	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();
	    final Transaction transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_ROLLBACK);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	} finally {

	    if (statement != null) {
		statement.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @SuppressWarnings("unused")
    @Test
    public void rollbackSavePointTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;
	try {
	    connection = DatabaseUtil.createConnection(true);
	    DatabaseUtil.createDatabase(connection);

	    connection.setAutoCommit(false);

	    statement = connection.createStatement();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

	    final Savepoint savepoint1 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");
	    final Savepoint savepoint2 = connection.setSavepoint();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");
	    final Savepoint savepoint3 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	    final Savepoint savepoint4 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack5', 'SQL', '1970-01-01');");

	    connection.releaseSavepoint(savepoint2);
	    final Savepoint savepoint5 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack6', 'SQL', '1970-01-01');");

	    connection.releaseSavepoint(savepoint5);
	    final Savepoint savepoint6 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack7', 'SQL', '1970-01-01');");

	    messages.clear();

	    final Savepoint savepoint7 = connection.setSavepoint();

	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();
	    Transaction transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_EXECUTE);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_EXECUTE);

	    connection.rollback(savepoint3);

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_ROLLBACK);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack8', 'SQL', '1970-01-01');");

	    messages.clear();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack9', 'SQL', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_EXECUTE);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[7].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[8].getState(), Query.STATE_EXECUTE);

	    connection.commit();

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_COMMIT);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[7].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[8].getState(), Query.STATE_COMMIT);
	    resultSet = statement.executeQuery("SELECT * FROM PERSONNE;");

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

    @SuppressWarnings("unused")
    @Test
    public void rollbackSavePointAvecNomTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;
	try {
	    connection = DatabaseUtil.createConnection(true);
	    DatabaseUtil.createDatabase(connection);

	    connection.setAutoCommit(false);

	    statement = connection.createStatement();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

	    final Savepoint savepoint1 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");
	    final Savepoint savepoint2 = connection.setSavepoint();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");
	    final Savepoint savepoint3 = connection.setSavepoint("point3");
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	    final Savepoint savepoint4 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack5', 'SQL', '1970-01-01');");

	    connection.releaseSavepoint(savepoint2);
	    final Savepoint savepoint5 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack6', 'SQL', '1970-01-01');");

	    connection.releaseSavepoint(savepoint5);
	    final Savepoint savepoint6 = connection.setSavepoint("point3");
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack7', 'SQL', '1970-01-01');");

	    messages.clear();

	    final Savepoint savepoint7 = connection.setSavepoint();

	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();
	    Transaction transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_EXECUTE);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_EXECUTE);

	    connection.rollback(savepoint3);

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_ROLLBACK);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack8', 'SQL', '1970-01-01');");

	    messages.clear();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack9', 'SQL', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_EXECUTE);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[7].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[8].getState(), Query.STATE_EXECUTE);

	    connection.commit();

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_COMMIT);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[6].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[7].getState(), Query.STATE_COMMIT);
	    Assert.assertEquals(transaction.getQueriesTransaction()[8].getState(), Query.STATE_COMMIT);
	    resultSet = statement.executeQuery("SELECT * FROM PERSONNE;");

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

    @Test
    public void rollbackSavePointAvecReleaseTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = DatabaseUtil.createConnection(true);
	    DatabaseUtil.createDatabase(connection);

	    connection.setAutoCommit(false);

	    statement = connection.createStatement();

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");

	    final Savepoint savepoint1 = connection.setSavepoint();
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");

	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	    final Savepoint savepoint2 = connection.setSavepoint();
	    statement.execute("UPDATE PERSONNE set PRENOM = 'RollBack4Update' where PRENOM = 'RollBack4';");

	    connection.releaseSavepoint(savepoint1);

	    messages.clear();

	    statement.execute("DELETE PERSONNE where PRENOM = 'RollBack2';");

	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();
	    Transaction transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_EXECUTE);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_EXECUTE);
	    connection.rollback(savepoint2);

	    sqlMessages = messages.getSqlMessages();
	    transaction = sqlMessages.get(0).getSqlOperation().getTransaction();
	    Assert.assertEquals(transaction.getTransactionState(), Transaction.STATE_ROLLBACK);

	    Assert.assertEquals(transaction.getQueriesTransaction()[0].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[1].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[2].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[3].getState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(transaction.getQueriesTransaction()[4].getState(), Query.STATE_ROLLBACK);
	    Assert.assertEquals(transaction.getQueriesTransaction()[5].getState(), Query.STATE_ROLLBACK);
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
