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
package fr.ms.log4jdbc.jboss5.marshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.invocation.unified.marshall.InvocationMarshaller;
import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.MarshallerDecorator;

import fr.ms.log4jdbc.jboss5.marshaller.decorator.StackTraceInvocationDecorator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class InvocationMarshallerWrapper extends InvocationMarshaller {

	private static final long serialVersionUID = 1L;

	private List<MarshallerDecorator> invocationDecorator = new ArrayList<MarshallerDecorator>();

	public InvocationMarshallerWrapper() {
		final MarshallerDecorator decorator = new StackTraceInvocationDecorator();
		addInvocationDecorator(decorator);
	}

	public void addInvocationDecorator(final MarshallerDecorator invocationDecorator) {
		this.invocationDecorator.add(invocationDecorator);
	}

	public void setInvocationDecorator(final List<MarshallerDecorator> invocationDecorator) {
		this.invocationDecorator = invocationDecorator;
	}

	@Override
	public Object addDecoration(Object dataObject) throws IOException {

		dataObject = super.addDecoration(dataObject);

		for (final MarshallerDecorator decorator : invocationDecorator) {
			dataObject = decorator.addDecoration(dataObject);
		}

		return dataObject;
	}

	@Override
	public Marshaller cloneMarshaller() throws CloneNotSupportedException {
		final InvocationMarshallerWrapper marshaller = new InvocationMarshallerWrapper();

		return marshaller;
	}
}
