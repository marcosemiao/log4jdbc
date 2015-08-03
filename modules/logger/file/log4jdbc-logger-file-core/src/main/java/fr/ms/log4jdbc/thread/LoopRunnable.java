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
package fr.ms.log4jdbc.thread;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class LoopRunnable implements Runnable {

    private final Runnable r;

    private final long sleppMillis;

    public LoopRunnable(final Runnable r, final long sleppMillis) {
	this.r = r;
	this.sleppMillis = sleppMillis;
    }

    public void run() {
	while (true) {
	    try {
		r.run();
		Thread.sleep(sleppMillis);
	    } catch (final Throwable e) {
		e.printStackTrace();
	    }
	}
    }
}
