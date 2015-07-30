package fr.ms.lang.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyObject implements InvocationHandler {

	private final Object impl;

	private final Object sourceImpl;

	public ProxyObject(final Object impl) {
		this.impl = impl;
		this.sourceImpl = impl;
	}

	public ProxyObject(final Object impl, final Object sourceImpl) {
		this.impl = impl;
		this.sourceImpl = sourceImpl;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final Object invoke = method.invoke(impl, args);

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
		final InvocationHandler ih = new ProxyObject(impl, sourceImpl);

		final Object proxy = Proxy.newProxyInstance(classLoader, interfaces, ih);

		return proxy;
	}

	public static interface RealImplementation{


	}

}
