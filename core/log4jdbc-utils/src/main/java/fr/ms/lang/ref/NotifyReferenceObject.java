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
package fr.ms.lang.ref;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

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
public class NotifyReferenceObject implements ReferenceObject {

	private final static Logger LOG = LoggerManager.getLogger(NotifyReferenceObject.class);

	private final static String nl = System.getProperty("line.separator");

	private final String message;

	private final Reference reference;

	private boolean firstCleanGC = true;

	NotifyReferenceObject(final String message, final Reference reference) {
		this.message = message;
		this.reference = reference;
	}

	public void clear() {
		reference.clear();
	}

	public boolean enqueue() {
		return reference.enqueue();
	}

	public Object get() {
		final Object obj = reference.get();

		if (firstCleanGC && obj == null) {
			if (LOG.isWarnEnabled()) {
				final StringWriter sw = new StringWriter();
				new Throwable().printStackTrace(new PrintWriter(sw));
				final String stackTrace = sw.toString();
				LOG.warn(message + nl + stackTrace);
			}

			firstCleanGC = false;
		}

		return obj;
	}

	public boolean isEnqueued() {
		return reference.isEnqueued();
	}

	static ReferenceObject newSoftReference(final String message, final Object referent) {
		final Reference soft = new SoftReference(referent);

		return new NotifyReferenceObject(message, soft);
	}

	public String toString() {
		return reference.get().toString();
	}
}
