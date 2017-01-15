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
package fr.ms.log4jdbc.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.ms.lang.StringUtils;
import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class InputStreamWrapper extends InputStream {

	private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

	private final static String nl = System.getProperty("line.separator");

	private InputStream is;

	public InputStreamWrapper(final InputStream is) {

		if (is != null) {
			BufferedReader br = null;
			final StringMaker sb = stringFactory.newString();
			String line;
			try {

				final InputStreamReader isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				while ((line = br.readLine()) != null) {
					final String stringFormat = StringUtils.replaceAll(line, "\\", "\\\\");
					sb.append(stringFormat);
					sb.append(nl);
				}

			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}

			this.is = new ByteArrayInputStream(sb.toString().getBytes());
		}
	}

	public int read() throws IOException {
		return is.read();
	}

	public int read(final byte[] b) throws IOException {
		return is.read(b);
	}

	public int read(final byte[] b, final int off, final int len) throws IOException {
		return is.read(b, off, len);
	}

	public long skip(final long n) throws IOException {
		return is.skip(n);
	}

	public int available() throws IOException {
		return is.available();
	}

	public void close() throws IOException {
		is.close();
	}

	public void mark(final int readlimit) {
		is.mark(readlimit);
	}

	public void reset() throws IOException {
		is.reset();
	}

	public boolean markSupported() {
		return is.markSupported();
	}
}
