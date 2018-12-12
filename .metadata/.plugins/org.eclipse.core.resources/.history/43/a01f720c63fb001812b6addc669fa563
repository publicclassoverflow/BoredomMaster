package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLTableCreation {
	// Test MySQL and JDBC first
	public static void main(String[] args) {
		try {
			// Connect to MySQL
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
			if (conn == null) {
				return;
			}
			
			System.out.println("Import successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
