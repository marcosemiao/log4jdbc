/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.log4jdbc.datasource;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ms.log4jdbc.proxy.Handlers;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public abstract class AbstractDataSource {

    protected Object impl;

    protected void setImpl(final Object impl) {
	this.impl = impl;
    }

    public abstract String getDataSourceClassName();

    public Object newInstanceDataSource() {
	try {
	    final String className = getDataSourceClassName();
	    final Class clazz = Class.forName(className);
	    final Object newInstance = clazz.newInstance();
	    return newInstance;
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }

    protected Object invokeMethod(final String methodName) {
	return invokeMethod(methodName, null);
    }

    protected Object invokeMethod(final String methodName, final Object param) {

	Object[] params = null;
	if (param != null) {
	    params = new Object[] { param };
	}

	return invokeMethod(this.impl, this.impl.getClass(), methodName, params);
    }

    protected Object invokeMethod(final String methodName, final Object params, final Class classParams) {
	Object[] paramsObject = null;
	if (params != null) {
	    paramsObject = new Object[] { params };
	}
	Class[] classParamsObject = null;
	if (classParams != null) {
	    classParamsObject = new Class[] { classParams };
	}
	return invokeMethod(this.impl, this.impl.getClass(), methodName, paramsObject, classParamsObject);
    }

    private static Object invokeMethod(final Object impl, final Class clazz, final String methodName, final Object[] params) {
	Class[] classParams = null;
	if (params != null && params.length > 0) {
	    classParams = new Class[params.length];
	    for (int i = 0; i < params.length; i++) {
		classParams[i] = params[i].getClass();
	    }
	}

	return invokeMethod(impl, clazz, methodName, params, classParams);
    }

    private static Object invokeMethod(final Object impl, final Class clazz, final String methodName, final Object[] params, final Class[] classParams) {
	try {
	    final Method method = clazz.getDeclaredMethod(methodName, classParams);
	    final Object invoke = method.invoke(impl, params);
	    return invoke;
	} catch (final NoSuchMethodException sme) {
	    final Class superClass = clazz.getSuperclass();
	    if (superClass != Object.class) {
		return invokeMethod(impl, superClass, methodName, params, classParams);
	    }
	    throw new RuntimeException(sme);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static Object wrapObject(final Object impl, final Object obj) {
	return wrapObject(impl, obj, null);
    }

    public static Object wrapObject(final Object impl, final Object obj, final Class[] interfaces) {
	final ClassLoader classLoader = impl.getClass().getClassLoader();

	final List inter = new ArrayList();

	inter.addAll(Arrays.asList(impl.getClass().getInterfaces()));
	if (interfaces != null) {
	    inter.addAll(Arrays.asList(interfaces));
	}

	final WrapObject ih = new WrapObject(impl, obj);

	final Object wrap = Proxy.newProxyInstance(classLoader, (Class[]) inter.toArray(new Class[inter.size()]), ih);

	return wrap;
    }

    private static class WrapObject implements InvocationHandler {

	private final Object impl;

	private final Object obj;

	private WrapObject(final Object impl, final Object obj) {
	    this.impl = impl;
	    this.obj = obj;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	    final Object invoke = method.invoke(impl, args);

	    if (invoke != null) {
		if (invoke instanceof Connection) {

		    final Connection c = (Connection) invoke;
		    final Class clazz = obj.getClass();
		    final Connection wrapObject = Handlers.wrapConnection(c, clazz);
		    return wrapObject;
		} else if (!invoke.getClass().isPrimitive()) {
		    final Class returnType = method.getReturnType();

		    final boolean isArray = returnType.isArray();
		    if (isArray) {
			final Class[] interfaces = new Class[] { returnType.getComponentType() };
			final Object[] invokeObject = (Object[]) invoke;

			final List liste = new ArrayList();
			for (int i = 0; i < invokeObject.length; i++) {
			    final Object invokeOnly = invokeObject[i];
			    final Object wrapObject = wrapObject(invokeOnly, obj, interfaces);
			    liste.add(wrapObject);
			}

			return liste.toArray((Object[]) Array.newInstance(returnType.getComponentType(), liste.size()));
		    } else {
			final Class[] interfaces = new Class[] { returnType };
			final Object wrapObject = wrapObject(invoke, obj, interfaces);
			return wrapObject;
		    }

		}
	    }
	    return invoke;
	}
    }
}
