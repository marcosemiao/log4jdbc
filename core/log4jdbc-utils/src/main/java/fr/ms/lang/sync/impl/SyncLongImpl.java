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
package fr.ms.lang.sync.impl;

import fr.ms.lang.delegate.SyncLongFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SyncLongImpl implements SyncLong {

	private final static SyncLongFactory factory = new Factory();

	private long value;

	public SyncLongImpl() {
		this(0);
	}

	public SyncLongImpl(final long initialValue) {
		this.value = initialValue;
	}

	public long addAndGet(final long delta) {
		value = value + delta;
		return value;
	}

	public long incrementAndGet() {
		final long addValue = addAndGet(1);
		return addValue;
	}

	public long decrementAndGet() {
		final long addValue = addAndGet(-1);
		return addValue;
	}

	public long get() {
		return value;
	}

	public static SyncLongFactory getSyncLongFactory() {
		return factory;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SyncLongImpl other = (SyncLongImpl) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

	public String toString() {
		return String.valueOf(value);
	}

	private final static class Factory implements SyncLongFactory {

		public SyncLong newLong() {
			return new SyncLongImpl();
		}

		public SyncLong newLong(final long initialValue) {
			return new SyncLongImpl(initialValue);
		}
	}
}
