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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import fr.ms.log4jdbc.thread.LoopRunnable;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Log4JdbcProperties implements Runnable {

	public final static String REQUETE_SQL_STYLE_ORIGINAL = "none";

	public final static String REQUETE_SQL_STYLE_ONELINE = "oneline";

	public final static String REQUETE_SQL_STYLE_FORMAT = "format";

	private final static long LOOP_THREAD = 1000;

	private final static String propertyFile = System.getProperty("log4jdbc.file", "/log4jdbc.properties");

	private final static Log4JdbcProperties instance = new Log4JdbcProperties();

	private Map mapProperties;

	private long lastModified;

	private Log4JdbcProperties() {
		run();

		final Runnable r = new LoopRunnable(this, LOOP_THREAD);

		final Thread t = new Thread(r, "Log4Jdbc-Reload");

		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	public static Log4JdbcProperties getInstance() {
		return instance;
	}

	public boolean logEnabled() {
		return getProperty("log4jdbc.enabled", true);
	}

	public boolean logProcessThread() {
		return getProperty("log4jdbc.process.thread", true);
	}

	public int logProcessThreadSize() {
		return getProperty("log4jdbc.process.thread.size", 500);
	}

	public boolean logGenericMessage() {
		return getProperty("log4jdbc.generic.message", false);
	}

	public boolean logTransaction() {
		return getProperty("log4jdbc.transaction", false);
	}

	public boolean logBatch() {
		return getProperty("log4jdbc.batch", false);
	}

	public boolean logRequeteAllSQL() {
		return getProperty("log4jdbc.request.sql.all", true);
	}

	public boolean logRequeteExecuteSQL() {
		return getProperty("log4jdbc.request.sql.execute", true) || logRequeteAllSQL();
	}

	public boolean logRequeteBatchSQL() {
		return getProperty("log4jdbc.request.sql.batch", true) || logRequeteAllSQL();
	}

	public boolean logRequeteCommentSQL() {
		return getProperty("log4jdbc.request.sql.comment", false);
	}

	public boolean logRequeteSemiColonAddSQL() {
		return getProperty("log4jdbc.request.sql.semicolon.add", false);
	}

	public String logRequeteStyleSQL() {
		return getProperty("log4jdbc.request.sql.style", REQUETE_SQL_STYLE_ORIGINAL);
	}

	public boolean logRequeteSelectSQL() {
		return getProperty("log4jdbc.request.sql.select", false) || logRequeteAllSQL();
	}

	public boolean logRequeteSelectResultSetSQL() {
		return getProperty("log4jdbc.request.sql.select.resultset", false);
	}

	public boolean logRequeteInsertSQL() {
		return getProperty("log4jdbc.request.sql.insert", false) || logRequeteAllSQL();
	}

	public boolean logRequeteUpdateSQL() {
		return getProperty("log4jdbc.request.sql.update", false) || logRequeteAllSQL();
	}

	public boolean logRequeteDeleteSQL() {
		return getProperty("log4jdbc.request.sql.delete", false) || logRequeteAllSQL();
	}

	public boolean logRequeteCreateSQL() {
		return getProperty("log4jdbc.request.sql.create", false) || logRequeteAllSQL();
	}

	public boolean logRequeteException() {
		return getProperty("log4jdbc.request.exception", true);
	}

	public boolean logStackTrace() {
		return getProperty("log4jdbc.stacktrace", false);
	}

	public String getStacktraceStartPackages() {
		if (mapProperties == null) {
			return null;
		}
		final Object object = mapProperties.get("log4jdbc.stacktrace.start.packages");
		if (object == null) {
			return null;
		}
		return (String) object;
	}

	public File file() {
		if (mapProperties == null) {
			return null;
		}
		final Object object = mapProperties.get("log4jdbc.file");
		if (object == null) {
			return null;
		}

		final String property = (String) object;

		final File file = new File(property);

		boolean fileExists = file.exists();

		if (!fileExists) {
			try {
				fileExists = file.createNewFile();
			} catch (final IOException e) {
			}
		}

		if (!fileExists) {
			return null;
		}

		return file;
	}

	private boolean getProperty(final String key, final boolean defaultValue) {
		if (mapProperties == null) {
			return defaultValue;
		}

		final Object object = mapProperties.get(key);
		if (object == null) {
			return defaultValue;
		}
		final String property = (String) object;

		return Boolean.valueOf(property).booleanValue();
	}

	private String getProperty(final String key, final String defaultValue) {
		if (mapProperties == null) {
			return defaultValue;
		}

		final Object object = mapProperties.get(key);
		if (object == null) {
			return defaultValue;
		}
		final String property = (String) object;

		return property;
	}

	private int getProperty(final String key, final int defaultValue) {
		if (mapProperties == null) {
			return defaultValue;
		}

		final Object object = mapProperties.get(key);
		if (object == null) {
			return defaultValue;
		}
		final String property = (String) object;

		return Integer.valueOf(property).intValue();
	}

	private InputStream getInputStream(final String path) {
		InputStream propStream = null;

		final File f = new File(path);
		if (f.isFile()) {
			final long lastModified = f.lastModified();
			if (lastModified > this.lastModified) {
				this.lastModified = lastModified;
				System.out.println("Reloading configuration file : " + f);
			} else {
				return null;
			}

			try {
				propStream = new FileInputStream(f);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			propStream = Log4JdbcProperties.class.getResourceAsStream(path);
		}

		if (propStream != null) {
			propStream = new InputStreamWrapper(propStream);
		}

		return propStream;
	}

	private Properties getLoadProperties() {
		Properties props = null;

		final InputStream propStream = getInputStream(propertyFile);

		if (propStream != null) {
			try {
				props = new Properties();
				props.load(propStream);
			} catch (final IOException e) {
				System.err.println(e);
			} finally {
				try {
					propStream.close();
				} catch (final IOException e) {
					System.err.println(e);
				}
			}
		}

		return props;
	}

	private Map getMapProperties() {
		final Properties props = getLoadProperties();
		if (props == null) {
			return null;
		}

		final Map m = new HashMap();

		final Enumeration propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			final String key = (String) propertyNames.nextElement();
			final String value = props.getProperty(key);

			m.put(key, value.trim());
		}

		return Collections.unmodifiableMap(m);
	}

	public void run() {
		final Map loadProperties = getMapProperties();

		if (loadProperties != null) {
			this.mapProperties = loadProperties;
		}
	}
}
