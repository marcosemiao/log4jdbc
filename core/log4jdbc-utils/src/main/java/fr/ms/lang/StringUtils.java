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
package fr.ms.lang;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class StringUtils {

	private final static StringMakerFactory stringMakerFactory = DefaultStringMakerFactory.getInstance();

	private StringUtils() {
	}

	public static String padRight(final String s, final int n) {
		final StringMaker sb = stringMakerFactory.newString();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String padRight(final String start, final String s, final int n) {
		return start + padRight(s, n - start.length());
	}

	public static String replaceAll(final String str, final String replace, final String replacement) {
		final StringMaker sb = stringMakerFactory.newString(str);
		int firstOccurrence = sb.toString().indexOf(replace);

		while (firstOccurrence != -1) {
			sb.replace(firstOccurrence, firstOccurrence + replace.length(), replacement);
			final int position = firstOccurrence + replacement.length();
			firstOccurrence = sb.toString().indexOf(replace, position);
		}

		return sb.toString();
	}

	public static String removePart(final String str, final String start, final String end) {
		return removePart(str, start, end, null, new Integer(0));
	}

	public static String removePart(final String str, final String start, final String end, final String exception) {
		return removePart(str, start, end, exception, new Integer(0));
	}

	public static int beginPart(final String str, final String start, final String end, final String exception,
			final int first) {

		if (str == null || str.isEmpty()) {
			return -1;
		}

		final int startComment = str.indexOf(start, first);
		final int endComment = str.indexOf(end, first);

		if (startComment == -1 || endComment == -1 || startComment >= endComment) {
			return -1;
		}

		if (exception != null) {
			final int startException = str.indexOf(exception);
			if (startComment == startException) {

				int beginPart = beginPart(str, start, end, exception, endComment + end.length());

				return beginPart;
			}
		}

		if (startComment > first) {
			return first;
		}

		return endComment + end.length();
	}

	private static String removePart(final String str, final String start, final String end, final String exception,
			final Integer first) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		final int startComment = str.indexOf(start, first.intValue());
		final int endComment = str.indexOf(end, first.intValue());

		if (startComment == -1 || endComment == -1 || startComment >= endComment) {
			return str;
		}

		if (exception != null) {
			final int startException = str.indexOf(exception);
			if (startComment == startException) {

				final String formatSql = removePart(str, start, end, exception, new Integer(endComment + end.length()));

				return formatSql;
			}
		}

		final String e1 = str.substring(0, startComment);
		final String e2 = str.substring(endComment + end.length(), str.length());

		final String replace = e1 + e2;

		String formatSql = removePart(replace, start, end, exception, first);

		return formatSql;
	}
}
