package webServer;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserManager {
	private Connection conn;
	
	/**
	 * In this constructor a new mysql connection will be created
	 */
	public UserManager(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//System.out.println("Error: MySQL JDBC Driver not found!");
			e.printStackTrace();
			return;
		}
		conn = null;
				

		try {
			conn = DriverManager.getConnection(url, "root", "");
			//conn = DriverManager.getConnection(url, "root", "");

		} catch (SQLException e) {
			//System.out.println("Connection to database (" + url + ") failed!");
			e.printStackTrace();
			return;
		}

		if (conn == null) { 
			//System.out.println("Failed to conect to users database!");
		}		
	}
	
	public boolean check(String username, String password) {
		if (conn == null) {
			return false;
		}
		try {
			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM users WHERE username='"+username+"' AND password='"+password+"';");
			if (statement != null) {
				ResultSet result = statement.executeQuery();
				boolean checkResult = result.next(); 
				statement.close();
				result.close();
				
				return checkResult;	
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println("Failed to close connection to users database!");
			}
		}
	}
}
