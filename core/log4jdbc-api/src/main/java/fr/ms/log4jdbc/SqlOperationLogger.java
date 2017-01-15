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

import java.lang.reflect.Method;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public interface SqlOperationLogger {

    final static String CONNECTION = "Connection";
    final static String STATEMENT = "Statement";
    final static String PREPARED_STATEMENT = "PreparedStatement";
    final static String CALLABLE_STATEMENT = "CallableStatement";
    final static String RESULT_SET = "ResultSet";

    final static String XA_RESOURCE = "XAResource";

    boolean isLogger(String typeLogger);

    boolean isEnabled();

    void buildLog(SqlOperation sqlOperation, Method method, Object[] args, Object invoke);

    void buildLog(SqlOperation sqlOperation, Method method, Object[] args, Throwable exception);
}
