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
package fr.ms.util.logging.impl;

import java.io.IOException;
import java.io.Writer;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.util.logging.Logger;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class WriterLogger extends Writer {

	private final static StringMakerFactory smf = DefaultStringMakerFactory.getInstance();

	private final Logger logger;

	private StringMaker line = smf.newString();

	public WriterLogger(final Logger logger) {
		this.logger = logger;
	}

	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		if (logger.isInfoEnabled()) {
			line.append(cbuf, off, len);
		}
	}

	public void flush() throws IOException {
		if (logger.isInfoEnabled()) {

			String l = line.toString();
			l = l.trim();
			logger.info(l);
			line = smf.newString();
		}
	}

	public void close() throws IOException {
		// NO-OP
	}
}
