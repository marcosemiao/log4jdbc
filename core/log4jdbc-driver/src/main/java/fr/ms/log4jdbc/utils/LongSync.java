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

/**
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * 
 * 
 * @author Marco Semiao
 * 
 */
public class LongSync extends Number {

  private static final long serialVersionUID = 1L;

  private long value;

  public LongSync() {
    this(0);
  }

  public LongSync(final long value) {
    this.value = value;
  }

  public final long incrementAndGet() {
    final long addValue = addValue(1);
    return addValue;
  }

  public final long decrementAndGet() {
    final long addValue = addValue(-1);
    return addValue;
  }

  public synchronized final long addValue(final long v) {
    value = value + v;
    return value;
  }

  public long getValue() {
    return value;
  }

  public int intValue() {
    return (int) value;
  }

  public long longValue() {
    return value;
  }

  public float floatValue() {
    return value;
  }

  public double doubleValue() {
    return value;
  }

  public String toString() {
    return "LongSync [value=" + value + "]";
  }
}
