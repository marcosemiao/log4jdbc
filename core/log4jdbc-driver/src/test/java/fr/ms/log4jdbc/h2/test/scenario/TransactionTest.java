package fr.ms.log4jdbc.h2.test.scenario;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.h2.Driver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sqloperation.SqlMessage;
import fr.ms.log4jdbc.sqloperation.SqlOperationMessage;

public class TransactionTest {

	private final SqlMessage messages = SqlMessage.getInstance();

	@After
	public void clear() {
		messages.clear();
	}

	@Test
	public void simpleTransactionTest() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = DatabaseUtil.createConnection(true);
			DatabaseUtil.createDatabase(connection);

			messages.clear();

			// ///////////////////// Set Auto Commit False - Debut Transaction
			connection.setAutoCommit(false);
			final List<SqlOperationMessage> sqlMessages1 = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages1.size());

			// SqlOperationMessage
			final SqlOperationMessage sqlOperationMessage1 = sqlMessages1.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage1.getTypeLogger());
			Assert.assertEquals(false, sqlOperationMessage1.getArgs()[0]);
			Assert.assertNotNull(sqlOperationMessage1.getMethod());
			Assert.assertNull(sqlOperationMessage1.getInvoke());
			Assert.assertNull(sqlOperationMessage1.getException());

			// SqlOperation
			final SqlOperation sqlOperation1 = sqlOperationMessage1.getSqlOperation();
			Assert.assertTrue(sqlOperation1.getConnectionNumber() >= sqlOperation1.getOpenConnection());
			Assert.assertEquals(1, sqlOperation1.getOpenConnection());
			Assert.assertNotNull(sqlOperation1.getDate());
			Assert.assertNotNull(sqlOperation1.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation1.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation1.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation1.getUrl());

			Assert.assertNull(sqlOperation1.getTransaction());

			// Query
			final Query query1 = sqlOperation1.getQuery();
			Assert.assertNull(query1);

			// ///////////////////// create statement
			statement = connection.createStatement();

			final List<SqlOperationMessage> sqlMessages2 = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages2.size());

			// SqlOperationMessage
			final SqlOperationMessage sqlOperationMessage2 = sqlMessages2.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage2.getTypeLogger());
			Assert.assertNull(sqlOperationMessage2.getArgs());
			Assert.assertNotNull(sqlOperationMessage2.getMethod());
			Assert.assertNotNull(sqlOperationMessage2.getInvoke());
			Assert.assertTrue(sqlOperationMessage2.getInvoke() instanceof Statement);
			Assert.assertNull(sqlOperationMessage2.getException());

			// SqlOperation
			final SqlOperation sqlOperation2 = sqlOperationMessage2.getSqlOperation();
			Assert.assertTrue(sqlOperation2.getConnectionNumber() >= sqlOperation2.getOpenConnection());
			Assert.assertEquals(1, sqlOperation2.getOpenConnection());
			Assert.assertNotNull(sqlOperation2.getDate());
			Assert.assertNotNull(sqlOperation2.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation2.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation2.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation2.getUrl());

			Assert.assertNull(sqlOperation2.getTransaction());

			// Query
			final Query query2 = sqlOperation2.getQuery();
			Assert.assertNull(query2);

			// Execute Query - INSERT
			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");

			final List<SqlOperationMessage> sqlMessages3 = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages3.size());

			// SqlOperationMessage
			final SqlOperationMessage sqlOperationMessage3 = sqlMessages3.get(0);

			Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage3.getTypeLogger());
			Assert.assertEquals(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');",
					sqlOperationMessage3.getArgs()[0]);
			Assert.assertNotNull(sqlOperationMessage3.getMethod());
			Assert.assertEquals(false, sqlOperationMessage3.getInvoke());
			Assert.assertNull(sqlOperationMessage3.getException());

			// SqlOperation
			final SqlOperation sqlOperation3 = sqlOperationMessage3.getSqlOperation();
			Assert.assertTrue(sqlOperation3.getConnectionNumber() >= sqlOperation3.getOpenConnection());
			Assert.assertEquals(1, sqlOperation3.getOpenConnection());
			Assert.assertNotNull(sqlOperation3.getDate());
			Assert.assertNotNull(sqlOperation3.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation3.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation3.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation3.getUrl());

			Assert.assertNotNull(sqlOperation3.getTransaction());

			// Query
			final Query query3 = sqlOperation3.getQuery();

			Assert.assertNotNull(query3);
			Assert.assertNotNull(query3.getDate());
			Assert.assertNotNull(query3.getExecTime());
			Assert.assertEquals(query3.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(query3.getJDBCParameters().size(), 0);
			Assert.assertEquals(query3.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(query3.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(query3.getState(), Query.STATE_EXECUTE);
			Assert.assertNull(query3.getResultSetCollector());
			Assert.assertNotNull(query3.getTransaction());
			Assert.assertEquals(query3.getTransaction().getTransactionState(), Transaction.STATE_EXECUTE);
			Assert.assertEquals(query3.getTransaction().getQueriesTransaction().length, 1);
			final Query transactionQuery3 = query3.getTransaction().getQueriesTransaction()[0];

			Assert.assertEquals(transactionQuery3, query3);

			// Bidon
			final int maxRows = statement.getMaxRows();

			final List<SqlOperationMessage> sqlMessagesBidon = messages.getSqlMessages();

			// Commit - Fin de la Transaction
			connection.commit();

			final List<SqlOperationMessage> sqlMessages4 = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages4.size());

			// SqlOperationMessage
			final SqlOperationMessage sqlOperationMessage4 = sqlMessages4.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage4.getTypeLogger());
			Assert.assertNull(sqlOperationMessage4.getArgs());
			Assert.assertNotNull(sqlOperationMessage4.getMethod());
			Assert.assertNull(sqlOperationMessage4.getInvoke());
			Assert.assertNull(sqlOperationMessage4.getException());

			// SqlOperation
			final SqlOperation sqlOperation4 = sqlOperationMessage4.getSqlOperation();
			Assert.assertTrue(sqlOperation4.getConnectionNumber() >= sqlOperation4.getOpenConnection());
			Assert.assertEquals(1, sqlOperation4.getOpenConnection());
			Assert.assertNotNull(sqlOperation4.getDate());
			Assert.assertNotNull(sqlOperation4.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation4.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation4.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation4.getUrl());

			Assert.assertNotNull(sqlOperation4.getTransaction());
			Assert.assertEquals(query3.getTransaction().getTransactionState(), Transaction.STATE_EXECUTE);
			Assert.assertEquals(query3.getTransaction().getQueriesTransaction().length, 1);
			final Query transactionQuery3Bis = query3.getTransaction().getQueriesTransaction()[0];

			Assert.assertNotNull(transactionQuery3Bis.getDate());
			Assert.assertNotNull(transactionQuery3Bis.getExecTime());
			Assert.assertEquals(transactionQuery3Bis.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery3Bis.getJDBCParameters().size(), 0);
			Assert.assertEquals(transactionQuery3Bis.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery3Bis.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(transactionQuery3Bis.getState(), Query.STATE_EXECUTE);
			Assert.assertNull(transactionQuery3Bis.getResultSetCollector());

			final Query query4 = sqlOperation4.getQuery();
			Assert.assertNull(query4);
		} finally {

			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Test
	public void plusieursRequeteTransactionTest() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = DatabaseUtil.createConnection(true);
			DatabaseUtil.createDatabase(connection);

			messages.clear();
			// Set Auto Commit False - Debut Transaction
			connection.setAutoCommit(false);
			List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages.size());

			SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
			Assert.assertEquals(false, sqlOperationMessage.getArgs()[0]);
			Assert.assertNotNull(sqlOperationMessage.getMethod());
			Assert.assertNull(sqlOperationMessage.getInvoke());
			Assert.assertNull(sqlOperationMessage.getException());

			SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();
			Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
			Assert.assertEquals(1, sqlOperation.getOpenConnection());
			Assert.assertNotNull(sqlOperation.getDate());
			Assert.assertNotNull(sqlOperation.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation.getUrl());

			Assert.assertNull(sqlOperation.getTransaction());

			Query query = sqlOperation.getQuery();
			Assert.assertNull(query);

			// create statement
			statement = connection.createStatement();

			sqlMessages = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages.size());

			sqlOperationMessage = sqlMessages.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
			Assert.assertNull(sqlOperationMessage.getArgs());
			Assert.assertNotNull(sqlOperationMessage.getMethod());
			Assert.assertNotNull(sqlOperationMessage.getInvoke());
			Assert.assertTrue(sqlOperationMessage.getInvoke() instanceof Statement);
			Assert.assertNull(sqlOperationMessage.getException());

			sqlOperation = sqlOperationMessage.getSqlOperation();
			Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
			Assert.assertEquals(1, sqlOperation.getOpenConnection());
			Assert.assertNotNull(sqlOperation.getDate());
			Assert.assertNotNull(sqlOperation.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation.getUrl());

			Assert.assertNull(sqlOperation.getTransaction());

			query = sqlOperation.getQuery();
			Assert.assertNull(query);

			// Execute Query - INSERT
			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");

			sqlMessages = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages.size());

			sqlOperationMessage = sqlMessages.get(0);

			Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());
			Assert.assertEquals(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');",
					sqlOperationMessage.getArgs()[0]);
			Assert.assertNotNull(sqlOperationMessage.getMethod());
			Assert.assertEquals(false, sqlOperationMessage.getInvoke());
			Assert.assertNull(sqlOperationMessage.getException());

			sqlOperation = sqlOperationMessage.getSqlOperation();
			Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
			Assert.assertEquals(1, sqlOperation.getOpenConnection());
			Assert.assertNotNull(sqlOperation.getDate());
			Assert.assertNotNull(sqlOperation.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation.getUrl());

			final Transaction transaction1 = sqlOperation.getTransaction();
			Assert.assertNotNull(transaction1);

			final Query query1 = sqlOperation.getQuery();
			Assert.assertNotNull(query1);
			Assert.assertNotNull(query1.getDate());
			Assert.assertNotNull(query1.getExecTime());
			Assert.assertEquals(query1.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(query1.getJDBCParameters().size(), 0);
			Assert.assertEquals(query1.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(query1.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(query1.getState(), Query.STATE_EXECUTE);
			Assert.assertNull(query1.getResultSetCollector());
			Assert.assertNotNull(query1.getTransaction());
			Assert.assertEquals(query1.getTransaction().getTransactionState(), Transaction.STATE_EXECUTE);
			Assert.assertEquals(query1.getTransaction().getQueriesTransaction().length, 1);
			Query transactionQuery1 = query1.getTransaction().getQueriesTransaction()[0];

			Assert.assertEquals(transactionQuery1, query1);
			Assert.assertEquals(query1.getTransaction(), transaction1);

			// Execute Query - INSERT 2
			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");

			sqlMessages = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages.size());

			sqlOperationMessage = sqlMessages.get(0);

			Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());
			Assert.assertEquals(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');",
					sqlOperationMessage.getArgs()[0]);
			Assert.assertNotNull(sqlOperationMessage.getMethod());
			Assert.assertEquals(false, sqlOperationMessage.getInvoke());
			Assert.assertNull(sqlOperationMessage.getException());

			sqlOperation = sqlOperationMessage.getSqlOperation();
			Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
			Assert.assertEquals(1, sqlOperation.getOpenConnection());
			Assert.assertNotNull(sqlOperation.getDate());
			Assert.assertNotNull(sqlOperation.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation.getUrl());

			final Transaction transaction2 = sqlOperation.getTransaction();
			Assert.assertNotNull(transaction2);

			final Query query2 = sqlOperation.getQuery();
			Assert.assertNotNull(query2);
			Assert.assertNotNull(query2.getDate());
			Assert.assertNotNull(query2.getExecTime());
			Assert.assertEquals(query2.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
			Assert.assertEquals(query2.getJDBCParameters().size(), 0);
			Assert.assertEquals(query2.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
			Assert.assertEquals(query2.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(query2.getState(), Query.STATE_EXECUTE);
			Assert.assertNull(query2.getResultSetCollector());
			Assert.assertNotNull(query2.getTransaction());
			Assert.assertEquals(query2.getTransaction().getTransactionState(), Transaction.STATE_EXECUTE);
			Assert.assertEquals(query2.getTransaction().getQueriesTransaction().length, 2);
			final Query transactionQuery2 = query2.getTransaction().getQueriesTransaction()[1];

			Assert.assertEquals(transactionQuery2, query2);
			Assert.assertEquals(query2.getTransaction(), transaction2);

			// Commit - Fin de la Transaction
			connection.commit();

			sqlMessages = messages.getSqlMessages();

			Assert.assertEquals(1, sqlMessages.size());

			sqlOperationMessage = sqlMessages.get(0);

			Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
			Assert.assertNull(sqlOperationMessage.getArgs());
			Assert.assertNotNull(sqlOperationMessage.getMethod());
			Assert.assertNull(sqlOperationMessage.getInvoke());
			Assert.assertNull(sqlOperationMessage.getException());

			sqlOperation = sqlOperationMessage.getSqlOperation();
			Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
			Assert.assertEquals(1, sqlOperation.getOpenConnection());
			Assert.assertNotNull(sqlOperation.getDate());
			Assert.assertNotNull(sqlOperation.getExecTime());
			Assert.assertEquals(Driver.class.getName(), sqlOperation.getDriverName());
			Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
			Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperation.getUrl());

			Assert.assertNotNull(sqlOperation.getTransaction());

			Assert.assertEquals(sqlOperation.getTransaction().getTransactionState(), Transaction.STATE_COMMIT);
			Assert.assertEquals(sqlOperation.getTransaction().getQueriesTransaction().length, 2);

			transactionQuery1 = sqlOperation.getTransaction().getQueriesTransaction()[0];
			Assert.assertNotNull(transactionQuery1.getDate());
			Assert.assertNotNull(transactionQuery1.getExecTime());
			Assert.assertEquals(transactionQuery1.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getJDBCParameters().size(), 0);
			Assert.assertEquals(transactionQuery1.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(transactionQuery1.getState(), Query.STATE_COMMIT);
			Assert.assertNull(transactionQuery1.getResultSetCollector());

			transactionQuery1 = sqlOperation.getTransaction().getQueriesTransaction()[1];
			Assert.assertNotNull(transactionQuery1.getDate());
			Assert.assertNotNull(transactionQuery1.getExecTime());
			Assert.assertEquals(transactionQuery1.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getJDBCParameters().size(), 0);
			Assert.assertEquals(transactionQuery1.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(transactionQuery1.getState(), Query.STATE_COMMIT);
			Assert.assertNull(transactionQuery1.getResultSetCollector());

			Assert.assertEquals(query1.getTransaction().getTransactionState(), Transaction.STATE_EXECUTE);
			Assert.assertEquals(query1.getTransaction().getQueriesTransaction().length, 1);
			transactionQuery1 = query1.getTransaction().getQueriesTransaction()[0];

			Assert.assertNotNull(transactionQuery1.getDate());
			Assert.assertNotNull(transactionQuery1.getExecTime());
			Assert.assertEquals(transactionQuery1.getJDBCQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getJDBCParameters().size(), 0);
			Assert.assertEquals(transactionQuery1.getSQLQuery(),
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
			Assert.assertEquals(transactionQuery1.getMethodQuery(), Query.METHOD_EXECUTE);
			Assert.assertEquals(transactionQuery1.getState(), Query.STATE_EXECUTE);
			Assert.assertNull(transactionQuery1.getResultSetCollector());

			query = sqlOperation.getQuery();
			Assert.assertNull(query);
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
