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

import java.util.Iterator;
import java.util.List;

import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.util.CollectionsUtil;
import fr.ms.util.Service;

/**
*
* @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
*
*
* @author Marco Semiao
*
*/
public final class RdbmsSpecificsServiceLoader {

	private RdbmsSpecificsServiceLoader(){}
	
	public static RdbmsSpecifics getRdbmsSpecifics(final String classType) {

		for (int i = 0; i < Holder.services.length; i++) {
			final RdbmsSpecifics r = Holder.services[i];
			final boolean rdbms = r.isRdbms(classType);
			if (rdbms) {
				return r;
			}
		}
		return null;
	}
	
	private static class Holder {
		private final static RdbmsSpecifics[] services = getServices();

		private final static RdbmsSpecifics[] getServices() {
			RdbmsSpecifics[] rdbmsSpecifics;

			final Iterator providers = Service.providers(RdbmsSpecifics.class,
					Holder.class.getClassLoader());

			final List list = CollectionsUtil.convert(providers);

			rdbmsSpecifics = (RdbmsSpecifics[]) list.toArray(new RdbmsSpecifics[list.size()]);

			return rdbmsSpecifics;
		}
	}
}
