package fr.ms.log4jdbc.transformer;

import fr.ms.log4jdbc.javassist.MethodTransformer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DriverTransformer extends MethodTransformer {

	public DriverTransformer() {
		super("java.sql.Driver", "fr.ms.log4jdbc");
	}

	@Override
	public void init(final ClassPool cp, final CtClass clazz) throws CannotCompileException, NotFoundException {
		cp.importPackage("java.lang.reflect.Proxy");
		cp.importPackage("java.lang.reflect.InvocationHandler");
		cp.importPackage("fr.ms.log4jdbc.context.Log4JdbcContext");
		cp.importPackage("fr.ms.log4jdbc.context.jdbc.Log4JdbcContextJDBC");
		cp.importPackage("fr.ms.log4jdbc.proxy.Log4JdbcProxy");
		cp.importPackage("java.sql.Connection");

		final CtField f = CtField.make("private final Log4JdbcContext log4JdbcContext = new Log4JdbcContextJDBC();",
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

			code.append("final Connection original = $_;");
			code.append("final boolean isProxyClass = Proxy.isProxyClass(original.getClass());");
			code.append("if (isProxyClass) {");
			code.append("final InvocationHandler invocationHandler = Proxy.getInvocationHandler(original);");
			code.append("final String name = invocationHandler.getClass().getName();");
			code.append("if (name.startsWith(\"fr.ms.log4jdbc\")) {");
			code.append("return original;");
			code.append("}");
			code.append("}");

			code.append(
					"final Connection wrap = (Connection) Log4JdbcProxy.proxyConnection(original, log4JdbcContext, $0, $1);");
			code.append("return wrap;");

			method.insertAfter(code.toString());
		}
	}
}
