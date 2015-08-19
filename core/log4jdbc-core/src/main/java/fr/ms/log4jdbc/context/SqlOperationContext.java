package fr.ms.log4jdbc.context;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.JdbcContext;
import fr.ms.log4jdbc.sql.QueryImpl;

public class SqlOperationContext {

    private final TimeInvocation invokeTime;
    private final JdbcContext jdbcContext;

    private QueryImpl query;

    public SqlOperationContext(final TimeInvocation invokeTime, final JdbcContext jdbcContext) {
	this.invokeTime = invokeTime;
	this.jdbcContext = jdbcContext;
    }

    public TimeInvocation getInvokeTime() {
	return invokeTime;
    }

    public JdbcContext getJdbcContext() {
	return jdbcContext;
    }

    public QueryImpl getQuery() {
	return query;
    }

    public void setQuery(final QueryImpl query) {
	this.query = query;
    }
}
