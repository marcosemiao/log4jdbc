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
package fr.ms.log4jdbc.jboss5.writer;

import fr.ms.log4jdbc.jboss5.marshaller.decorator.StackTraceThreadLocal;
import fr.ms.log4jdbc.writer.MessageWriterFactory;
import fr.ms.log4jdbc.writer.WrapperMessageWriterFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class WrapperJBoss5MessageWriterFactory implements WrapperMessageWriterFactory {

	public boolean isEnabled() {
		try {
			Class.forName(StackTraceThreadLocal.class.getName());
			return true;
		} catch (final Throwable e) {
		}
		return false;
	}

	public int getPriority() {
		return 100;
	}

	public MessageWriterFactory getMessageWriterFactory() {
		return Holder.factory;
	}

	private static class Holder {
		private final static MessageWriterFactory factory = new JBoss5MessageWriterFactory();
	}
}
