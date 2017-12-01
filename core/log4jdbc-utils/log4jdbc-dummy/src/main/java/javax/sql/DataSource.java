package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource extends CommonDataSource, Wrapper {

	Connection getConnection() throws SQLException;

	Connection getConnection(String username, String password) throws SQLException;

}
