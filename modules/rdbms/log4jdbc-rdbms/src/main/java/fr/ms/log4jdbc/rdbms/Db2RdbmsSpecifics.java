package fr.ms.log4jdbc.rdbms;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the IBM DB2.
 *
 * @author qxo(qxodream@gmail.com)
 */
public class Db2RdbmsSpecifics implements RdbmsSpecifics {

  private final RdbmsSpecifics genericRdbms = GenericRdbms.getInstance();

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("'TIMESTAMP('''yyyy-MM-dd HH:mm:ss.SSS''')'");

  public boolean isRdbms(final String classType) {
    return classType.equals("com.ibm.db2.jcc.DB2Driver") || classType.equals("COM.ibm.db2.jdbc.app.DB2Driver")
        || classType.equals("COM.ibm.db2.jdbc.net.DB2Driver");
  }

  public String formatSql(final String sql) {
    return genericRdbms.formatSql(sql);
  }

  public String formatParameter(final Object object) {
    if (object instanceof Date) {
      return DATE_FORMAT.format((Date) object);
    }
    return genericRdbms.formatParameter(object);
  }

  public String getTypeQuery(String sql) {
    return genericRdbms.getTypeQuery(sql);
  }

  public boolean isCaseSensitive() {
    return genericRdbms.isCaseSensitive();
  }
}
