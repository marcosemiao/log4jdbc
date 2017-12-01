package javax.sql;

import java.sql.SQLException;

public interface XAConnection {

	javax.transaction.xa.XAResource getXAResource() throws SQLException;
}
