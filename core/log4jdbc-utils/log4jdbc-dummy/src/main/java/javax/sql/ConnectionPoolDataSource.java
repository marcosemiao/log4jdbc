package javax.sql;

import java.sql.SQLException;

public interface ConnectionPoolDataSource extends CommonDataSource {
	PooledConnection getPooledConnection() throws SQLException;

	PooledConnection getPooledConnection(String user, String password) throws SQLException;
}