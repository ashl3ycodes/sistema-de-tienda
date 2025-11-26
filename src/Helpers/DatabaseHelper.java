package Helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
	private static final String URL = "jdbc:sqlite:database.db";

	public static Connection connect() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL);
			Statement cs = connection.createStatement();
			cs.execute("PRAGMA foreign_keys = ON;");
		} catch(SQLException e) {
			System.err.println("Error crítico de conexión: " + e.getMessage());
			LoggingHelper.log("ERROR", "Error de conexión a la DB: " + e.getMessage());
		}
		return connection;
	}

	public static void initializeDB() {
		String sqlProductsString = "CREATE TABLE IF NOT EXISTS products (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"name TEXT NOT NULL," +
			"price REAL NOT NULL CHECK(price >= 0)," +
			"stock INTEGER NOT NULL CHECK(stock >= 0)," +
			"provider TEXT" +
			");";

		String sqlSalesString = "CREATE TABLE IF NOT EXISTS sales (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"date TEXT NOT NULL," +
			"total REAL NOT NULL" +
			");";

		String sqlSaleDetailsString = "CREATE TABLE IF NOT EXISTS sale_details (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"sale_id INTEGER," +
			"product_id INTEGER," +
			"quantity INTEGER NOT NULL," +
			"subtotal REAL NOT NULL," +
			"FOREIGN KEY(sale_id) REFERENCES sales(id)," +
			"FOREIGN KEY(product_id) REFERENCES products(id)" +
			");";

		String sqlUsersString = "CREATE TABLE IF NOT EXISTS users (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"username TEXT NOT NULL UNIQUE," +
			"password TEXT NOT NULL," +
			"role TEXT NOT NULL CHECK(role IN ('ADMIN', 'VENDOR'))" +
			");";

		try(Connection connection = connect(); Statement stmt = connection.createStatement()) {
			stmt.execute(sqlProductsString);
			stmt.execute(sqlSalesString);
			stmt.execute(sqlSaleDetailsString);
			stmt.execute(sqlUsersString);
			createDefaultAdmin(connection);
		} catch(SQLException e) {
			System.out.println("Error inicializando DB: " + e.getMessage());
		}
	}

	private static void createDefaultAdmin(Connection conn) throws SQLException {
		String sqlUserCountString = "SELECT count(*) FROM users";

		try(Statement cs = conn.createStatement();
			ResultSet rs = cs.executeQuery(sqlUserCountString)) {

			if(rs.next() && rs.getInt(1) == 0) {
				System.out.println("Creando cuenta de administrador...");
				String sqlCreateAdminString = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";

				try(PreparedStatement pstmt = conn.prepareStatement(sqlCreateAdminString)) {
					pstmt.setString(1, "admin");
					pstmt.setString(2, SecurityHelper.hashPassword("admin"));
					pstmt.setString(3, "ADMIN");
					pstmt.executeUpdate();
					System.out.println("Cuenta de administrador creada. (Usuario: admin / Contraseña: admin)");
					LoggingHelper.log("INFO", "Cuenta de administrador creada por defecto.");
				}
			}
		}
	}
}