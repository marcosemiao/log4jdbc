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
package fr.ms.log4jdbc;

import fr.ms.log4jdbc.formatter.DefaultFormatQuery;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.FormatQueryFactory;
import fr.ms.log4jdbc.thread.ThreadMessageProcess;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
abstract class AbstractLogger implements FormatQueryFactory {

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final MessageProcess messageProcess;

	private final MessageProcess messageProcessThread;

	AbstractLogger(final MessageProcess messageProcess) {
		this.messageProcess = messageProcess;
		this.messageProcessThread = new ThreadMessageProcess(messageProcess);
	}

	MessageProcess getInstance() {
		if (props.logProcessThread()) {
			return messageProcessThread;
		}
		return messageProcess;
	}

	public FormatQuery getFormatQuery() {
		return DefaultFormatQuery.getInstance();
	}
}
