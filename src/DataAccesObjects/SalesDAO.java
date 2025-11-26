package DataAccesObjects;

import Models.SaleModel;
import Helpers.LoggingHelper;
import Helpers.DatabaseHelper;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesDAO {

	public boolean registerSale(List<SaleModel> items, double total) {
		if(items.isEmpty()) {
			return false;
		}
		String sqlSaleString = "INSERT INTO sales(date, total) VALUES(?, ?)";
		String sqlSaleDetailsString = "INSERT INTO sale_details(sale_id, product_id, quantity, subtotal) VALUES(?,?,?,?)";
		String sqlUpdateStockString = "UPDATE products SET stock = stock - ? WHERE id = ?";
		Connection connection = null;

		try {
			connection = DatabaseHelper.connect();
			connection.setAutoCommit(false);
			String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			PreparedStatement psSale = connection.prepareStatement(sqlSaleString, Statement.RETURN_GENERATED_KEYS);
			psSale.setString(1, date);
			psSale.setDouble(2, total);
			psSale.executeUpdate();
			ResultSet rs = psSale.getGeneratedKeys();
			int saleId;

			if(rs.next()) {
				saleId = rs.getInt(1);
			} else {
				throw new SQLException("Error al crear la venta, no se obtuvo una ID de producto.");
			}
			PreparedStatement psDetails = connection.prepareStatement(sqlSaleDetailsString);
			PreparedStatement psStock = connection.prepareStatement(sqlUpdateStockString);

			for(SaleModel item: items) {
				psDetails.setInt(1, saleId);
				psDetails.setInt(2, item.getProductId());
				psDetails.setInt(3, item.getQuantity());
				psDetails.setDouble(4, item.getSubtotal());
				psDetails.addBatch();
				psStock.setInt(1, item.getQuantity());
				psStock.setInt(2, item.getProductId());
				psStock.addBatch();
			}
			psDetails.executeBatch();
			psStock.executeBatch();
			connection.commit();
			LoggingHelper.log("INFO", "Venta registrada ID: " + saleId + " Total: $" + total);
			return true;
		} catch(SQLException e) {
			System.out.println("Error al realizar la venta. Revirtiendo cambios: " + e.getMessage());
			LoggingHelper.log("ERROR", "Error al realizar la venta: " + e.getMessage());
			try {
				connection.rollback();
			} catch(SQLException ex) {
				System.out.println("Error al realizar la venta: " + ex.getMessage());
			}
			return false;
		} finally {
			if(connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch(SQLException e) {
					System.out.println("Error cerrando conexión");
				}
			}
		}
	}

	public void generateReport() {
		String sqlShowSalesString = "SELECT * FROM sales ORDER BY id DESC LIMIT 50";

		try(Connection connection = DatabaseHelper.connect();
			Statement cs = connection.createStatement();
			ResultSet rs = cs.executeQuery(sqlShowSalesString)) {
			double grandTotal = 0;

			while(rs.next()) {
				System.out.printf("ID: %d | Fecha: %s | Total: $%.2f%n", rs.getInt("id"), rs.getString("date"), rs.getDouble("total"));
				grandTotal += rs.getDouble("total");
			}
			System.out.printf("Total Generado (últimas 50): $%.2f%n", grandTotal);
		} catch(SQLException e) {
			System.out.println("Error generando reporte: " + e.getMessage());
		}
	}
}