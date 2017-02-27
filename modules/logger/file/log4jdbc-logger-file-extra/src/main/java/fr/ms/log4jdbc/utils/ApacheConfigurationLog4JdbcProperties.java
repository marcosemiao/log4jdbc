package fr.ms.log4jdbc.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import fr.ms.util.ServiceLoader;

@ServiceLoader(Log4JdbcProperties.class)
public class ApacheConfigurationLog4JdbcProperties extends Log4JdbcProperties {

	@Override
	protected Properties getLoadProperties() {
		final Reader reader = getInputStream(propertyFile);
		try {
			if (reader == null) {
				return null;
			}
			final PropertiesConfiguration config = new PropertiesConfiguration();
			config.read(reader);
			return ConfigurationConverter.getProperties(config);
		} catch (final IOException e) {
			System.err.println("An error occurred trying to initialize log4jdbc from the property file " + propertyFile
					+ ":\n" + e.getMessage());
		} catch (final ConfigurationException e) {
			System.err.println(
					"An error occurred trying to process the log4jdbc configuration properties in the property file "
							+ propertyFile + ":\n" + e.getMessage());
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
}
