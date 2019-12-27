import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Main {

	public static void main(String[] args) {
		if (!new File("Bot.txt").exists()) {
			setNewBotData();
		}

		initBot();
	}

	private static void setNewBotData() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Bot Configuration not found! Please add that file or insert the needed data: ");
			System.out.println("\nBot Token: ");
			String content = br.readLine();
			content += "\n";
			System.out.println("Bot Name: ");
			content += br.readLine();

			Files.write(Paths.get("Bot.txt"), content.getBytes(), TRUNCATE_EXISTING, CREATE);
			System.out.println("\nFile created! Starting the bot...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initBot() {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

		try {
			telegramBotsApi.registerBot(new KraisieReminderBot());
			System.out.println("Bot is up and running!");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
