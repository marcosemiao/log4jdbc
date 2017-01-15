package fr.ms.log4jdbc.sqloperation;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;

public class StatementSqlOperationLogger implements SqlOperationLogger {

    private final static String TYPE_LOGGER = SqlOperationLogger.STATEMENT;

    public boolean isEnabled() {
	return true;
    }

    public boolean isLogger(final String typeLogger) {
	return TYPE_LOGGER.equals(typeLogger);
    }

    public void buildLog(final SqlOperation sqlOperation, final Method method, final Object[] args, final Object invoke) {
	final SqlOperationMessage message = new SqlOperationMessage(TYPE_LOGGER, sqlOperation, method, args, invoke);
	final SqlMessage messages = SqlMessage.getInstance();
	messages.addMessage(message);
    }

    public void buildLog(final SqlOperation sqlOperation, final Method method, final Object[] args, final Throwable exception) {
	final SqlOperationMessage message = new SqlOperationMessage(TYPE_LOGGER, sqlOperation, method, args, exception);
	final SqlMessage messages = SqlMessage.getInstance();
	messages.addMessage(message);
    }
}
