package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch(db) {
		case "mysql":
			//return mysql connectin function
			return new MySQLConnection();
		case "mangodb":
			//return new mangodb coonection function
			//return new MongoDBConnection;
			
		//wrong message control	
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}

	}
	
	public static DBConnection getConnectin() {
		return getConnection(DEFAULT_DB);
	}
}
