package fr.ms.log4jdbc.transformer;

import fr.ms.log4jdbc.javassist.MethodTransformer;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DriverTransformer extends MethodTransformer {

	public DriverTransformer() {
		super("java.sql.Driver", "fr.ms.log4jdbc");
	}

	@Override
	public void init(final CtClass clazz) throws CannotCompileException, NotFoundException {
		final CtField f = CtField.make(
				"private final fr.ms.log4jdbc.context.Log4JdbcContext log4JdbcContext = new fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC();",
				clazz);

		clazz.addField(f);
	}

	@Override
	public void createMethod(final CtClass clazz, final CtMethod method)
			throws CannotCompileException, NotFoundException {

		final CtClass returnType = method.getReturnType();
		final String nameReturnType = returnType.getName();
		if (!nameReturnType.equals("java.sql.Connection")) {
			return;
		}

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
		final CtClass returnType = method.getReturnType();
		final String nameReturnType = returnType.getName();
		if (nameReturnType.equals("java.sql.Connection") && !method.isEmpty()) {
			final StringBuilder code = new StringBuilder();

			code.append(
					"final java.sql.Connection wrap = (java.sql.Connection)  fr.ms.log4jdbc.proxy.Log4JdbcProxy.proxyConnection($_, log4JdbcContext, $0, $1);");
			code.append("return wrap;");

			method.insertAfter(code.toString());
		}
	}
}
