package db;

public class DBConnectionFactory {
	// This is used to create different database instances
	private static final String DEFAULT_DB = "mysql";
	private static final String MYSQL = "mysql";
	private static final String MONGODB = "mongodb";
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case MYSQL:
			// return new MySQLConnection();
			return null;
		case MONGODB:
			// Support for MongoDB will be implemented in the future
			// return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid database specifier: " + db);
		}
	}

	public static DBConnection getConnection() {
		// Create a default (MySQL) database instance for now
		return getConnection(DEFAULT_DB);
	}
}
