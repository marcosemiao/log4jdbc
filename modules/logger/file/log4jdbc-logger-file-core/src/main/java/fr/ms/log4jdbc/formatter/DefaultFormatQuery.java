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
package fr.ms.log4jdbc.formatter;

import fr.ms.lang.StringUtils;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultFormatQuery implements FormatQuery {

	private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

	private final static SQLFormatter sqlFormatter = SQLFormatterFactory.getInstance();

	private final static FormatQuery INSTANCE = new DefaultFormatQuery();

	private DefaultFormatQuery() {
	}

	public final static FormatQuery getInstance() {
		return INSTANCE;
	}

	public String format(String sql, final RdbmsSpecifics rdbms) {

		final String style = props.logRequeteStyleSQL();

		if (Log4JdbcProperties.REQUETE_SQL_STYLE_FORMAT.equals(style)) {
			sql = sqlFormatter.prettyPrint(sql, rdbms);
		} else {
			if (Log4JdbcProperties.REQUETE_SQL_STYLE_ONELINE.equals(style)) {
				sql = StringUtils.replaceAll(sql, "\r", " ");
				sql = StringUtils.replaceAll(sql, "\n", " ");
			}

			if (!props.logRequeteCommentSQL()) {
				sql = rdbms.removeComment(sql);
			}
		}

		if (props.logRequeteSemiColonAddSQL()) {
			sql = sql.trim();
			if (!sql.endsWith(";")) {
				sql = sql + ";";
			}
		}
		return sql;
	}
}
