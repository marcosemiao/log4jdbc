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
package fr.ms.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class CollectionsUtil {

	private CollectionsUtil() {
	}

	public final static List convert(final Iterator iterator) {
		final List list = new ArrayList();
		while (iterator.hasNext()) {
			try {
				final Object e = iterator.next();
				list.add(e);
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}

		return list;
	}

	public final static List synchronizedList(final List list) {
		return Collections.synchronizedList(list);
	}

	public final static Map synchronizedMap(final Map map) {
		return Collections.synchronizedMap(map);
	}
}
