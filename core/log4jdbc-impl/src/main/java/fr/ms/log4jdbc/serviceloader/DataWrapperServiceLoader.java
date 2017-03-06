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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.ms.log4jdbc.data.DataWrapper;
import fr.ms.util.Service;

/**
*
* @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
*
*
* @author Marco Semiao
*
*/
public class DataWrapperServiceLoader {

	private DataWrapperServiceLoader(){}
	
	public static Object wrapData(final Class clazz,  Object obj)
	{
		final DataWrapper service = getService(clazz);
		if (service!=null)
		{
			obj = service.wrap(obj); 
		}
		
		return obj;
	}
	
	private static DataWrapper getService(final Class clazz) {

		DataWrapper service = (DataWrapper) Holder.services.get(clazz);

		return service;
	}
	
	private static class Holder {
		private final static Map services = getServices();

		private final static Map getServices() {
			Map services = new HashMap();

			final Iterator providers = Service.providers(DataWrapper.class, Holder.class.getClassLoader());

			while (providers.hasNext()) {
				try {
					final DataWrapper e = (DataWrapper)providers.next();
					services.put(e.getType(), e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}

			return services;
		}
	}
}
