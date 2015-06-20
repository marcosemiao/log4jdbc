package fr.ms.log4jdbc.rdbms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * RDBMS specifics for the MySql db.
 *
 * @author Arthur Blake
 */
public class MySqlRdbmsSpecifics implements RdbmsSpecifics {

    private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

    private final static String TIME_PATTERN = "HH:mm:ss";

    private final static String SQL_DATE_PATTERN = "yyyy-MM-dd";

    private final static String UTIL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public boolean isRdbms(final String classType) {
	return classType.startsWith("com.mysql");
    }

    public DataRdbms getData(final Object object) {
	if (object instanceof java.sql.Time) {
	    final DateFormat df = new SimpleDateFormat(TIME_PATTERN);
	    final String dateString = df.format(object);
	    return new GenericDataRdbms(dateString, "'");
	}

	if (object instanceof java.sql.Date) {
	    final DateFormat df = new SimpleDateFormat(SQL_DATE_PATTERN);
	    final String dateString = df.format(object);
	    return new GenericDataRdbms(dateString, "'");
	}

	if (object instanceof java.util.Date) { // (includes java.sql.Timestamp)
	    final DateFormat df = new SimpleDateFormat(UTIL_DATE_PATTERN);
	    final String dateString = df.format(object);
	    return new GenericDataRdbms(dateString, "'");
	}

	return genericRdbms.getData(object);
    }

    public String getTypeQuery(final String sql) {
	return genericRdbms.getTypeQuery(sql);
    }

    public String removeComment(final String sql) {
	return genericRdbms.removeComment(sql);
    }

    public boolean isCaseSensitive() {
	return genericRdbms.isCaseSensitive();
    }
}
