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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import fr.ms.log4jdbc.thread.LoopRunnable;
import fr.ms.util.Service;

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

	protected final static String propertyFile = System.getProperty("log4jdbc.file", "/log4jdbc.properties");

	private final static Log4JdbcProperties instance = serviceLog4JdbcProperties();

	private Map mapProperties;

	private long lastModified;

	public Log4JdbcProperties() {
		run();

		final Runnable r = new LoopRunnable(this, LOOP_THREAD);

		final Thread t = new Thread(r, "Log4Jdbc-Reload");

		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	private final static Log4JdbcProperties serviceLog4JdbcProperties() {
		final Iterator providers = Service.providers(Log4JdbcProperties.class);

		if (providers.hasNext()) {
			return (Log4JdbcProperties) providers.next();
		} else {
			return new Log4JdbcProperties();
		}
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

	protected Reader getInputStream(final String path) {

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
				return new InputStreamReader(new InputStreamWrapper(new FileInputStream(f)));
			} catch (final FileNotFoundException e) {
				System.err.println("Tried to open log4jdbc properties file at " + f.getPath()
						+ " but couldn't find it, failing over to see if it can be opened on the classpath.");
			}
		}
		final InputStream stream = Log4JdbcProperties.class.getResourceAsStream(path);
		if (stream == null) {
			throw new RuntimeException("Tried to open " + path
					+ " from the classpath but couldn't find it there. Please check your configuration.");

		}
		return new InputStreamReader(new InputStreamWrapper(stream));
	}

	protected Properties getLoadProperties() {
		final Reader reader = getInputStream(propertyFile);
		try {
			if (reader == null) {
				return null;
			}
			final Properties props = new Properties();
			props.load(reader);

			return props;
		} catch (final IOException e) {
			System.err.println("An error occurred trying to initialize log4jdbc from the property file " + propertyFile
					+ ":\n" + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					System.err.println("An error occurred trying to close the reader for the property file "
							+ propertyFile + ":\n" + e.getMessage());
				}
			}
		}

		return null;
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
