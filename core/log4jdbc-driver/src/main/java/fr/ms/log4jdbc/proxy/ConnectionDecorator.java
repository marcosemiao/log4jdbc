package fr.ms.log4jdbc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import fr.ms.lang.reflect.ImplementationDecorator;
import fr.ms.lang.reflect.ImplementationDecorator.ImplementationProxy;

public class ConnectionDecorator implements ImplementationProxy{

	public static Object proxyConnection(final Object impl)
	{
		final ImplementationProxy ip =new ConnectionDecorator();
		final InvocationHandler ih = new ImplementationDecorator(impl,ip);

		final Class clazz = impl.getClass();
		final ClassLoader classLoader = clazz.getClassLoader();
		final Class[] interfaces = clazz.getInterfaces();
		final Object wrap = Proxy.newProxyInstance(classLoader, interfaces, ih);

		return wrap;
	}

	public Object createProxy(final ImplementationDecorator origine, final Object invoke) {
		if (invoke instanceof Connection) {

			final Connection c = (Connection) invoke;

			final Connection wrapObject = Handlers.wrapConnection(c, origine.getSourceImpl().getClass());
			return wrapObject;
		}
		return null;
	}
}
