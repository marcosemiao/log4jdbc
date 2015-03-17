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
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public final class Log4JdbcStackTrace {

  private final static String CLASS = "fr.ms.log4jdbc.invocationhandler.WrapperMessageInvocationHandler";

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final static String nl = System.getProperty("line.separator");

  public static String getStackTraceFilter(final StackTraceElement[] stackTrace) {
    final StringBuilder sb = new StringBuilder();
    if (props.logStackTrace()) {

      final List<String> logStackTraceStartPackages = logStackTraceStartPackages();
      StackTraceElement[] stackTraceDump;
      if (logStackTraceStartPackages.isEmpty()) {
        stackTraceDump = Log4JdbcStackTrace.getStackTrace(stackTrace, false);
      } else {
        stackTraceDump = Log4JdbcStackTrace.getStackTrace(stackTrace, logStackTraceStartPackages);
      }
      for (final StackTraceElement stackTraceElement : stackTraceDump) {
        sb.append(stackTraceElement);
        sb.append(nl);
      }
    }
    return sb.toString();
  }

  private static List<String> logStackTraceStartPackages() {
    final String property = props.getStacktraceStartPackages();
    if (property == null || property.isEmpty()) {
      return new ArrayList<String>();
    }
    final String[] split = property.split(",");
    final List<String> asList = Arrays.asList(split);
    return asList;
  }

  public static StackTraceElement[] getStackTrace(final List<String> packages) {
    final StackTraceElement[] stackTraceDump = getStackTrace(false);

    final List<StackTraceElement> stackTracePackage = new ArrayList<StackTraceElement>();

    for (final StackTraceElement stackTraceElement : stackTraceDump) {
      final String className = stackTraceElement.getClassName();
      for (final String p : packages) {
        final boolean matches = className.startsWith(p);
        if (matches) {
          stackTracePackage.add(stackTraceElement);
          break;
        }
      }
    }

    return stackTracePackage.toArray(new StackTraceElement[0]);
  }

  public static StackTraceElement[] getStackTrace(final StackTraceElement[] stackTrace, final List<String> packages) {
    final StackTraceElement[] stackTraceDump = getStackTrace(stackTrace, false);

    final List<StackTraceElement> stackTracePackage = new ArrayList<StackTraceElement>();

    for (final StackTraceElement stackTraceElement : stackTraceDump) {
      final String className = stackTraceElement.getClassName();
      for (final String p : packages) {
        final boolean matches = className.startsWith(p.trim());
        if (matches) {
          stackTracePackage.add(stackTraceElement);
          break;
        }
      }
    }

    return stackTracePackage.toArray(new StackTraceElement[0]);
  }

  public static StackTraceElement[] getStackTrace() {
    final Throwable t = new Throwable();
    t.fillInStackTrace();

    final StackTraceElement[] stackTrace = t.getStackTrace();
    return stackTrace;
  }

  public static StackTraceElement[] getStackTrace(final boolean log4jdbcTrace) {
    final StackTraceElement[] stackTrace = getStackTrace();

    final StackTraceElement[] stackTraceDump = getStackTrace(stackTrace, log4jdbcTrace);

    return stackTraceDump;
  }

  public static StackTraceElement[] getStackTrace(final StackTraceElement[] stackTrace, final boolean log4jdbcTrace) {
    if (stackTrace == null || log4jdbcTrace) {
      return stackTrace;
    }
    int position = 0;
    for (position = 0; position < stackTrace.length; position++) {
      final String className = stackTrace[position].getClassName();
      if (CLASS.equals(className)) {
        break;
      }
    }
    position = position + 2;
    final int stackTraceLength = stackTrace.length - position;

    if (stackTraceLength < 1) {
      return stackTrace;
    }
    final StackTraceElement[] stackTraceDump = new StackTraceElement[stackTraceLength];

    System.arraycopy(stackTrace, position, stackTraceDump, 0, stackTraceLength);

    return stackTraceDump;
  }
}
