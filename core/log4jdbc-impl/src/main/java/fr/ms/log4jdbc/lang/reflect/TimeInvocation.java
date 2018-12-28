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
package fr.ms.log4jdbc.lang.reflect;

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

	private WrapInvocation wrapInvocation;

	private final Date startDate = new Date();
	private Date endDate;

	private long execTime;

	public WrapInvocation getWrapInvocation() {
		return wrapInvocation;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getExecTime() {
		return execTime;
	}

	void setWrapInvocation(WrapInvocation wrapInvocation) {
		this.wrapInvocation = wrapInvocation;
	}

	void finish() {
		setEndDate(new Date());
	}

	void setEndDate(final Date endDate) {
		this.endDate = endDate;
		this.execTime = endDate.getTime() - startDate.getTime();
	}

	public String toString() {
		return "TimeInvocation [startDate=" + startDate + ", endDate=" + endDate + ", execTime=" + execTime
				+ ", wrapInvocation=" + wrapInvocation + "]";
	}
}
