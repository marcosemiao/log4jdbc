package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;

public class TransactionContextJDBCFactory implements TransactionContextFactory {

	public TransactionContextJDBC newTransactionContext(final Connection connection,
			final ConnectionContextJDBC connectionContext) {
		return new TransactionContextJDBC();
	}
}
