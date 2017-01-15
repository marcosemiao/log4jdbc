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
package fr.ms.log4jdbc.message;

import java.lang.reflect.Method;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.writer.MessageWriter;
import fr.ms.log4jdbc.writer.MessageWriterFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public interface MessageProcess extends MessageWriterFactory {

	void buildLog(MessageWriter messageWriter, SqlOperation message, Method method, Object[] args, Object invoke);

	void buildLog(MessageWriter messageWriter, SqlOperation message, Method method, Object[] args, Throwable exception);
}
