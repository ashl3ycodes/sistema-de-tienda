import DataAccesObjects.ProductDAO;
import DataAccesObjects.SalesDAO;
import DataAccesObjects.UserDAO;
import Models.ProductModel;
import Models.SaleModel;
import Models.UserModel;
import Helpers.DatabaseHelper;
import Helpers.InputHelper;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		DatabaseHelper.initializeDB();
		ProductDAO productDAO = new ProductDAO();
		SalesDAO salesDAO = new SalesDAO();
		UserDAO userDAO = new UserDAO();

		while(true) {
			UserModel currentUser = null;

			while(currentUser == null) {
				System.out.println("\nSistema de tienda - Inicio de sesion");
				System.out.println("(Escribe 'SALIR' en usuario para cerrar el programa)");
				String user = InputHelper.readString("Usuario: ");

				if(user.equalsIgnoreCase("SALIR")) {
					System.out.println("Cerrando sistema... Hasta luego!");
					System.exit(0);
				}
				String pass = InputHelper.readString("Contraseña: ");
				currentUser = userDAO.login(user, pass);

				if(currentUser == null) {
					System.out.println("Inicio de sesion fallido. Intente de nuevo.");
				} else {
					System.out.println("Bienvenido, " + currentUser.getUsername());
				}
			}
			boolean sessionActive = true;

			while(sessionActive) {
				System.out.println("\n--- MENÚ PRINCIPAL ---");
				System.out.println("1. Nueva venta");
				System.out.println("2. Ver inventario");
				if(currentUser.isAdmin()) {
					System.out.println("3. Registrar producto");
					System.out.println("4. Editar producto");
					System.out.println("5. Reporte de Ventas");
					System.out.println("6. Gestion de usuarios");
				}
				System.out.println("7. Cerrar sesion");
				System.out.println("0. Salir del programa");
				int option = InputHelper.readInt("Seleccione una opcion: ");

				switch(option) {
					case 1: {
						double totalSale = 0;
						List<SaleModel> cart = new ArrayList<>();

						while(true) {
							int prodId = InputHelper.readInt("ID del producto (Ingrese 0 para terminar): ");
							if(prodId == 0) {
								break;
							}
							ProductModel p = productDAO.getProductById(prodId);

							if(p == null) {
								System.out.println("Producto no encontrado. Intente de nuevo.");
								continue;
							}
							System.out.println(p);
							int qty = InputHelper.readPositiveInt("Cantidad: ");

							if(qty > p.getStock()) {
								System.out.println("Stock insuficiente. Intente de nuevo.");
							} else if(qty > 0) {
								double subtotal = qty * p.getPrice();
								cart.add(new SaleModel(p.getId(), qty, subtotal));
								totalSale += subtotal;
								System.out.printf("Producto agregado. Subtotal: $%.2f%n", totalSale);
							}
						}

						if(!cart.isEmpty()) {
							System.out.printf("Total a Pagar: $%.2f%n", totalSale);

							if(InputHelper.readString("¿Confirmar? (s/n): ").equalsIgnoreCase("s")) {
								if(salesDAO.registerSale(cart, totalSale)) {
									System.out.println("Venta realizada con exito.");
								}
							}
						}
						break;
					}
					case 2: {
						List<ProductModel> products = productDAO.getAllProducts();

						if(products.isEmpty()) {
							System.out.println("\nEl inventario esta vacio.");
						} else {
							System.out.println("\nInventario:");
							products.forEach(System.out::println);
						}
						break;
					}
					case 3: {
						if(!currentUser.isAdmin()) {
							System.out.println("Opcion no valida. Intente de nuevo.");
							break;
						}
						String name = InputHelper.readString("\nNombre del producto: ");

						if(name.isEmpty()) {
							break;
						}
						double price = InputHelper.readPositiveDouble("Precio del producto: ");
						int stock = InputHelper.readPositiveInt("Cantidad del producto: ");
						String provider = InputHelper.readString("Proveedor del producto: ");
						productDAO.addProduct(name, price, stock, provider);
						break;
					}
					case 4: {
						if(!currentUser.isAdmin()) {
							System.out.println("Opcion no valida. Intente de nuevo.");
							break;
						}
						int idEdit = InputHelper.readInt("\nIngrese ID del producto a editar: ");
						ProductModel pEdit = productDAO.getProductById(idEdit);

						if(pEdit != null) {
							boolean editing = true;
							while(editing) {
								System.out.println(pEdit);
								System.out.println("\n1. Editar nombre");
								System.out.println("2. Editar precio");
								System.out.println("3. Editar stock");
								System.out.println("4. Editar proveedor");
								System.out.println("5. Borrar producto");
								System.out.println("0. Volver al menú principal");
								int editOp = InputHelper.readInt("Seleccione opcion: ");

								switch(editOp) {
									case 1:
										String newName = InputHelper.readString("Nuevo Nombre: ");

										if(!newName.isEmpty()) {
											pEdit.setName(newName);
											productDAO.editProduct(pEdit);
										}
										break;
									case 2:
										double newPrice = InputHelper.readPositiveDouble("Nuevo Precio: ");
										pEdit.setPrice(newPrice);
										productDAO.editProduct(pEdit);
										break;
									case 3:
										int newStock = InputHelper.readPositiveInt("Nuevo Stock: ");
										pEdit.setStock(newStock);
										productDAO.editProduct(pEdit);
										break;
									case 4:
										String newProv = InputHelper.readString("Nuevo Proveedor: ");
										pEdit.setProvider(newProv);
										productDAO.editProduct(pEdit);
										break;
									case 5:
										System.out.println("Estas seguro que deseas borrar " + pEdit.getName() + "?");
										String confirm = InputHelper.readString("Escribe 'SI' para confirmar: ");

										if(confirm.equals("SI")) {
											if(productDAO.deleteProduct(pEdit.getId())) {
												editing = false;
											}
										} else {
											System.out.println("Operacion cancelada.");
										}
										break;
									case 0:
										editing = false;
										break;
									default:
										System.out.println("Opcion invalida. Intente de nuevo.");
								}
							}
						} else {
							System.out.println("Producto no encontrado.");
						}
						break;
					}
					case 5: {
						if(!currentUser.isAdmin()) {
							System.out.println("Opcion no valida. Intente de nuevo.");
							break;
						}
						salesDAO.generateReport();
						break;
					}
					case 6: {
						if(!currentUser.isAdmin()) {
							System.out.println("Opción no válida. Intente de nuevo.");
							break;
						}

						boolean managingUsers = true;

						while(managingUsers) {
							System.out.println("\n1. Cambiar el nombre de la cuenta de administrador (" + currentUser.getUsername() + ")");
							System.out.println("2. Cambiar la contraseña de la cuenta de administrador");
							UserModel vendedor = userDAO.getVendedorUser();

							if(vendedor == null) {
								System.out.println("3. Crear cuenta de vendedor");
							} else {
								System.out.println("3. Cambiar el nombre de la cuenta de vendedor (" + vendedor.getUsername() + ")");
								System.out.println("4. Cambiar la contraseña de la cuenta de vendedor");
							}
							System.out.println("0. Volver");
							int userOp = InputHelper.readInt("Opción: ");

							switch(userOp) {
								case 1: {
									String newAdminName = InputHelper.readString("Nuevo nombre para " + currentUser.getUsername() + ": ");

									if(!newAdminName.isEmpty()) {
										if(userDAO.updateUsername(currentUser.getUsername(), newAdminName)) {
											System.out.println("Nombre actualizado.");
											currentUser.setUsername(newAdminName);
										}
									}
									break;
								}
								case 2: {
									String newAdminPass = InputHelper.readString("Nueva contraseña para " + currentUser.getUsername() + ": ");

									if(!newAdminPass.isEmpty()) {
										if(userDAO.updatePassword(currentUser.getUsername(), newAdminPass)) {
											System.out.println("Contraseña actualizada.");
										}
									}
									break;
								}
								case 3: {
									if(vendedor == null) {
										String vUser = InputHelper.readString("Nombre de la cuenta de vendedor: ");
										String vPass = InputHelper.readString("Contraseña de la cuenta de vendedor: ");

										if(userDAO.createVendedor(vUser, vPass)) {
											System.out.println("Cuenta de vendedor creada exitosamente.");
										}
									} else {
										String newVUser = InputHelper.readString("Nuevo nombre para " + vendedor.getUsername() + ": ");

										if(!newVUser.isEmpty()) {
											if(userDAO.updateUsername(vendedor.getUsername(), newVUser)) {
												System.out.println("Nombre actualizado.");
											}
										}
									}
									break;
								}
								case 4: {
									if(vendedor != null) {
										String newVPass = InputHelper.readString("Nueva contraseña para " + vendedor.getUsername() + ": ");

										if(!newVPass.isEmpty()) {
											if(userDAO.updatePassword(vendedor.getUsername(), newVPass)) {
												System.out.println("Contraseña actualizada.");
											}
										}
									} else {
										System.out.println("Opción no válida.");
									}
									break;
								}
								case 0: {
									managingUsers = false;
									break;
								}
								default: {
									System.out.println("Opción inválida. Intente de nuevo.");
								}
							}
						}
						break;
					}
					case 7: {
						System.out.println("Cerrando sesion...");
						sessionActive = false;
						break;
					}
					case 0: {
						System.out.println("Cerrando sistema...");
						System.exit(0);
						break;
					}
					default: {
						System.out.println("Opcion no valida. Intente de nuevo.");
					}
				}
			}
		}
	}
}