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
package fr.ms.log4jdbc.writer;

import java.util.Iterator;

import fr.ms.lang.DefaultStringMakerFactory;
import fr.ms.lang.StringMaker;
import fr.ms.lang.StringMakerFactory;
import fr.ms.log4jdbc.message.MessageHandler;
import fr.ms.log4jdbc.message.resultset.ResultSetCollector;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.utils.Trace;
import fr.ms.log4jdbc.writer.resultset.DefaultResultSetPrinterFormatCell;
import fr.ms.log4jdbc.writer.resultset.ResultSetPrinterFormatCell;
import fr.ms.log4jdbc.writer.resultset.ResultSetPrinterIterator;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class MessageWriterImpl implements MessageWriter {

    private final static StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();

    private final String threadName = Thread.currentThread().getName();

    private final MessageHandler message;

    private final ResultSetPrinterFormatCell formatCell;

    private ResultSetCollector resultSetCollector;

    private final static int MAX = 10000;

    private final static String nl = System.getProperty("line.separator");

    public MessageWriterImpl(final MessageHandler message) {
	this.message = message;
	this.formatCell = new DefaultResultSetPrinterFormatCell(message.getRdbms());
    }

    public void traceMessage(final String str) {
	if (resultSetCollector != null && resultSetCollector.getRows() != null && MAX < resultSetCollector.getRows().length) {
	    Trace.print(traceHeader());
	    Trace.print(str);
	    traceResultSet();
	    Trace.print(traceFooter());
	} else {
	    final StringMaker sb = stringFactory.newString();
	    sb.append(traceHeader());
	    sb.append(nl);
	    sb.append(str);
	    sb.append(nl);
	    final Iterator printResultSet = new ResultSetPrinterIterator(resultSetCollector, formatCell, MAX);
	    if (printResultSet.hasNext()) {
		sb.append(printResultSet.next());
		sb.append(nl);
	    }
	    sb.append(traceFooter());
	    Trace.print(sb.toString());
	}
    }

    public String traceHeader() {
	final StringMaker sb = stringFactory.newString();

	sb.append(message.getDate());
	sb.append(" - ");
	sb.append(threadName);
	sb.append(nl);
	sb.append(message.getConnectionNumber());
	sb.append(". Total ");
	sb.append(message.getOpenConnection());
	sb.append(nl);

	return sb.toString();
    }

    public void traceResultSet() {
	final Iterator iterator = new ResultSetPrinterIterator(resultSetCollector, formatCell, MAX);

	while (iterator.hasNext()) {
	    final String next = (String) iterator.next();
	    Trace.print(next);
	}
    }

    public String traceFooter() {
	final long execTime;

	final Query query = message.getQuery();
	if (query != null) {
	    execTime = query.getExecTime();
	} else {
	    execTime = message.getExecTime();
	}

	final StringMaker sb = stringFactory.newString();

	sb.append(" {executed in ");
	sb.append(execTime);
	sb.append(" ms} ");
	sb.append(nl);
	sb.append("****************************************************************");

	return sb.toString();
    }

    public void setResultSetCollector(final ResultSetCollector resultSetCollector) {
	this.resultSetCollector = resultSetCollector;
    }
}
