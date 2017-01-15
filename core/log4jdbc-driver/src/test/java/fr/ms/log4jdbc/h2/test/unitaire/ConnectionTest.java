package fr.ms.log4jdbc.h2.test.unitaire;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import org.h2.Driver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.sqloperation.SqlMessage;
import fr.ms.log4jdbc.sqloperation.SqlOperationMessage;

public class ConnectionTest {

    private final SqlMessage messages = SqlMessage.getInstance();

    @After
    public void clear() {
	messages.clear();
    }

    @Test
    public void closeTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);
	} finally {

	    if (connection != null) {
		connection.close();
		final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

		Assert.assertEquals(1, sqlMessages.size());

		final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

		Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());

		Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
		Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

		final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

		Assert.assertNull(sqlOperation.getTransaction());
		Assert.assertNull(sqlOperation.getQuery());
	    }
	}
    }

    @Test
    public void commitTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.commit();
	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void setAutoCommitFalseTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void setAutoCommitFalseTrueTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getQuery());

	    connection.setAutoCommit(true);
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void rollbackTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.rollback();
	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void transactionRollbackTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    connection.rollback();
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void savePointTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    final Savepoint savepoint = connection.setSavepoint();
	    Assert.assertNotNull(savepoint);

	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void savePointNameTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    final Savepoint savepoint = connection.setSavepoint("1point");
	    Assert.assertNotNull(savepoint);

	    final List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    final SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    final SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void transactionSavePointTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    connection.setSavepoint();
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void transactionSavePointNameTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    connection.setSavepoint("1point");
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void transactionSavePointRollbackTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    final Savepoint savepoint = connection.setSavepoint();
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    connection.rollback(savepoint);

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void transactionSavePointNameRollbackTest() throws SQLException {
	Connection connection = null;

	try {
	    connection = DatabaseUtil.createConnection(true);

	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    final Savepoint savepoint = connection.setSavepoint("1point");
	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());

	    connection.rollback(savepoint);

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals(1, sqlOperationMessage.getSqlOperation().getOpenConnection());
	    Assert.assertEquals(Driver.class.getName(), sqlOperationMessage.getSqlOperation().getDriverName());
	    Assert.assertEquals(DatabaseUtil.getURL(false), sqlOperationMessage.getSqlOperation().getUrl());

	    sqlOperation = sqlOperationMessage.getSqlOperation();

	    Assert.assertNull(sqlOperation.getTransaction());

	    Assert.assertNull(sqlOperation.getQuery());
	} finally {

	    if (connection != null) {
		connection.close();
	    }
	}
    }
}
