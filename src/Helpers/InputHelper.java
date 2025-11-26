package Helpers;

import java.util.Scanner;

public class InputHelper {
	private static final Scanner scanner = new Scanner(System.in);

	public static String readString(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine().trim();
	}

	public static double readPositiveDouble(String prompt) {
		while(true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();

			try {
				double value = Double.parseDouble(input);

				if(value < 0) {
					System.out.println("El precio no puede ser negativo. Intente de nuevo.");
					continue;
				}
				return value;
			} catch(NumberFormatException e) {
				System.out.println("Entrada inválida. Ingrese un monto válido (ej. 10.50).");
			}
		}
	}

	public static int readInt(String prompt) {
		while(true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();

			try {
				return Integer.parseInt(input);
			} catch(NumberFormatException e) {
				System.out.println("Entrada inválida. Ingrese un número entero.");
			}
		}
	}

	public static int readPositiveInt(String prompt) {
		while(true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();

			try {
				int value = Integer.parseInt(input);

				if(value < 0) {
					System.out.println("La cantidad no puede ser negativa. Intente de nuevo.");
					continue;
				}
				return value;
			} catch(NumberFormatException e) {
				System.out.println("Entrada inválida. Ingrese un número entero positivo.");
			}
		}
	}
}