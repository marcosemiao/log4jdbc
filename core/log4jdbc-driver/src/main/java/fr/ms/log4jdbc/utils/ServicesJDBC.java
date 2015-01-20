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
package fr.ms.log4jdbc.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ms.log4jdbc.MessageLogger;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.serviceloader.Service;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class ServicesJDBC {

  private static final MessageLogger[] messageLogger;

  private static final RdbmsSpecifics[] rdbmsSpecifics;

  static {
    messageLogger = getMessageLogger();

    rdbmsSpecifics = getRdbmsSpecifics();
  }

  public static MessageLogger[] getMessageLogger(final String typeLogger) {
    final List results = new ArrayList();

    for (int i = 0; i < messageLogger.length; i++) {
      final MessageLogger m = messageLogger[i];
      final boolean type = m.isLogger(typeLogger);
      if (type) {
        results.add(m);
      }
    }

    return (MessageLogger[]) results.toArray(new MessageLogger[results.size()]);
  }

  public static RdbmsSpecifics getRdbmsSpecifics(final String classType) {

    for (int i = 0; i < rdbmsSpecifics.length; i++) {
      final RdbmsSpecifics r = rdbmsSpecifics[i];
      final boolean rdbms = r.isRdbms(classType);
      if (rdbms) {
        return r;
      }
    }
    return null;
  }

  private static MessageLogger[] getMessageLogger() {
    MessageLogger[] messageLogger;

    final Iterator providers = Service.providers(MessageLogger.class);

    final List list = new ArrayList();
    while (providers.hasNext()) {
      try {
        final MessageLogger m = (MessageLogger) providers.next();
        list.add(m);
      } catch (final Throwable t) {
        t.printStackTrace();
      }
    }

    messageLogger = (MessageLogger[]) list.toArray(new MessageLogger[list.size()]);

    return messageLogger;
  }

  private static RdbmsSpecifics[] getRdbmsSpecifics() {
    RdbmsSpecifics[] rdbmsSpecifics;

    final Iterator providers = Service.providers(RdbmsSpecifics.class);

    final List list = new ArrayList();
    while (providers.hasNext()) {
      try {
        final RdbmsSpecifics r = (RdbmsSpecifics) providers.next();
        list.add(r);
      } catch (final Throwable t) {
        t.printStackTrace();
      }
    }

    rdbmsSpecifics = (RdbmsSpecifics[]) list.toArray(new RdbmsSpecifics[list.size()]);

    return rdbmsSpecifics;
  }
}
