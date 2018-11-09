package fr.ms.log4jdbc.javassist;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import javassist.ByteArrayClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

public class JavassistMethodTransformer implements ClassFileTransformer {

	private final Set<JavassistClassFileTransformer> liste = new HashSet<JavassistClassFileTransformer>();

	public void addTransformer(final JavassistClassFileTransformer transformer) {
		liste.add(transformer);
	}

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, byte[] classfileBuffer) {

		final String dotClassName = className.replace('/', '.');

		final ClassPool cp = new ClassPool();

		final ClassPath classPath = new ByteArrayClassPath(dotClassName, classfileBuffer);
		final ClassPath loaderPath = new LoaderClassPath(loader);

		try {
			cp.insertClassPath(loaderPath);
			cp.insertClassPath(classPath);

			final CtClass cc = cp.get(dotClassName);
			if (cc.isInterface()) {
				return classfileBuffer;
			}

			for (final JavassistClassFileTransformer b : liste) {
				final CtClass ccSubType = b.getCtClass();
				if (ccSubType != null) {
					final boolean isClassImplemented = cc.subtypeOf(ccSubType);
					if (isClassImplemented) {
						classfileBuffer = b.transform(cp, cc, loader, dotClassName, classBeingRedefined,
								protectionDomain, classfileBuffer);

					}
				}
			}
			return classfileBuffer;
		} catch (final Throwable e) {
			return classfileBuffer;
		} finally {
			cp.removeClassPath(classPath);
			cp.removeClassPath(loaderPath);
		}
	}
}