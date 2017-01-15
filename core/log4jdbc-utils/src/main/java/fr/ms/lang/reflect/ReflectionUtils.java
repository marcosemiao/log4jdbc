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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	public static String constantName(Class c, int value) {
		Integer v = new Integer(value);
		final Field[] declaredFields = c.getDeclaredFields();

		for (int i = 0; i < declaredFields.length; i++) {
			final Field f = declaredFields[i];
			final int mod = f.getModifiers();
			if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)) {
				try {
					Integer valueFind = (Integer) f.get(null);

					if (v.equals(valueFind)) {
						return f.getName();
					}
				} catch (Exception e) {
				}
			}
		}

		return null;
	}
}
