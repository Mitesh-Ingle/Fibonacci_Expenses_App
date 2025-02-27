package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	private static final String URL = "jdbc:sqlite:expenses.db"; // Database file

	public static Connection getConnection() {
		try {
			Connection conn = DriverManager.getConnection(URL);
			createTable(conn); // Ensure table exists
			return conn;
		} catch (SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
			return null;
		}
		
	}

	private static void createTable(Connection conn) {
		String sql = "CREATE TABLE IF NOT EXISTS expenses (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "amount REAL NOT NULL, " + "category TEXT NOT NULL, " + "date TEXT NOT NULL, " + "description TEXT);";

		try (Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
			System.out.println("Table checked/created successfully.");
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}

	}

}