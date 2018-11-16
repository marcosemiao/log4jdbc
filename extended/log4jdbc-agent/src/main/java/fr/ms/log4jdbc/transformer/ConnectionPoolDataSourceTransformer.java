package fr.ms.log4jdbc.transformer;

import fr.ms.log4jdbc.javassist.MethodTransformer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ConnectionPoolDataSourceTransformer extends MethodTransformer {

	public ConnectionPoolDataSourceTransformer() {
		super("javax.sql.ConnectionPoolDataSource", "fr.ms.log4jdbc");
	}

	@Override
	public void init(final ClassPool cp, final CtClass clazz) throws CannotCompileException, NotFoundException {
		cp.importPackage("java.lang.reflect.Proxy");
		cp.importPackage("java.lang.reflect.InvocationHandler");
		cp.importPackage("fr.ms.log4jdbc.context.Log4JdbcContext");
		cp.importPackage("fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC");
		cp.importPackage("fr.ms.log4jdbc.datasource.ConnectionDecorator");
		cp.importPackage("java.sql.PooledConnection");

		final CtField f = CtField.make(
				"private final Log4JdbcContext log4JdbcContextConnectionPoolDataSource = new Log4JdbcContextJDBC();",
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

			code.append("final PooledConnection original = $_;");
			code.append("final boolean isProxyClass = Proxy.isProxyClass(original.getClass());");
			code.append("if (isProxyClass) {");
			code.append("final InvocationHandler invocationHandler = Proxy.getInvocationHandler(original);");
			code.append("final String name = invocationHandler.getClass().getName();");
			code.append("if (name.startsWith(\"fr.ms.log4jdbc\")) {");
			code.append("return original;");
			code.append("}");
			code.append("}");

			code.append(
					"final PooledConnection wrap = (PooledConnection) ConnectionDecorator.proxyConnection(log4JdbcContextConnectionPoolDataSource, original, $0);");
			code.append("return wrap;");

			method.insertAfter(code.toString());
		}
	}
}
