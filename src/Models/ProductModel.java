package Models;

public class ProductModel {
	private final int id;
	private String name;
	private double price;
	private int stock;
	private String provider;

	public ProductModel(int id, String name, double price, int stock, String provider) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.provider = provider;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public String getProvider() {
		return provider;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return String.format("| ID: %-4d | %-20s | $%-8.2f | Stock: %-4d | Proveedor: %s |", id, name, price, stock, provider);
	}
}