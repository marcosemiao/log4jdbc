package fr.ms.log4jdbc.rdbms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the IBM DB2.
 *
 * @author qxo(qxodream@gmail.com)
 */
public class Db2RdbmsSpecifics implements RdbmsSpecifics {

    private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public boolean isRdbms(final String classType) {
	return classType.startsWith("com.ibm.db2") || classType.startsWith("COM.ibm.db2");
    }

    public DataRdbms getData(final Object object) {
	if (object instanceof Date) {
	    final DateFormat df = new SimpleDateFormat(DATE_PATTERN);
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
