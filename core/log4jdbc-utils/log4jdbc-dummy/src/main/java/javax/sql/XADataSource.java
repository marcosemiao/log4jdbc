package javax.sql;

import java.sql.SQLException;

public interface XADataSource extends CommonDataSource {
	XAConnection getXAConnection() throws SQLException;

	XAConnection getXAConnection(String user, String password) throws SQLException;
}
