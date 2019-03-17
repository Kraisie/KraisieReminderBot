package Data;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Reminder {

	private LocalDateTime creation;
//	private LocalDateTime remindDate;
	private String message;

	public Reminder(LocalDateTime creation, String message) {
		this.creation = creation;
		this.message = message;
	}

	public static List<Reminder> readData(Path filePath) {
		String json;

		try {
			json = new String(Files.readAllBytes(filePath));
		} catch (IOException e) {
			return new ArrayList<>();
		}

		Gson gson = new Gson();
		Reminder[] entries = gson.fromJson(json, Reminder[].class);

		return new ArrayList<>(Arrays.asList(entries));
	}

	public static void writeData(List<Reminder> allEntries, Path filePath) {
		Gson gson = new Gson();
		String json = gson.toJson(allEntries);

		try {
			Files.write(filePath, json.getBytes(), TRUNCATE_EXISTING, CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LocalDateTime getCreation() {
		return creation;
	}

	public void setCreation(LocalDateTime creation) {
		this.creation = creation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Reminder reminder = (Reminder) o;
		// add remindDate if added l8er
		return message.equals(reminder.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message);
	}
}
