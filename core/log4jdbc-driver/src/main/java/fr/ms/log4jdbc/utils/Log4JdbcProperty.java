package fr.ms.log4jdbc.utils;

public final class Log4JdbcProperty {

  public final static boolean getProperty(final String key, final boolean defaultValue) {
    final String property = System.getProperty(key, new Boolean(defaultValue).toString());

    return Boolean.valueOf(property).booleanValue();
  }
}
