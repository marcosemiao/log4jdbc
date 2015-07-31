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

import java.util.concurrent.atomic.AtomicLong;

import fr.ms.lang.delegate.SyncLong;
import fr.ms.lang.delegate.SyncLongFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class AtomicLongImpl implements SyncLong {

    private final static SyncLongFactory factory = new Factory();

    private final AtomicLong atomicLong;

    public AtomicLongImpl() {
	atomicLong = new AtomicLong();
    }

    public AtomicLongImpl(final long initialValue) {
	atomicLong = new AtomicLong(initialValue);
    }

    public long addAndGet(final long delta) {
	return atomicLong.addAndGet(delta);
    }

    public long incrementAndGet() {
	return atomicLong.incrementAndGet();
    }

    public long decrementAndGet() {
	return atomicLong.decrementAndGet();
    }

    public long get() {
	return atomicLong.get();
    }

    public static SyncLongFactory getSyncLongFactory() {
	return factory;
    }

    private final static class Factory implements SyncLongFactory {

	public SyncLong newLong() {
	    return new AtomicLongImpl();
	}

	public SyncLong newLong(final long initialValue) {
	    return new AtomicLongImpl(initialValue);
	}
    }
}
