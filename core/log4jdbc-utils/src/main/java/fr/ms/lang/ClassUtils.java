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
package fr.ms.lang;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class ClassUtils {

    public final static boolean classPresent(final String className) {
	try {
	    Class.forName(className);
	    return true;
	} catch (final ClassNotFoundException e) {
	    return false;
	}
    }

    public final static Class[] getAllInterfaces(final Class cls) {
	if (cls == null) {
	    return null;
	}

	final Set interfacesFound = new HashSet();
	getAllInterfaces(cls, interfacesFound);

	final Class[] interfaces = (Class[]) interfacesFound.toArray(new Class[interfacesFound.size()]);

	return interfaces;
    }

    private final static void getAllInterfaces(Class clazz, final Set interfacesFound) {
	while (clazz != null) {
	    final Class[] interfaces = clazz.getInterfaces();

	    for (int i = 0; i < interfaces.length; i++) {
		final Class inter = interfaces[i];
		if (interfacesFound.add(inter)) {
		    getAllInterfaces(inter, interfacesFound);
		}
	    }

	    clazz = clazz.getSuperclass();
	}
    }
}
