package DataAccesObjects;

import Models.ProductModel;
import Helpers.LoggingHelper;
import Helpers.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

	public void addProduct(String name, double price, int stock, String provider) {
		String sqlCreateString = "INSERT INTO products(name, price, stock, provider) VALUES(?,?,?,?)";

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlCreateString)) {
			ps.setString(1, name);
			ps.setDouble(2, price);
			ps.setInt(3, stock);
			ps.setString(4, provider);
			ps.executeUpdate();
			System.out.println("Producto registrado exitosamente.");
			LoggingHelper.log("INFO", "Producto creado: " + name);
		} catch(SQLException e) {
			System.out.println("Error al guardar: " + e.getMessage());
			LoggingHelper.log("ERROR", "Error al crear producto: " + e.getMessage());
		}
	}

	public void editProduct(ProductModel product) {
		ProductModel oldProduct = getProductById(product.getId());
		String sqlUpdateString = "UPDATE products SET name = ?, price = ?, stock = ?, provider = ? WHERE id = ?";
		
		try (Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlUpdateString)) {
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPrice());
			ps.setInt(3, product.getStock());
			ps.setString(4, product.getProvider());
			ps.setInt(5, product.getId());
			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Producto actualizado correctamente.");
				StringBuilder changes = new StringBuilder();

				if (oldProduct != null) {
					if (!oldProduct.getName().equals(product.getName())) {
						changes.append(String.format("  Nombre: %s -> %s%n", oldProduct.getName(), product.getName()));
					}
					if (oldProduct.getPrice() != product.getPrice()) {
						changes.append(String.format("  Precio: $%.2f -> $%.2f%n", oldProduct.getPrice(), product.getPrice()));
					}
					if (oldProduct.getStock() != product.getStock()) {
						changes.append(String.format("  Stock: %d -> %d%n", oldProduct.getStock(), product.getStock()));
					}
					if (!oldProduct.getProvider().equals(product.getProvider())) {
						changes.append(String.format("  Proveedor: %s -> %s%n", oldProduct.getProvider(), product.getProvider()));
					}
				}
				LoggingHelper.log("INFO", "Producto " + product.getId() + " actualizado. Cambios: " + changes.toString().replace("\n", " | "));
			}

		} catch (SQLException e) {
			System.out.println("Error al actualizar: " + e.getMessage());
			LoggingHelper.log("ERROR", "Error al actualizar el producto " + product.getId() + ": " + e.getMessage());
		}
	}

	public boolean deleteProduct(int id) {
		String sqlDeleteString = "DELETE FROM products WHERE id = ?";

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sqlDeleteString)) {
			ps.setInt(1, id);
			int affectedRows = ps.executeUpdate();

			if(affectedRows > 0) {
				System.out.println("Producto eliminado exitosamente.");
				LoggingHelper.log("INFO", "Producto eliminado ID: " + id);
				return true;
			} else {
				System.out.println("No se encontro el producto.");
				return false;
			}
		} catch(SQLException e) {
			if(e.getErrorCode() == 19) {
				System.out.println("No puedes eliminar este producto porque tiene ventas registradas.");
			} else {
				System.out.println("Error al eliminar: " + e.getMessage());
			}
			LoggingHelper.log("ERROR", "Error al eliminar el producto " + id + ": " + e.getMessage());
			return false;
		}
	}

	public List<ProductModel> getAllProducts() {
		List<ProductModel> list = new ArrayList<>();
		String sql = "SELECT * FROM products";

		try(Connection connection = DatabaseHelper.connect();
			Statement cs = connection.createStatement();
			ResultSet rs = cs.executeQuery(sql)) {

			while(rs.next()) {
				list.add(new ProductModel(
					rs.getInt("id"),
					rs.getString("name"),
					rs.getDouble("price"),
					rs.getInt("stock"),
					rs.getString("provider")
				));
			}
		} catch(SQLException e) {
			System.out.println("Error al consultar: " + e.getMessage());
		}
		return list;
	}

	public ProductModel getProductById(int id) {
		String sql = "SELECT * FROM products WHERE id = ?";

		try(Connection connection = DatabaseHelper.connect();
			PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				return new ProductModel(
					rs.getInt("id"), rs.getString("name"),
					rs.getDouble("price"), rs.getInt("stock"),
					rs.getString("provider")
				);
			}
		} catch(SQLException e) {
			System.out.println("Error buscando ID: " + e.getMessage());
		}
		return null;
	}
}