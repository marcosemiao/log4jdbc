package fr.ms.log4jdbc.transformer;

import fr.ms.log4jdbc.javassist.MethodTransformer;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ConnectionPoolDataSourceTransformer extends MethodTransformer {

	public ConnectionPoolDataSourceTransformer() {
		super("javax.sql.ConnectionPoolDataSource", "fr.ms.log4jdbc");
	}

	@Override
	public void init(final CtClass clazz) throws CannotCompileException, NotFoundException {
		final CtField f = CtField.make(
				"private final fr.ms.log4jdbc.context.Log4JdbcContext log4JdbcContextConnectionPoolDataSource = new fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC();",
				clazz);

		clazz.addField(f);
	}

	@Override
	public void createMethod(final CtClass clazz, final CtMethod method)
			throws CannotCompileException, NotFoundException {
		final String methodName = method.getName();
		final String signature = method.getSignature();

		try {
			clazz.getMethod(methodName, signature);

			final CtMethod newMethod = new CtMethod(method, clazz, null);

			newMethod.setBody("{ return super." + methodName + "($$); }");

			clazz.addMethod(newMethod);

			replaceMethod(clazz, newMethod);
		} catch (final NotFoundException nfe) {
			// NO-OP
		}
	}

	@Override
	public void replaceMethod(final CtClass clazz, final CtMethod method)
			throws CannotCompileException, NotFoundException {
		if (!method.isEmpty()) {
			final StringBuilder code = new StringBuilder();

			code.append(
					"final javax.sql.PooledConnection wrap = (javax.sql.PooledConnection) fr.ms.log4jdbc.datasource.ConnectionDecorator.proxyConnection(log4JdbcContextConnectionPoolDataSource, $_, $0);");
			code.append("return wrap;");

			method.insertAfter(code.toString());
		}
	}
}
