package fr.ms.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.ms.util.StackTraceHolder;
import fr.ms.util.logging.Logger;
import fr.ms.util.logging.LoggerManager;

public class WrapInvocationHandler implements InvocationHandler {

	private final static Logger LOG = LoggerManager.getLogger(WrapInvocationHandler.class);

	private final static String nl = System.getProperty("line.separator");

	private final Object implementation;

	public WrapInvocationHandler(final Object implementation) {
		this.implementation = implementation;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class[] parameterTypes = method.getParameterTypes();
		Class returnType = method.getReturnType();

		Object invoke = null;
		Throwable targetException = null;
		try {
			args = unProxyLog4Jdbc(args);

			args = wrapObject(parameterTypes, args);

			invoke = method.invoke(implementation, args);

			invoke = wrapObject(returnType, invoke);

			if (LOG.isDebugEnabled()) {
				String logMessage = "Method : " + method + " - args Proxy : " + args + " - args  : " + args
						+ " - invoke : " + invoke;

				if (LOG.isTraceEnabled()) {
					LOG.trace(logMessage + nl + StackTraceHolder.returnStackTrace());
				} else {
					LOG.debug(logMessage);
				}
			}
		} catch (final InvocationTargetException s) {
			targetException = s.getTargetException();
			if (targetException == null) {
				throw s;
			}
			if (LOG.isErrorEnabled()) {
				LOG.error("Method : " + method + " - args Proxy : " + args + " - targetException : " + targetException);
			}
		}

		final WrapInvocation wrapInvocation = new WrapInvocation(args, invoke, targetException);
		return wrapInvocation;
	}

	private static Object[] wrapObject(Class[] clazz, Object[] args) {
		return args;
	}

	private static Object wrapObject(Class clazz, Object arg) {
		return arg;
	}

	private static final Object[] unProxyLog4Jdbc(final Object[] args) {
		Object[] dest = args;
		if (dest != null && dest.length > 0) {
			dest = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object obj = args[i];
				if (args[i] != null && Proxy.isProxyClass(args[i].getClass())) {
					final InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[i]);
					if (invocationHandler instanceof ProxyOperationInvocationHandler) {
						final ProxyOperationInvocationHandler handler = (ProxyOperationInvocationHandler) invocationHandler;
						obj = handler.getImplementation();
					}
				}
				dest[i] = obj;
			}
		}

		return dest;
	}
}