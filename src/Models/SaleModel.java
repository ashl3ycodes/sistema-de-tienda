package Models;

public class SaleModel {
	private final int productId;
	private final int quantity;
	private final double subtotal;

	public SaleModel(int productId, int quantity, double subtotal) {
		this.productId = productId;
		this.quantity = quantity;
		this.subtotal = subtotal;
	}

	public int getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getSubtotal() {
		return subtotal;
	}
}