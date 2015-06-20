package fr.ms.log4jdbc.rdbms;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the Oracle DB.
 *
 * @author Arthur Blake
 */
public class OracleRdbmsSpecifics implements RdbmsSpecifics {

    private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

    private final static String TIMESTAMP_PATTERN = "MM/dd/yyyy HH:mm:ss.SSS";

    private final static String DATE_PATTERN = "MM/dd/yyyy HH:mm:ss";

    public boolean isRdbms(final String classType) {
	return classType.startsWith("oracle.jdbc");
    }

    public DataRdbms getData(final Object object) {
	if (object instanceof Timestamp) {
	    final DateFormat df = new SimpleDateFormat(TIMESTAMP_PATTERN);
	    final String dateString = df.format(object);
	    return new GenericDataRdbms("to_timestamp('", dateString, "', 'mm/dd/yyyy hh24:mi:ss.ff3')");
	}

	if (object instanceof Date) {
	    final DateFormat df = new SimpleDateFormat(DATE_PATTERN);
	    final String dateString = df.format(object);
	    return new GenericDataRdbms("to_date('", dateString, "', 'mm/dd/yyyy hh24:mi:ss')");
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
