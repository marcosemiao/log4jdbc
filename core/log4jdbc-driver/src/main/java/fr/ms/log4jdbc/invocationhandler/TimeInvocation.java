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

import java.util.Date;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TimeInvocation {

    private final Date execDate = new Date();
    private long execTime;

    private Object invoke;
    private Throwable targetException;

    public Date getExecDate() {
	return execDate;
    }

    public long getExecTime() {
	return execTime;
    }

    public Object getInvoke() {
	return invoke;
    }

    public Throwable getTargetException() {
	return targetException;
    }

    public void calculExecTime(final long endTime) {
	this.execTime = (endTime - execDate.getTime());
    }

    public void setExecTime(final long execTime) {
	this.execTime = execTime;
    }

    public void setInvoke(final Object invoke) {
	this.invoke = invoke;
    }

    public void setTargetException(final Throwable targetException) {
	this.targetException = targetException;
    }

    public String toString() {
	return "TimeInvocation [execDate=" + execDate + ", execTime=" + execTime + ", invoke=" + invoke + ", targetException=" + targetException + "]";
    }
}
