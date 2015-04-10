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

import javax.sql.DataSource;

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
public class DataSourceFactory implements InvocationHandler {

  private final static LongSync nbConnectionTotal = new LongSync();

  private final DataSource dataSource;

  private Class driverClass;
  private RdbmsSpecifics rdbmsSpecifics;

  private DataSourceFactory(final DataSource dataSource) {
    this(dataSource, GenericRdbmsSpecifics.getInstance());
  }

  private DataSourceFactory(final DataSource dataSource, final RdbmsSpecifics rdbmsSpecifics) {
    this.dataSource = dataSource;
    this.rdbmsSpecifics = rdbmsSpecifics;
  }

  private DataSourceFactory(final DataSource dataSource, final Class driverClass) {
    this.dataSource = dataSource;
    this.driverClass = driverClass;
  }

  public static DataSource getDataSource(final DataSource dataSource) {
    final DataSourceFactory d = new DataSourceFactory(dataSource);
    return getDataSource(d);
  }

  public static DataSource getDataSource(final DataSource dataSource, final RdbmsSpecifics rdbmsSpecifics) {
    final DataSourceFactory d = new DataSourceFactory(dataSource, rdbmsSpecifics);
    return getDataSource(d);
  }

  public static DataSource getDataSource(final DataSource dataSource, final Class driverClass) {
    final DataSourceFactory d = new DataSourceFactory(dataSource, driverClass);
    return getDataSource(d);
  }

  private static DataSource getDataSource(final DataSourceFactory d) {
    final ClassLoader classLoader = d.getClass().getClassLoader();
    final Class[] interfaces = new Class[]{DataSource.class};

    final DataSource instance = (DataSource) Proxy.newProxyInstance(classLoader, interfaces, d);

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
