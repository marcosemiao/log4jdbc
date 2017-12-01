package java.sql;

import java.util.logging.Logger;

public interface Driver {

	Connection connect(String url, java.util.Properties info) throws SQLException;

	boolean acceptsURL(String url) throws SQLException;

	DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException;

	int getMajorVersion();

	int getMinorVersion();

	boolean jdbcCompliant();

	Logger getParentLogger() throws SQLFeatureNotSupportedException;
}
