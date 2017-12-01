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
package fr.ms.lang.delegate;

import java.util.Iterator;

import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.util.Service;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultStringMakerFactory implements StringMakerFactory {

	private final static StringMakerFactory instance = new DefaultStringMakerFactory();

	private static StringMakerFactory delegate;

	static {
		final Iterator providers = Service.providers(StringMakerFactory.class,
				DefaultStringMakerFactory.class.getClassLoader());

		while (providers.hasNext()) {
			try {
				final StringMakerFactory p = (StringMakerFactory) providers.next();

				if (p != null) {
					if (delegate == null || delegate.getPriority() < p.getPriority()) {
						delegate = p;
					}
				}
			} catch (final Throwable t) {
			}
		}
	}

	private DefaultStringMakerFactory() {
	}

	public static StringMakerFactory getInstance() {
		return instance;
	}

	public StringMaker newString() {
		return delegate.newString();
	}

	public StringMaker newString(final int capacity) {
		return delegate.newString(capacity);
	}

	public StringMaker newString(final String str) {
		return delegate.newString(str);
	}

	public int getPriority() {
		return delegate.getPriority();
	}
}
