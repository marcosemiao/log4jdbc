package fr.ms.log4jdbc.javassist;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;

public interface JavassistClassFileTransformer {

	CtClass getCtClass();

	byte[] transform(ClassPool cp, CtClass cc, ClassLoader loader, String dotClassName, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;
}
