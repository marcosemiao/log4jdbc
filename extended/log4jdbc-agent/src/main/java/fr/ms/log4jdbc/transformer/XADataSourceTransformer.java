package fr.ms.log4jdbc.transformer;

import fr.ms.log4jdbc.javassist.MethodTransformer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class XADataSourceTransformer extends MethodTransformer {

	public XADataSourceTransformer() {
		super("javax.sql.XADataSource", "fr.ms.log4jdbc");
	}

	@Override
	public void init(final ClassPool cp, final CtClass clazz) throws CannotCompileException, NotFoundException {
		cp.importPackage("java.lang.reflect.Proxy");
		cp.importPackage("java.lang.reflect.InvocationHandler");
		cp.importPackage("fr.ms.log4jdbc.context.xa.Log4JdbcContextXA");
		cp.importPackage("fr.ms.log4jdbc.datasource.XAConnectionDecorator");
		cp.importPackage("javax.sql.XAConnection");

		final CtField f = CtField
				.make("private final Log4JdbcContextXA log4JdbcContextXADataSource = new Log4JdbcContextXA();", clazz);

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

			code.append("final XAConnection original = $_;");
			code.append("final boolean isProxyClass = Proxy.isProxyClass(original.getClass());");
			code.append("if (isProxyClass) {");
			code.append("final InvocationHandler invocationHandler = Proxy.getInvocationHandler(original);");
			code.append("final String name = invocationHandler.getClass().getName();");
			code.append("if (name.startsWith(\"fr.ms.log4jdbc\")) {");
			code.append("return original;");
			code.append("}");
			code.append("}");

			code.append(
					"final XAConnection wrap = (XAConnection) XAConnectionDecorator.proxyConnection2(log4JdbcContextXADataSource, original, $0);");
			code.append("return wrap;");

			method.insertAfter(code.toString());
		}
	}
}
