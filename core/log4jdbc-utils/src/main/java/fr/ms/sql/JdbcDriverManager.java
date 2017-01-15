/*
 * This file is part of Log4Jdbc.
 *
 * Logimport java.io.PrintWriter;
import java.sql.Driver;
import java.sql.SQLException;
of the GNU General Public License as published by
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
package fr.ms.sql;

import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public interface JdbcDriverManager {

	void setLogWriter(PrintWriter out);

	Enumeration getDrivers();

	void registerDriver(Driver driver) throws SQLException;
}
