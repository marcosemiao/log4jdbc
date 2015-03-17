package fr.ms.log4jdbc.rdbms;

import java.text.SimpleDateFormat;

/**
 * RDBMS specifics for the MySql db.
 *
 * @author Arthur Blake
 */
public class MySqlRdbmsSpecifics implements RdbmsSpecifics {

  private final RdbmsSpecifics genericRdbms = GenericRdbmsSpecifics.getInstance();

  public boolean isRdbms(final String classType) {
    return classType.equals("com.mysql.jdbc.Driver");
  }

  public DataRdbms getData(final Object object) {
    if (object instanceof java.sql.Time) {
      return new GenericDataRdbms(new SimpleDateFormat("HH:mm:ss").format(object), "'");
    }

    if (object instanceof java.sql.Date) {
      return new GenericDataRdbms(new SimpleDateFormat("yyyy-MM-dd").format(object), "'");
    }

    if (object instanceof java.util.Date) { // (includes java.sql.Timestamp)
      return new GenericDataRdbms(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object), "'");
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
