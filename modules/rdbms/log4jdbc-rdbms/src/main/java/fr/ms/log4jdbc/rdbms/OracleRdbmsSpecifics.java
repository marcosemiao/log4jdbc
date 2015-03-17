package fr.ms.log4jdbc.rdbms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the Oracle DB.
 *
 * @author Arthur Blake
 */
public class OracleRdbmsSpecifics implements RdbmsSpecifics {

  private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

  public boolean isRdbms(final String classType) {
    return classType.equals("oracle.jdbc.driver.OracleDriver") || classType.equals("oracle.jdbc.OracleDriver");
  }

  public DataRdbms getData(final Object object) {
    if (object instanceof Timestamp) {
      return new GenericDataRdbms("to_timestamp('" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(object)
          + "', 'mm/dd/yyyy hh24:mi:ss.ff3')");
    }

    if (object instanceof Date) {
      return new GenericDataRdbms("to_date('" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(object)
          + "', 'mm/dd/yyyy hh24:mi:ss')");
    }

    return genericRdbms.getData(object);
  }

  public String getTypeQuery(final String sql) {
    return genericRdbms.getTypeQuery(sql);
  }

  public String removeComment(String sql) {
    return genericRdbms.removeComment(sql);
  }

  public boolean isCaseSensitive() {
    return genericRdbms.isCaseSensitive();
  }
}
