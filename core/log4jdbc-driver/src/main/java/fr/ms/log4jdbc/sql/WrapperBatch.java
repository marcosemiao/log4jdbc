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

import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class WrapperBatch implements Batch {

    private final Batch batch;

    private final RdbmsSpecifics rdbms;

    private final FormatQuery formatQuery;

    public WrapperBatch(final Batch batch, final RdbmsSpecifics rdbms, final FormatQuery formatQuery) {
	if (batch == null || rdbms == null || formatQuery == null) {
	    throw new NullPointerException();
	}
	this.batch = batch;
	this.rdbms = rdbms;
	this.formatQuery = formatQuery;
    }

    public Query[] getQueriesBatch() {
	final Query[] queries = batch.getQueriesBatch();
	if (queries == null) {
	    return null;
	}

	final Query[] result = new Query[queries.length];
	for (int i = 0; i < queries.length; i++) {
	    final Query query = queries[i];
	    final Query wrap = new WrapperQuery(query, rdbms, formatQuery);
	    result[i] = wrap;
	}

	return result;
    }

    public String getBatchState() {
	return batch.getBatchState();
    }

    public long getBatchNumber() {
	return batch.getBatchNumber();
    }

    public long getOpenBatch() {
	return batch.getOpenBatch();
    }
}
