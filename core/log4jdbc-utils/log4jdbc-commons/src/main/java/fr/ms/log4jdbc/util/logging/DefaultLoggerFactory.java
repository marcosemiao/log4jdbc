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
package fr.ms.log4jdbc.util.logging;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import fr.ms.log4jdbc.util.logging.impl.DefaultLogger;
import fr.ms.log4jdbc.util.logging.impl.FilePrintHandler;
import fr.ms.log4jdbc.util.logging.impl.PrintHandler;
import fr.ms.log4jdbc.util.logging.impl.SystemOutPrintHandler;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultLoggerFactory implements LoggerFactory {

	private final static String PROPERTY_FILE = "log4jdbc.log.file";

	private final static String PREFIX = "log4jdbc.log.level.";

	private final static Map LOG_LEVEL = new HashMap();

	private PrintHandler printHandler = new SystemOutPrintHandler();

	public DefaultLoggerFactory() {
		initPrintHandler();
		initLevel();
	}

	private void initPrintHandler() {
		final String property = System.getProperty(PROPERTY_FILE);
		if (property != null) {
			final File file = new File(property);
			final boolean fileExists = file.exists();

			if (!fileExists) {
				try {
					file.createNewFile();
				} catch (final IOException e) {
				}
			}

			printHandler = new FilePrintHandler(file);
		}
	}

	private void initLevel() {
		final Properties properties = System.getProperties();

		final Enumeration en = properties.propertyNames();
		while (en.hasMoreElements()) {
			String propName = (String) en.nextElement();

			if (propName.startsWith(PREFIX)) {
				final String propValue = properties.getProperty(propName);
				propName = propName.substring(PREFIX.length());

				LOG_LEVEL.put(propName, propValue);
			}
		}
	}

	private String getLevel(final String name) {
		String nameLogger = null;
		String levelLogger = null;

		final Iterator entries = LOG_LEVEL.entrySet().iterator();
		while (entries.hasNext()) {
			final Entry element = (Entry) entries.next();
			final String key = (String) element.getKey();
			if (name.startsWith(key) && (nameLogger == null || key.length() > nameLogger.length())) {
				nameLogger = key;

				levelLogger = (String) element.getValue();
			}
		}

		if (levelLogger != null) {
			return levelLogger.toLowerCase();
		}

		return levelLogger;

	}

	public Logger getLogger(final String name) {
		final String level = getLevel(name);
		final DefaultLogger logger = new DefaultLogger(printHandler, level, name);
		return logger;
	}
}
