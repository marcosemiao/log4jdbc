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
package fr.ms.log4jdbc.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import fr.ms.log4jdbc.utils.LongSync;

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class DevMessageInvocationHandler implements InvocationHandler {

  private final InvocationHandler invocationHandler;

  private static long maxTime;

  private static String maxMethodName;

  private static LongSync averageTime = new LongSync();
  private static LongSync quotient = new LongSync();

  public DevMessageInvocationHandler(final InvocationHandler invocationHandler) {
    this.invocationHandler = invocationHandler;
  }

  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final long start = System.currentTimeMillis();

    final TimeInvocation invokeTime = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

    final long end = System.currentTimeMillis();

    final long time = (end - start) - invokeTime.getExecTime();

    final String methodName = getMethodCall(method.getDeclaringClass().getName() + "." + method.getName(), args);

    if (time > maxTime) {
      maxTime = time;
      maxMethodName = methodName;
    }

    averageTime.addValue(time);

    final StringBuffer sb = new StringBuffer();
    sb.append("Time Process : ");
    sb.append(time);
    sb.append(" ms - Average Time : ");
    sb.append(averageTime.getValue() / quotient.incrementAndGet());
    sb.append(" ms - Method Name : ");
    sb.append(methodName);
    sb.append(" - Max Time Process : ");
    sb.append(maxTime);
    sb.append(" ms - Max Method Name : ");
    sb.append(maxMethodName);

    System.out.println(sb.toString());

    return invokeTime.getInvoke();
  }

  public static String getMethodCall(final String methodName, final Object[] args) {
    final StringBuffer sb = new StringBuffer();
    sb.append(methodName);
    sb.append("(");

    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        final Object arg = args[i];
        sb.append(arg.getClass());
        if (i < args.length - 1) {
          sb.append(",");
        }
      }
    }
    sb.append(");");

    return sb.toString();
  }
}
