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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
abstract class AbstractDataSource {

	protected abstract Object getImpl();

	private List invokeLater;

	public boolean initialized = false;

	public void initialized() {
		initialized = true;

		for (int i = 0; i < invokeLater.size(); i++) {
			final Runnable r = (Runnable) invokeLater.get(i);
			r.run();
		}
		invokeLater = null;
	}

	protected Object newInstanceDataSource(final String className) {
		try {
			final Class clazz = Class.forName(className);
			final Object newInstance = clazz.newInstance();
			return newInstance;
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
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

		return invokeWrapperMethod(methodName, params, null);
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
		return invokeWrapperMethod(methodName, paramsObject, classParamsObject);
	}

	private Object invokeWrapperMethod(final String methodName, final Object[] params, final Class[] classParams) {
		if (initialized) {
			return invokeMethod(getImpl(), getImpl().getClass(), methodName, params, classParams);
		}

		if (invokeLater == null) {
			invokeLater = new ArrayList();

			final Runnable invoker = new Runnable() {
				public void run() {
					invokeMethod(getImpl(), getImpl().getClass(), methodName, params, classParams);

				}
			};
			invokeLater.add(invoker);
		}
		return null;
	}

	private static Object invokeMethod(final Object impl, final Class clazz, final String methodName,
			final Object[] params, Class[] classParams) {
		if (classParams == null || classParams.length == 0) {
			if (params != null && params.length > 0) {
				classParams = new Class[params.length];
				for (int i = 0; i < params.length; i++) {
					classParams[i] = params[i].getClass();
				}
			}
		}

		try {
			final Method method = clazz.getDeclaredMethod(methodName, classParams);
			final Object invoke = method.invoke(impl, params);
			return invoke;
		} catch (final NoSuchMethodException sme) {
			final Class superClass = clazz.getSuperclass();
			if (superClass != Object.class) {
				return invokeMethod(impl, superClass, methodName, params, classParams);
			}
			throw new RuntimeException(sme.getMessage());
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
