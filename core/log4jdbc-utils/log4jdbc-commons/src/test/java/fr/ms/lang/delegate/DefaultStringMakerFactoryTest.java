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

import org.junit.Assert;
import org.junit.Test;

import fr.ms.lang.stringmaker.impl.StringBuilderImpl;
import fr.ms.lang.stringmaker.impl.StringMaker;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultStringMakerFactoryTest {

    @Test
    public void newStringTest() {
	final StringMakerFactory stringMaker = DefaultStringMakerFactory.getInstance();

	StringMaker newString = stringMaker.newString();
	Assert.assertEquals(newString.getClass(), StringBuilderImpl.class);

	newString = newString.append(true);
	Assert.assertEquals("true", newString.toString());

	newString = newString.append(false);
	Assert.assertEquals("truefalse", newString.toString());

	newString = newString.append('e');
	Assert.assertEquals("truefalsee", newString.toString());

	final char[] chararray = { 'a', 'r', 'h', 'j' };
	newString = newString.append(chararray);
	Assert.assertEquals("truefalseearhj", newString.toString());

	newString = newString.append(25.4);
	Assert.assertEquals("truefalseearhj25.4", newString.toString());

	newString = newString.append(47f);
	Assert.assertEquals("truefalseearhj25.447.0", newString.toString());

	newString = newString.append(624551);
	Assert.assertEquals("truefalseearhj25.447.0624551", newString.toString());

	newString = newString.append(new Byte("10"));
	Assert.assertEquals("truefalseearhj25.447.062455110", newString.toString());

	newString = newString.append("string");
	Assert.assertEquals("truefalseearhj25.447.062455110string", newString.toString());

	newString = newString.append(new StringBuffer("buffer"));
	Assert.assertEquals("truefalseearhj25.447.062455110stringbuffer", newString.toString());

	newString = newString.append(chararray, 2, 2);
	Assert.assertEquals("truefalseearhj25.447.062455110stringbufferhj", newString.toString());

	newString = newString.replace(6, 14, "re");
	Assert.assertEquals("truefare25.447.062455110stringbufferhj", newString.toString());

	final String substring = newString.substring(20, 35);
	Assert.assertEquals("5110stringbuffe", substring);

	newString = newString.delete(16, 20);
	Assert.assertEquals("truefare25.447.05110stringbufferhj", newString.toString());

	Assert.assertEquals(34, newString.length());
    }

    @Test
    public void newStringCapacityTest() {
	final StringMakerFactory stringMaker = DefaultStringMakerFactory.getInstance();

	StringMaker newString = stringMaker.newString(10);
	Assert.assertEquals(newString.getClass(), StringBuilderImpl.class);

	newString = newString.append(true);
	Assert.assertEquals("true", newString.toString());

	newString = newString.append(false);
	Assert.assertEquals("truefalse", newString.toString());

	newString = newString.append('e');
	Assert.assertEquals("truefalsee", newString.toString());

	final char[] chararray = { 'a', 'r', 'h', 'j' };
	newString = newString.append(chararray);
	Assert.assertEquals("truefalseearhj", newString.toString());

	newString = newString.append(25.4);
	Assert.assertEquals("truefalseearhj25.4", newString.toString());

	newString = newString.append(47f);
	Assert.assertEquals("truefalseearhj25.447.0", newString.toString());

	newString = newString.append(624551);
	Assert.assertEquals("truefalseearhj25.447.0624551", newString.toString());

	newString = newString.append(new Byte("10"));
	Assert.assertEquals("truefalseearhj25.447.062455110", newString.toString());

	newString = newString.append("string");
	Assert.assertEquals("truefalseearhj25.447.062455110string", newString.toString());

	newString = newString.append(new StringBuffer("buffer"));
	Assert.assertEquals("truefalseearhj25.447.062455110stringbuffer", newString.toString());

	newString = newString.append(chararray, 2, 2);
	Assert.assertEquals("truefalseearhj25.447.062455110stringbufferhj", newString.toString());

	newString = newString.replace(6, 14, "re");
	Assert.assertEquals("truefare25.447.062455110stringbufferhj", newString.toString());

	final String substring = newString.substring(20, 35);
	Assert.assertEquals("5110stringbuffe", substring);

	newString = newString.delete(16, 20);
	Assert.assertEquals("truefare25.447.05110stringbufferhj", newString.toString());

	Assert.assertEquals(34, newString.length());
    }

    @Test
    public void newStringDefaultTest() {
	final StringMakerFactory stringMaker = DefaultStringMakerFactory.getInstance();

	StringMaker newString = stringMaker.newString("default");
	Assert.assertEquals(newString.getClass(), StringBuilderImpl.class);
	Assert.assertEquals("default", newString.toString());

	newString = newString.append(true);
	Assert.assertEquals("defaulttrue", newString.toString());

	newString = newString.append(false);
	Assert.assertEquals("defaulttruefalse", newString.toString());

	newString = newString.append('e');
	Assert.assertEquals("defaulttruefalsee", newString.toString());

	final char[] chararray = { 'a', 'r', 'h', 'j' };
	newString = newString.append(chararray);
	Assert.assertEquals("defaulttruefalseearhj", newString.toString());

	newString = newString.append(25.4);
	Assert.assertEquals("defaulttruefalseearhj25.4", newString.toString());

	newString = newString.append(47f);
	Assert.assertEquals("defaulttruefalseearhj25.447.0", newString.toString());

	newString = newString.append(624551);
	Assert.assertEquals("defaulttruefalseearhj25.447.0624551", newString.toString());

	newString = newString.append(new Byte("10"));
	Assert.assertEquals("defaulttruefalseearhj25.447.062455110", newString.toString());

	newString = newString.append("string");
	Assert.assertEquals("defaulttruefalseearhj25.447.062455110string", newString.toString());

	newString = newString.append(new StringBuffer("buffer"));
	Assert.assertEquals("defaulttruefalseearhj25.447.062455110stringbuffer", newString.toString());

	newString = newString.append(chararray, 2, 2);
	Assert.assertEquals("defaulttruefalseearhj25.447.062455110stringbufferhj", newString.toString());

	newString = newString.replace(6, 14, "re");
	Assert.assertEquals("defaulreseearhj25.447.062455110stringbufferhj", newString.toString());

	final String substring = newString.substring(20, 35);
	Assert.assertEquals("7.062455110stri", substring);

	newString = newString.delete(16, 20);
	Assert.assertEquals("defaulreseearhj27.062455110stringbufferhj", newString.toString());

	Assert.assertEquals(41, newString.length());
    }
}
