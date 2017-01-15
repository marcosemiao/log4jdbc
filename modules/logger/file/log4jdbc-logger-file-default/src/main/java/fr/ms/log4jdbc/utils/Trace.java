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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class Trace {

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	public static synchronized void print(final String message) {

		final File file = props.file();
		if (file == null) {
			System.out.println(message);
		} else {
			FileOutputStream fos = null;
			OutputStreamWriter writer = null;
			try {
				fos = new FileOutputStream(file.getAbsolutePath(), true);
				writer = new OutputStreamWriter(fos, "UTF-8");
				writer.write(message, 0, message.length());
				writer.write(System.getProperty("line.separator"));
			} catch (final IOException e) {
				throw new RuntimeException(e.getMessage());
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (final IOException e) {
						throw new RuntimeException(e.getMessage());
					}
				}
			}
		}
	}
}
