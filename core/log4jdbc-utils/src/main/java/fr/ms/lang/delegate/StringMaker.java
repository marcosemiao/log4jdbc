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
package fr.ms.lang.delegate;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public interface StringMaker {

    StringMaker append(Object obj);

    StringMaker append(String str);

    StringMaker append(StringBuffer sb);

    StringMaker append(char[] str);

    StringMaker append(char[] str, int offset, int len);

    StringMaker append(boolean b);

    StringMaker append(char c);

    StringMaker append(int i);

    StringMaker append(long lng);

    StringMaker append(float f);

    StringMaker append(double d);

    StringMaker replace(int start, int end, String str);

    String substring(int start, int end);

    StringMaker delete(int start, int end);

    int length();
}
