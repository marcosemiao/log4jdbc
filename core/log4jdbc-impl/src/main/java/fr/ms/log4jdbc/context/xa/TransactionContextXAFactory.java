package fr.ms.log4jdbc.context.xa;

import java.sql.Connection;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextFactory;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;

public class TransactionContextXAFactory implements TransactionContextFactory {

	public TransactionContextJDBC newTransactionContext(final Connection connection,
			final ConnectionContextJDBC connectionContext) {
		return new TransactionContextXA();
	}
}
