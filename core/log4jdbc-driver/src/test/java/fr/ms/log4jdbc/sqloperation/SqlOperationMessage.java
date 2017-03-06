package fr.ms.log4jdbc.sqloperation;

import java.lang.reflect.Method;
import java.util.Arrays;

import fr.ms.log4jdbc.SqlOperation;

public class SqlOperationMessage {

	private final String typeLogger;
	private final SqlOperation sqlOperation;
	private final Method method;
	private final Object[] args;

	private Object invoke;

	private Throwable exception;

	public SqlOperationMessage(final String typeLogger, final SqlOperation sqlOperation, final Method method,
			final Object[] args) {
		this.typeLogger = typeLogger;
		this.sqlOperation = sqlOperation;
		this.method = method;
		this.args = args;
	}

	public SqlOperationMessage(final String typeLogger, final SqlOperation sqlOperation, final Method method,
			final Object[] args, final Object invoke) {
		this(typeLogger, sqlOperation, method, args);
		this.invoke = invoke;
	}

	public SqlOperationMessage(final String typeLogger, final SqlOperation sqlOperation, final Method method,
			final Object[] args, final Throwable exception) {
		this(typeLogger, sqlOperation, method, args);
		this.exception = exception;
	}

	public String getTypeLogger() {
		return typeLogger;
	}

	public SqlOperation getSqlOperation() {
		return sqlOperation;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object getInvoke() {
		return invoke;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "SqlOperationMessage [typeLogger=" + typeLogger + ", sqlOperation=" + sqlOperation + ", method=" + method
				+ ", args=" + Arrays.toString(args) + ", invoke=" + invoke + ", exception=" + exception + "]";
	}
}
