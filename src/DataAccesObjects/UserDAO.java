package DataAccesObjects;

import Models.UserModel;
import Helpers.LoggingHelper;
import Helpers.DatabaseHelper;
import Helpers.SecurityHelper;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

	public UserModel login(String username, String password) {
		String sqlLoginString = "SELECT role FROM users WHERE username = ? AND password = ?";
		String hashedPassword = SecurityHelper.hashPassword(password);

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlLoginString)) {
			ps.setString(1, username);
			ps.setString(2, hashedPassword);
			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				return new UserModel(username, rs.getString("role"));
			}
		} catch(SQLException e) {
			System.out.println("Error Login: " + e.getMessage());
		}
		return null;
	}

	public UserModel getVendedorUser() {
		String sqlVendorString = "SELECT username, role FROM users WHERE role = 'VENDOR' LIMIT 1";

		try(Connection connection = DatabaseHelper.connect();
			Statement cs = connection.createStatement();
			ResultSet rs = cs.executeQuery(sqlVendorString)) {
			if(rs.next()) {
				return new UserModel(rs.getString("username"), rs.getString("role"));
			}
		} catch(SQLException e) {System.out.println(e.getMessage());}
		return null;
	}

	public boolean createVendedor(String username, String rawPassword) {
		String sqlVendorString = "INSERT INTO users(username, password, role) VALUES(?, ?, 'VENDOR')";

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlVendorString)) {
			ps.setString(1, username);
			ps.setString(2, SecurityHelper.hashPassword(rawPassword));
			ps.executeUpdate();
			LoggingHelper.log("ADMIN", "Nuevo vendedor creado: " + username);
			return true;
		} catch(SQLException e) {
			if(e.getMessage().contains("UNIQUE")) {
				System.out.println("❌ El usuario ya existe.");
			} else {
				System.out.println("Error al crear: " + e.getMessage());
			}
			return false;
		}
	}

	public boolean updateUsername(String currentName, String newName) {
		String sqlUpdateString = "UPDATE users SET username = ? WHERE username = ?";

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlUpdateString)) {
			ps.setString(1, newName);
			ps.setString(2, currentName);
			ps.executeUpdate();
			LoggingHelper.log("ADMIN", "Cambio de usuario: " + currentName + " -> " + newName);
			return true;
		} catch(SQLException e) {
			System.out.println("❌ Error (probablemente el nombre ya existe).");
			return false;
		}
	}

	public boolean updatePassword(String username, String newRawPass) {
		String sqlUpdateString = "UPDATE users SET password = ? WHERE username = ?";
		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlUpdateString)) {
			ps.setString(1, SecurityHelper.hashPassword(newRawPass));
			ps.setString(2, username);
			ps.executeUpdate();
			LoggingHelper.log("ADMIN", "Contraseña cambiada para: " + username);
			return true;
		} catch(SQLException e) {
			System.out.println("Error cambiando pass: " + e.getMessage());
			return false;
		}
	}
}