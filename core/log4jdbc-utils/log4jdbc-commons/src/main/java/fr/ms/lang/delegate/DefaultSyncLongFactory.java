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
package fr.ms.lang.delegate;

import java.util.Iterator;

import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.util.Service;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultSyncLongFactory implements SyncLongFactory {

	private final static SyncLongFactory instance = new DefaultSyncLongFactory();

	private static SyncLongFactory delegate;

	static {
		final Iterator providers = Service.providers(SyncLongFactory.class,
				DefaultSyncLongFactory.class.getClassLoader());

		while (providers.hasNext()) {
			try {
				final SyncLongFactory p = (SyncLongFactory) providers.next();

				if (p != null) {
					if (delegate == null || delegate.getPriority() < p.getPriority()) {
						delegate = p;
					}
				}
			} catch (final Throwable t) {
			}
		}
	}

	private DefaultSyncLongFactory() {
	}

	public static SyncLongFactory getInstance() {
		return instance;
	}

	public SyncLong newLong() {
		return delegate.newLong();
	}

	public SyncLong newLong(final long initialValue) {
		return delegate.newLong(initialValue);
	}

	public int getPriority() {
		return delegate.getPriority();
	}
}
