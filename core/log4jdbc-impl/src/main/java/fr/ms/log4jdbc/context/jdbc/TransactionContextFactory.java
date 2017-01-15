package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;

public interface TransactionContextFactory {

	TransactionContextJDBC newTransactionContext(Connection connection, ConnectionContextJDBC connectionContext);
}
