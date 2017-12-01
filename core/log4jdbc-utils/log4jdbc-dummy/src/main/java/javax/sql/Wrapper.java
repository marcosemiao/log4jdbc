package javax.sql;

public interface Wrapper {

	Object unwrap(Class iface) throws java.sql.SQLException;

	boolean isWrapperFor(Class iface) throws java.sql.SQLException;
}
