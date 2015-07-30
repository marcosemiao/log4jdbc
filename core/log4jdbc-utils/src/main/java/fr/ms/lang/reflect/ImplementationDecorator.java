package fr.ms.lang.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ImplementationDecorator implements InvocationHandler {

	private final Object impl;

	private final Object sourceImpl;

	private final ImplementationProxy ip;

	public ImplementationDecorator(final Object impl, final ImplementationProxy ip) {
		this(impl, impl, ip);
	}

	private ImplementationDecorator(final Object impl, final Object sourceImpl, final ImplementationProxy ip) {
		if (ip == null) {
			throw new NullPointerException();
		}
		this.impl = impl;
		this.sourceImpl = sourceImpl;
		this.ip = ip;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final Object invoke = method.invoke(impl, args);

		final Object createProxy = ip.createProxy(this, invoke);
		if (createProxy != null) {
			return createProxy;
		}

		if (invoke != null) {
			final Class clazz = invoke.getClass();
			final Class returnType = method.getReturnType();
			final boolean isPrimitive = clazz.isPrimitive() && returnType.isPrimitive();

			if (!isPrimitive) {
				final boolean isArray = returnType.isArray();
				if (isArray) {
					final Object[] invokes = (Object[]) invoke;

					final List liste = new ArrayList();
					for (int i = 0; i < invokes.length; i++) {
						final Object invokeOnly = invokes[i];
						final Object wrapObject = createProxy(invokeOnly);
						liste.add(wrapObject);
					}

					return liste.toArray((Object[]) Array.newInstance(returnType.getComponentType(), liste.size()));
				} else {
					final Object wrapObject = createProxy(invoke);
					return wrapObject;
				}
			}
		}

		return invoke;
	}

	private Object createProxy(final Object impl) {
		final Class clazz = impl.getClass();
		final ClassLoader classLoader = clazz.getClassLoader();
		final Class[] interfaces = clazz.getInterfaces();
		final InvocationHandler ih = new ImplementationDecorator(impl, sourceImpl, ip);

		final Object proxy = Proxy.newProxyInstance(classLoader, interfaces, ih);

		return proxy;
	}

	public Object getSourceImpl() {
		return sourceImpl;
	}

	public static interface ImplementationProxy {

		public Object createProxy(final ImplementationDecorator origine, final Object invoke);
	}
}
