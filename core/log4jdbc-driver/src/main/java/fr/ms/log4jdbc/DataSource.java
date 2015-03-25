/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.log4jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.proxy.Handlers;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.utils.LongSync;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class DataSource implements InvocationHandler {

  private final static LongSync nbConnectionTotal = new LongSync();

  private final javax.sql.DataSource dataSource;

  private Class driverClass;
  private RdbmsSpecifics rdbmsSpecifics;

  private DataSource(final javax.sql.DataSource dataSource) {
    this(dataSource, GenericRdbmsSpecifics.getInstance());
  }

  private DataSource(final javax.sql.DataSource dataSource, final RdbmsSpecifics rdbmsSpecifics) {
    this.dataSource = dataSource;
    this.rdbmsSpecifics = rdbmsSpecifics;
  }

  private DataSource(final javax.sql.DataSource dataSource, final Class driverClass) {
    this.dataSource = dataSource;
    this.driverClass = driverClass;
  }

  public static javax.sql.DataSource getDataSource(final javax.sql.DataSource dataSource) {
    final DataSource d = new DataSource(dataSource);
    return getDataSource(d);
  }

  public static javax.sql.DataSource getDataSource(final javax.sql.DataSource dataSource,
      final RdbmsSpecifics rdbmsSpecifics) {
    final DataSource d = new DataSource(dataSource, rdbmsSpecifics);
    return getDataSource(d);
  }

  public static javax.sql.DataSource getDataSource(final javax.sql.DataSource dataSource, final Class driverClass) {
    final DataSource d = new DataSource(dataSource, driverClass);
    return getDataSource(d);
  }

  private static javax.sql.DataSource getDataSource(final DataSource d) {
    final ClassLoader classLoader = d.getClass().getClassLoader();
    final Class[] interfaces = new Class[]{javax.sql.DataSource.class};

    final javax.sql.DataSource instance = (javax.sql.DataSource) Proxy.newProxyInstance(classLoader, interfaces, d);

    return instance;
  }

  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final Object invoke = method.invoke(dataSource, args);

    if (invoke != null) {
      if (invoke instanceof Connection) {

        final Connection connection = (Connection) invoke;
        final JdbcContext jdbcContext = getJdbcContext();
        final Connection wrap = Handlers.getConnection(connection, jdbcContext);
        return wrap;
      }
    }

    return invoke;
  }

  private JdbcContext getJdbcContext() {
    if (driverClass != null) {
      return new JdbcContext(driverClass, "DataSource" + nbConnectionTotal.incrementAndGet());
    }

    return new JdbcContext(rdbmsSpecifics, "DataSource" + nbConnectionTotal.incrementAndGet());
  }
}
