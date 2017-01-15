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

import fr.ms.lang.ClassUtils;
import fr.ms.lang.stringmaker.impl.StringBufferImpl;
import fr.ms.lang.stringmaker.impl.StringBuilderImpl;
import fr.ms.lang.stringmaker.impl.StringMaker;

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
		if (ClassUtils.classPresent("java.lang.StringBuilder")) {
			delegate = StringBuilderImpl.getStringMakerFactory();
		} else {
			delegate = StringBufferImpl.getStringMakerFactory();
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
}
