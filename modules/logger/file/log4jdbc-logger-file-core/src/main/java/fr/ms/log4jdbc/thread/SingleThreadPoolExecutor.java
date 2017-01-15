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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.util.logging.Logger;
import fr.ms.util.logging.LoggerManager;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SingleThreadPoolExecutor {

	private final static Logger LOG = LoggerManager.getLogger(SingleThreadPoolExecutor.class);

	private final static SingleThreadPoolExecutor INSTANCE = new SingleThreadPoolExecutor();
	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final WorkerRunnable w = new WorkerRunnable();

	private final Thread thread;

	private SingleThreadPoolExecutor() {
		thread = new Thread(w, "Log4Jdbc-Logger");
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public static SingleThreadPoolExecutor getInstance() {
		return INSTANCE;
	}

	public synchronized void execute(final Runnable command) {
		final int limit = props.logProcessThreadSize();

		final int size = w.pool.size();

		if (size > limit && thread.getPriority() != Thread.MAX_PRIORITY) {

			if (Thread.MAX_PRIORITY != thread.getPriority()) {
				thread.setPriority(Thread.MAX_PRIORITY);
			}
			logLimit();
		} else if (size > (limit / 2) && thread.getPriority() == Thread.MIN_PRIORITY) {
			if (Thread.NORM_PRIORITY != thread.getPriority()) {
				thread.setPriority(Thread.NORM_PRIORITY);
				logLimit();
			}
		} else if (size < (limit / 10)) {
			if (Thread.MIN_PRIORITY != thread.getPriority()) {
				thread.setPriority(Thread.MIN_PRIORITY);
				logLimit();
			}
		}

		w.execute(command);
	}

	private void logLimit() {
		if (LOG.isDebugEnabled()) {
			LOG.debug(
					"Log4JDBC : pool size limit : " + w.pool.size() + " - thread priority  : " + thread.getPriority());
		}
	}

	private static class WorkerRunnable implements Runnable {

		private final Verrou v = new Verrou();

		private final List pool = Collections.synchronizedList(new LinkedList());

		public void run() {
			while (true) {
				if (pool.isEmpty()) {
					v.attente();
				} else {
					final Runnable r = getNextRunnable();
					try {
						if (r != null) {
							r.run();
						}
					} catch (final Throwable t) {
						t.printStackTrace();
					}
				}
			}
		}

		public void execute(final Runnable command) {
			pool.add(command);
			v.libere();
		}

		private Runnable getNextRunnable() {
			final Runnable r = (Runnable) pool.get(0);
			pool.remove(0);
			return r;
		}
	}

	private static class Verrou {

		public void attente() {
			pose(false);
		}

		public void libere() {
			pose(true);
		}

		private synchronized void pose(final boolean libere) {
			if (libere) {
				notify();
			} else {
				try {
					wait();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
