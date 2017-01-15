package fr.ms.log4jdbc.h2.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import fr.ms.log4jdbc.h2.DatabaseUtil;
import fr.ms.log4jdbc.sqloperation.SqlMessage;

public class PerfomanceTest {

	private final SqlMessage messages = SqlMessage.getInstance();

	private final static int Min = 1;

	private final static int Max = 1000;

	@After
	public void clear() {
		messages.clear();
	}

	@Ignore
	@Test
	public void heapDumpSingleTest() throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DatabaseUtil.createConnection(true);
			DatabaseUtil.createDatabase(connection);

			connection.setAutoCommit(false);

			statement = connection
					.prepareStatement("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES (?, ?, ?)");

			for (int i = 0; i < 100; i++) {
				int nombreAleatoire = Min + (int) (Math.random() * ((Max - Min) + 1));
				statement.setString(1, "Prenom " + nombreAleatoire);
				nombreAleatoire = Min + (int) (Math.random() * ((Max - Min) + 1));
				statement.setString(2, "Nom " + nombreAleatoire);
				statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));

				statement.addBatch();
			}
			statement.executeBatch();

			connection.commit();

			HeapDump.dumpHeap("c:/dev/heap.log", true);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Ignore
	@Test
	public void heapDumpTest() throws SQLException, InterruptedException, ExecutionException {
		Connection connection = null;
		Statement statement = null;
		try {
			ExecutorService executor = Executors.newFixedThreadPool(4);

			connection = DatabaseUtil.createConnection(true);
			DatabaseUtil.createDatabase(connection);

			List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

			for (int i = 0; i < 100; i++) {
				Callable<Boolean> task = new Traitement();
				Future<Boolean> future = executor.submit(task);
				futures.add(future);
			}

			for (Future<Boolean> f : futures) {
				Boolean ok = f.get();
				if (!ok) {
					System.out.println("erreur");
					return;
				}
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	private class Traitement implements Callable<Boolean> {

		public Boolean call() throws Exception {

			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = DatabaseUtil.createConnection(true);

				connection.setAutoCommit(false);

				statement = connection
						.prepareStatement("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES (?, ?, ?)");

				for (int i = 0; i < 100000; i++) {
					int nombreAleatoire = Min + (int) (Math.random() * ((Max - Min) + 1));
					statement.setString(1, "Prenom " + nombreAleatoire);
					nombreAleatoire = Min + (int) (Math.random() * ((Max - Min) + 1));
					statement.setString(2, "Nom " + nombreAleatoire);
					statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));

					statement.addBatch();
				}
				statement.executeBatch();

				connection.commit();
				return true;
			} finally {

				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			}
		}
	}
}
