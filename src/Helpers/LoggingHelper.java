package Helpers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingHelper {
	private static final String FILE_PATH = "logs.txt";

	public static void log(String level, String message) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			writer.write(String.format("[%s] [%s] %s", timestamp, level, message));
			writer.newLine();

		} catch(IOException e) {
			System.err.println("Error escribiendo en los registros: " + e.getMessage());
		}
	}
}