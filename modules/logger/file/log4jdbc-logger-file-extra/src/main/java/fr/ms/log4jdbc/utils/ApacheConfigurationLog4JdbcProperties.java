package fr.ms.log4jdbc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import fr.ms.util.ServiceLoader;

@ServiceLoader(Log4JdbcProperties.class)
public class ApacheConfigurationLog4JdbcProperties extends Log4JdbcProperties {

	protected Properties getLoadProperties() {
		final InputStream is = getInputStream(propertyFile);
		try {
			if (is == null) {
				return null;
			}
			final PropertiesConfiguration config = new PropertiesConfiguration();

			config.read(new InputStreamReader(is));
			return ConfigurationConverter.getProperties(config);
		} catch (final IOException e) {
			System.err.println("An error occurred trying to initialize log4jdbc from the property file " + propertyFile
					+ ":\n" + e.getMessage());
		} catch (final ConfigurationException e) {
			System.err.println(
					"An error occurred trying to process the log4jdbc configuration properties in the property file "
							+ propertyFile + ":\n" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException e) {
					System.err.println("An error occurred trying to close the reader for the property file "
							+ propertyFile + ":\n" + e.getMessage());
				}
			}
		}
		return null;
	}
}
