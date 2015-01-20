package fr.ms.log4jdbc.rdbms;

import java.text.SimpleDateFormat;

/**
 * RDBMS specifics for the MySql db.
 *
 * @author Arthur Blake
 */
public class MySqlRdbmsSpecifics implements RdbmsSpecifics {

  private final RdbmsSpecifics genericRdbms = GenericRdbms.getInstance();

  public boolean isRdbms(final String classType) {
    return classType.equals("com.mysql.jdbc.Driver");
  }

  public String formatSql(final String sql) {
    return genericRdbms.formatSql(sql);
  }

  public String formatParameter(final Object object) {
    if (object instanceof java.sql.Time) {
      return "'" + new SimpleDateFormat("HH:mm:ss").format(object) + "'";
    }

    if (object instanceof java.sql.Date) {
      return "'" + new SimpleDateFormat("yyyy-MM-dd").format(object) + "'";
    }

    if (object instanceof java.util.Date) { // (includes java.sql.Timestamp)
      return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object) + "'";
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
