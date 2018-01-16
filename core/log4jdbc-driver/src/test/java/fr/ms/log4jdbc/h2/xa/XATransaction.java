package fr.ms.log4jdbc.h2.xa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

import fr.ms.log4jdbc.XADataSource;
import fr.ms.log4jdbc.h2.DatabaseUtil;

public class XATransaction {

	@Test
	public void simpleResource() throws SQLException, XAException {
		final JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(DatabaseUtil.URL_H2);
		ds.setUser(DatabaseUtil.USER);
		ds.setPassword(DatabaseUtil.PASSWORD);

		final XAConnection xaConnection = ds.getXAConnection();

		Connection connection = null;

		try {
			connection = xaConnection.getConnection();
			DatabaseUtil.createDatabase(connection);

			final Statement statement = connection.createStatement();

			final Xid xid = new SimpleXid(100, new byte[] { 0x01 }, new byte[] { 0x02 });

			final XAResource xaResource = xaConnection.getXAResource();

			xaResource.start(xid, XAResource.TMNOFLAGS);

			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");

			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");

			statement.execute(
					"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

			xaResource.end(xid, XAResource.TMSUCCESS);

			final int prepare = xaResource.prepare(xid);
			if (prepare == XAResource.XA_OK) {
				xaResource.commit(xid, false);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Test
	public void resource2Test() throws SQLException, XAException {
		final JdbcDataSource real = new JdbcDataSource();
		final XADataSource ds = new XADataSource(real);
		real.setURL(DatabaseUtil.URL_H2);
		real.setUser(DatabaseUtil.USER);
		real.setPassword(DatabaseUtil.PASSWORD);

		final XAConnection xaCon1 = ds.getXAConnection();
		final XAResource xaRes1 = xaCon1.getXAResource();
		
		Connection con1 = null;
		Connection con2 = null;

		try {
		
		
		  con1 = xaCon1.getConnection();
		DatabaseUtil.createDatabase(con1);
		final Statement stmt1 = con1.createStatement();

		final Xid xid1 = new SimpleXid(100, new byte[] { 0x01 }, new byte[] { 0x02 });
		xaRes1.start(xid1, XAResource.TMNOFLAGS);
		stmt1.executeUpdate(
				"INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Marco', 'Semiao', '1970-01-01');");
		stmt1.executeUpdate("INSERT INTO ETABLISSEMENT (NOM) VALUES ('Blanqui');");
		xaRes1.end(xid1, XAResource.TMSUCCESS);

		final XAConnection xaCon2 = ds.getXAConnection();
		final XAResource xaRes2 = xaCon1.getXAResource();
		  con2 = xaCon2.getConnection();
		final Statement stmt2 = con2.createStatement();

		if (xaRes2.isSameRM(xaRes1)) {
			xaRes2.start(xid1, XAResource.TMJOIN);
			stmt2.executeUpdate("INSERT INTO PERSONNE_ETABLISSEMENT (ID_PERSONNE, ID_ETABLISSEMENT) VALUES (1,1);");
			xaRes2.end(xid1, XAResource.TMSUCCESS);
		} else {
			final Xid xid2 = new SimpleXid(100, new byte[] { 0x01 }, new byte[] { 0x03 });
			xaRes2.start(xid2, XAResource.TMNOFLAGS);
			stmt2.executeUpdate("INSERT INTO PERSONNE_ETABLISSEMENT (ID_PERSONNE, ID_ETABLISSEMENT) VALUES (1,1);");
			xaRes2.end(xid2, XAResource.TMSUCCESS);
			final int ret = xaRes2.prepare(xid2);
			if (ret == XAResource.XA_OK) {
				xaRes2.commit(xid2, false);
			}
		}

		final int ret = xaRes1.prepare(xid1);
		if (ret == XAResource.XA_OK) {
			xaRes1.commit(xid1, false);
		}
		} finally {
			if (con1 != null) {
				con1.close();
			}
			if (con2 != null) {
				con2.close();
			}
		}
	}
}
