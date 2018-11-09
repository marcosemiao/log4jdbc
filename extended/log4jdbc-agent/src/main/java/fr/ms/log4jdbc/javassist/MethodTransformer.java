package fr.ms.log4jdbc.javassist;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public abstract class MethodTransformer implements JavassistClassFileTransformer {

	private final String clazz;

	private CtClass ccSubType;
	private CtMethod[] interfaceMethods;

	private Set<String> excludesPackages;

	protected MethodTransformer(final String clazz) {
		this.clazz = clazz;
		try {
			final ClassPool cp = ClassPool.getDefault();
			ccSubType = cp.get(this.clazz);
			interfaceMethods = ccSubType.getDeclaredMethods();
		} catch (final NotFoundException e) {
			// NO-OP
		}

	}

	protected MethodTransformer(final String clazz, final String... excludesPackages) {
		this(clazz);
		if (excludesPackages != null && excludesPackages.length > 0) {
			this.excludesPackages = new HashSet<String>(Arrays.asList(excludesPackages));
		}
	}

	@Override
	public CtClass getCtClass() {
		return ccSubType;
	}

	@Override
	public byte[] transform(final ClassPool cp, final CtClass cc, final ClassLoader loader, final String dotClassName,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
			throws IllegalClassFormatException {
		if (excludesPackages != null) {
			for (final String exclude : excludesPackages) {
				final boolean startsWith = dotClassName.startsWith(exclude);
				if (startsWith) {
					return classfileBuffer;
				}
			}
		}

		try {
			return transform0(cp, cc, loader, dotClassName, classfileBuffer);
		} catch (final CannotCompileException e) {
			final StringBuilder message = new StringBuilder();
			message.append("Log4Jdbc Agent not transformed ");
			message.append(clazz);
			message.append(" : ");
			message.append(dotClassName);
			message.append(" - Classloader : ");
			if (loader == null) {
				message.append(" Bootstrap Loader");
			} else {
				message.append(loader);
			}
			message.append(" May be Log4Jdbc not into classloader !!!! ");
			System.err.println(message.toString());
		} catch (final Exception e) {
			final StringBuilder message = new StringBuilder();
			message.append("Log4Jdbc Agent Error ");
			message.append(clazz);
			message.append(" : ");
			message.append(dotClassName);
			message.append(" - Classloader : ");
			if (loader == null) {
				message.append(" Bootstrap Loader");
			} else {
				message.append(loader);
			}

			System.err.println(message.toString());
			e.printStackTrace();
		}

		return classfileBuffer;
	}

	private byte[] transform0(final ClassPool cp, final CtClass cc, final ClassLoader loader, final String dotClassName,
			final byte[] classfileBuffer) throws NotFoundException, CannotCompileException, IOException {

		boolean firstTransform = true;

		for (final CtMethod method : interfaceMethods) {

			final String methodName = method.getName();
			final CtClass[] parameterTypes = method.getParameterTypes();
			try {
				final CtMethod declaredMethod = cc.getDeclaredMethod(methodName, parameterTypes);
				if (firstTransform) {
					init(cc);
					firstTransform = false;
				}
				replaceMethod(cc, declaredMethod);

			} catch (final NotFoundException e) {
				final CtClass[] interfaces = cc.getInterfaces();
				if (interfaces != null) {
					for (final CtClass ctClass : interfaces) {
						if (ctClass.subtypeOf(ccSubType)) {
							if (firstTransform) {
								init(cc);
								firstTransform = false;
							}
							createMethod(cc, method);
						}
					}
				}
			}
		}

		final byte[] newClassfileBuffer = cc.toBytecode();

		if (Arrays.equals(classfileBuffer, newClassfileBuffer)) {
			return classfileBuffer;
		}

		cc.debugWriteFile("C:\\dev\\IBM\\Was9\\AppServer\\profiles\\server1\\logs\\clazz");
		cc.defrost();

		if (Arrays.equals(classfileBuffer, newClassfileBuffer)) {
			return classfileBuffer;
		}

		final StringBuilder message = new StringBuilder();
		message.append("Log4Jdbc Agent ");
		message.append(clazz);
		message.append(" : ");
		message.append(dotClassName);
		message.append(" - Classloader : ");
		if (loader == null) {
			message.append(" Bootstrap Loader");
		} else {
			message.append(loader);
		}

		System.out.println(message.toString());

		return newClassfileBuffer;
	}

	public abstract void init(CtClass clazz) throws CannotCompileException, NotFoundException;

	public abstract void createMethod(CtClass clazz, CtMethod method) throws CannotCompileException, NotFoundException;

	public abstract void replaceMethod(CtClass clazz, CtMethod method) throws CannotCompileException, NotFoundException;
}
