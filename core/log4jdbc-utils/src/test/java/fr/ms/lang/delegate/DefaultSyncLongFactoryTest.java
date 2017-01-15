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

import org.junit.Assert;
import org.junit.Test;

import fr.ms.lang.sync.impl.AtomicLongImpl;
import fr.ms.lang.sync.impl.SyncLong;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultSyncLongFactoryTest {

    @Test
    public void newLongTest() {
	final SyncLongFactory syncLong = DefaultSyncLongFactory.getInstance();

	final SyncLong newLong = syncLong.newLong();
	Assert.assertEquals(newLong.getClass(), AtomicLongImpl.class);
	Assert.assertEquals(0, newLong.get());

	long value = newLong.addAndGet(1);
	Assert.assertEquals(1, value);
	Assert.assertEquals(1, newLong.get());

	value = newLong.addAndGet(66);
	Assert.assertEquals(67, value);
	Assert.assertEquals(67, newLong.get());

	value = newLong.addAndGet(-15);
	Assert.assertEquals(52, value);
	Assert.assertEquals(52, newLong.get());

	value = newLong.incrementAndGet();
	Assert.assertEquals(53, value);
	Assert.assertEquals(53, newLong.get());

	value = newLong.incrementAndGet();
	Assert.assertEquals(54, value);
	Assert.assertEquals(54, newLong.get());

	value = newLong.decrementAndGet();
	Assert.assertEquals(53, value);
	Assert.assertEquals(53, newLong.get());

	value = newLong.incrementAndGet();
	Assert.assertEquals(54, value);
	Assert.assertEquals(54, newLong.get());

	value = newLong.decrementAndGet();
	Assert.assertEquals(53, value);
	Assert.assertEquals(53, newLong.get());

	value = newLong.decrementAndGet();
	Assert.assertEquals(52, value);
	Assert.assertEquals(52, newLong.get());
    }
}
