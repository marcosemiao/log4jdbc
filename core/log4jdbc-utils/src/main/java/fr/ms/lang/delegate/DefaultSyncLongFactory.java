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

import fr.ms.lang.ClassUtils;
import fr.ms.lang.sync.impl.AtomicLongImpl;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.lang.sync.impl.SyncLongImpl;

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
		if (ClassUtils.classPresent("java.util.concurrent.atomic.AtomicLong")) {
			delegate = AtomicLongImpl.getSyncLongFactory();
		} else {
			delegate = SyncLongImpl.getSyncLongFactory();
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
}
