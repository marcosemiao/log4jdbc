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
package fr.ms.log4jdbc.messagefactory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.log4jdbc.context.JdbcContext;
import fr.ms.log4jdbc.invocationhandler.MessageInvocationHandler.MessageInvocationContext;
import fr.ms.log4jdbc.invocationhandler.TimeInvocation;
import fr.ms.log4jdbc.message.MessageHandlerImpl;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QuerySQLFactory;
import fr.ms.log4jdbc.sql.impl.WrapperQuery;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class PreparedStatementHandler extends StatementHandler {

  private WrapperQuery newQuery;

  public PreparedStatementHandler(final PreparedStatement preparedStatement, final JdbcContext jdbcContext,
      final String sql, final QuerySQLFactory querySQLFactory) {
    super(preparedStatement, querySQLFactory);

    query = querySQLFactory.newQuerySQL(jdbcContext, sql);
  }

  public MessageHandlerImpl transformMessage(final Object proxy, final Method method, final Object[] args,
      final MessageInvocationContext mic, final MessageHandlerImpl message) {

    final TimeInvocation timeInvocation = mic.getInvokeTime();
    final JdbcContext jdbcContext = mic.getJdbcContext();

    final String nameMethod = method.getName();

    final boolean addBatchMethod = nameMethod.equals("addBatch") && args == null;
    if (addBatchMethod) {
      query.setMethodQuery(Query.METHOD_BATCH);
      query.setTimeInvocation(timeInvocation);

      jdbcContext.addQuery(query, true);

      mic.setQuery(query);
      message.setQuery(query);

      // Creation de la prochaine requete
      newQuery = createWrapperQuery(querySQLFactory, jdbcContext, query);

      return message;
    }

    final boolean setNullMethod = nameMethod.equals("setNull") && args != null && args.length >= 1;
    if (setNullMethod) {
      if (newQuery != null) {
        this.query = newQuery;
        newQuery = null;
      }
      final Object param = args[0];
      final Object value = null;
      query.putParams(param, value);
      return message;
    }

    final boolean setMethod = nameMethod.startsWith("set") && args != null && args.length >= 2;
    if (setMethod) {
      if (newQuery != null) {
        this.query = newQuery;
        newQuery = null;
      }
      final Object param = args[0];
      final Object value = args[1];
      query.putParams(param, value);
      return message;
    }

    final boolean executeMethod = nameMethod.startsWith("execute") && !nameMethod.equals("executeBatch")
        && args == null;
    if (executeMethod) {

      query.setMethodQuery(Query.METHOD_EXECUTE);
      query.setTimeInvocation(timeInvocation);
      final Integer updateCount = getUpdateCount(timeInvocation, method);
      query.setUpdateCount(updateCount);

      jdbcContext.addQuery(query, false);

      mic.setQuery(query);
      message.setQuery(query);

      // Creation de la prochaine requete
      newQuery = createWrapperQuery(querySQLFactory, jdbcContext, query);

      return message;
    }

    return super.transformMessage(proxy, method, args, mic, message);
  }

  private static WrapperQuery createWrapperQuery(final QuerySQLFactory querySQLFactory, final JdbcContext jdbcContext,
      final WrapperQuery query) {
    final WrapperQuery newQuery = querySQLFactory.newQuerySQL(jdbcContext, query.getJDBCQuery());

    final Map jdbcParameters = query.getJDBCParameters();

    final Iterator entries = jdbcParameters.entrySet().iterator();
    while (entries.hasNext()) {
      final Entry thisEntry = (Entry) entries.next();
      final Object key = thisEntry.getKey();
      final Object value = thisEntry.getValue();

      newQuery.putParams(key, value);
    }

    return newQuery;
  }
}
