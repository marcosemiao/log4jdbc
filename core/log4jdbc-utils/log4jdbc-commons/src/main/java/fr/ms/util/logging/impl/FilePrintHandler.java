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

import java.io.File;

import fr.ms.java.io.FilePrint;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class FilePrintHandler implements PrintHandler {

	private final FilePrint filePrint;

	public FilePrintHandler(final File file) {
		filePrint = new FilePrint(file);
	}

	public void trace(final String message) {
		write(message);
	}

	public void debug(final String message) {
		write(message);
	}

	public void info(final String message) {
		write(message);
	}

	public void warn(final String message) {
		write(message);
	}

	public void error(final String message) {
		write(message);
	}

	public void fatal(final String message) {
		write(message);
	}

	private void write(final String message) {
		filePrint.println(message);
	}
}