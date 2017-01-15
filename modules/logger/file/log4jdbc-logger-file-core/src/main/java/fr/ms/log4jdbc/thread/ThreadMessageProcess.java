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

import java.lang.reflect.Method;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;
import fr.ms.log4jdbc.writer.MessageWriter;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ThreadMessageProcess implements MessageProcess {

	private final MessageProcess messageProcess;

	private final static SingleThreadPoolExecutor executor = SingleThreadPoolExecutor.getInstance();

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	public ThreadMessageProcess(final MessageProcess messageProcess) {
		this.messageProcess = messageProcess;
	}

	public MessageWriter newMessageWriter(final SqlOperation message, final Method method, final Object[] args,
			final Object invoke, final Throwable exception) {
		return messageProcess.newMessageWriter(message, method, args, invoke, exception);
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Object invoke) {
		final Runnable r = new MessageRunnable(messageWriter, message, method, args, invoke);
		executor.execute(r);
	}

	public void buildLog(final MessageWriter messageWriter, final SqlOperation message, final Method method,
			final Object[] args, final Throwable exception) {
		final Runnable r = new MessageRunnable(messageWriter, message, method, args, exception);
		executor.execute(r);
	}

	private class MessageRunnable implements Runnable {

		private final MessageWriter messageWriter;
		private final SqlOperation message;
		private final Method method;
		private final Object[] args;
		private Object invoke;
		private Throwable exception;

		public MessageRunnable(final MessageWriter messageWriter, final SqlOperation message, final Method method,
				final Object[] args, final Object invoke) {
			this.messageWriter = messageWriter;
			this.message = message;
			this.method = method;
			this.args = args;
			this.invoke = invoke;
		}

		public MessageRunnable(final MessageWriter messageWriter, final SqlOperation message, final Method method,
				final Object[] args, final Throwable exception) {
			this.messageWriter = messageWriter;
			this.message = message;
			this.method = method;
			this.args = args;
			this.exception = exception;
		}

		public void run() {
			if (exception == null) {
				messageProcess.buildLog(messageWriter, message, method, args, invoke);
			} else {
				messageProcess.buildLog(messageWriter, message, method, args, exception);
			}
		}
	}
}
