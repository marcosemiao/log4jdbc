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
package fr.ms.log4jdbc.serviceloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.util.CollectionsUtil;
import fr.ms.util.Service;

/**
*
* @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
*
*
* @author Marco Semiao
*
*/
public final class SqlOperationLoggerServiceLoader {
	
	private SqlOperationLoggerServiceLoader(){}
	
	public static SqlOperationLogger[] getMessageLogger(final String typeLogger) {
		final List results = new ArrayList();

		for (int i = 0; i < Holder.services.length; i++) {
			final SqlOperationLogger m = Holder.services[i];
			final boolean type = m.isLogger(typeLogger);
			if (type) {
				results.add(m);
			}
		}

		return (SqlOperationLogger[]) results.toArray(new SqlOperationLogger[results.size()]);
	}
	
	
	private static class Holder {
		private final static SqlOperationLogger[] services = getServices();

		private final static SqlOperationLogger[] getServices() {
			SqlOperationLogger[] messageLogger;

			final Iterator providers = Service.providers(SqlOperationLogger.class,
					Holder.class.getClassLoader());

			final List list = CollectionsUtil.convert(providers);

			messageLogger = (SqlOperationLogger[]) list.toArray(new SqlOperationLogger[list.size()]);

			return messageLogger;
		}
	}
}
