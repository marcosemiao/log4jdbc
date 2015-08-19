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
package fr.ms.log4jdbc.sql;

import java.util.Date;
import java.util.Map;

import fr.ms.log4jdbc.context.Batch;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.resultset.ResultSetCollector;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class WrapperQuery implements Query {

    private final Query query;

    private final RdbmsSpecifics rdbms;

    private final FormatQuery formatQuery;

    public WrapperQuery(final Query query, final RdbmsSpecifics rdbms, final FormatQuery formatQuery) {
	if (query == null || rdbms == null || formatQuery == null) {
	    throw new NullPointerException();
	}
	this.query = query;
	this.rdbms = rdbms;
	this.formatQuery = formatQuery;
    }

    public String getJDBCQuery() {
	final String jdbcQuery = query.getJDBCQuery();
	final String format = formatQuery.format(jdbcQuery, rdbms);
	return format;
    }

    public String getSQLQuery() {
	final String sqlQuery = query.getSQLQuery();
	final String format = formatQuery.format(sqlQuery, rdbms);
	return format;
    }

    public Date getDate() {
	return query.getDate();
    }

    public long getExecTime() {
	return query.getExecTime();
    }

    public long getQueryNumber() {
	return query.getQueryNumber();
    }

    public String getMethodQuery() {
	return query.getMethodQuery();
    }

    public Map getJDBCParameters() {
	return query.getJDBCParameters();
    }

    public String getTypeQuery() {
	return query.getTypeQuery();
    }

    public Integer getUpdateCount() {
	return query.getUpdateCount();
    }

    public ResultSetCollector getResultSetCollector() {
	return query.getResultSetCollector();
    }

    public String getState() {
	return query.getState();
    }

    public Transaction getTransaction() {
	return query.getTransaction();
    }

    public Batch getBatch() {
	return query.getBatch();
    }

    public String toString() {
	return "WrapperQuery [query=" + query + ", rdbms=" + rdbms + ", formatQuery=" + formatQuery + "]";
    }
}
