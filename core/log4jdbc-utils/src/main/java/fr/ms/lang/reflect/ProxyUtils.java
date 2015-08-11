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
package fr.ms.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import fr.ms.lang.ClassUtils;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class ProxyUtils {

    public static final Object newProxyInstance(final Object implementation, final InvocationHandler h) {
	final ClassLoader classLoader = implementation.getClass().getClassLoader();
	final Class[] interfaces = ClassUtils.getAllInterfaces(implementation.getClass());

	final Object instance = Proxy.newProxyInstance(classLoader, interfaces, h);

	return instance;
    }
}
