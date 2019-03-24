import Data.Reminder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KraisieReminderBot extends TelegramLongPollingBot {

	/*
	 *	SMILEYS UNICODE
	 *
	 * 	U+2049	= red ?!
	 * 	U+2139	= information
	 * 	U+23F0 	= alarm clock
	 * 	U+26A0	= warning
	 *	U+2705 	= green check mark
	 * 	U+2709	= memo
	 *  U+270F  = pencil
	 * 	U+2753	= red ?
	 * 	U+27A1 	= right_arrow
	 */

	private String token;
	private String name;

	@Override
	public String getBotToken() {
		if (token == null || token.length() == 0) {
			readBotParameters();
		}

		return token;
	}

	@Override
	public String getBotUsername() {
		if (name == null || name.length() == 0) {
			readBotParameters();
		}

		return name;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.getEditedMessage() != null) {
			// edits are not supported
			return;
		}

		String message = update.getMessage().getText().toLowerCase();
		String command = message.split(" ")[0];

		switch(command) {
			case "/help":
				showHelp(update);
				break;
			case "/reminder":
				setNewReminder(update);
				break;
			case "/edit":
				editReminder(update);
				break;
			case "/list":
				listAllReminders(update);
				break;
			case "/search":
				searchReminder(update);
				break;
			case "/delete":
				deleteReminder(update);
				break;
			case "/yesorno":
				yesOrNo(update);
				break;
		}
	}

	private void readBotParameters() {
		try {
			File tokenFile = new File("Bot.txt");
			BufferedReader reader = new BufferedReader(new FileReader(tokenFile));

			token = reader.readLine();
			name = reader.readLine();
			reader.close();
		} catch (IOException e) {
			System.out.println("Could not read token file:");
			e.printStackTrace();
		}
	}

	private void showHelp(Update update) {
		SendMessage answer = new SendMessage();
		answer.setChatId(update.getMessage().getChatId());
		String response = "\u2139 */help* - Show the help message (which you found already)\n" +
				"\u23F0 */reminder <message>* - Create a new reminder\n" +
				"\u2709 */list* - List all saved reminders\n" +
				"\u270F */edit <number> <message>* - Replace the text of the reminder with that number on the list with a new text\n" +
				"\u26A0 */delete <number>* - Delete the reminder with that number on the list\n" +
				"\u2049 */yesorno* - Get a random yes or no";

		sendAnswer(update.getMessage().getChatId(), response, true);
	}

	/*
	 * 	the reminder file only got the chat id as name option as there might be groups that organize
	 * 	their reminders/deadlines/whatever as teams. chatID_senderID.json would prevent that.
	 */

	private void setNewReminder(Update update) {
		updateReminder(update);

		String response = "Hey " + update.getMessage().getFrom().getUserName() + ", I've saved your reminder.";
		sendAnswer(update.getMessage().getChatId(), response, false);
	}

	private void updateReminder(Update update) {
		Path reminderFile = Paths.get(
				"reminder_data/"
				+ update.getMessage().getChatId()
				+ ".json"
		);
		String message = update.getMessage().getText().replaceFirst("/reminder ", "");

		List<Reminder> allReminder = Reminder.readData(reminderFile);
		Reminder reminder = new Reminder(LocalDateTime.now(), message);
		allReminder.add(reminder);
		Reminder.writeData(allReminder, reminderFile);
	}

	private void editReminder(Update update) {
		long chatID = update.getMessage().getChatId();
		List<Reminder> allReminder = getReminders(update);
		if (allReminder == null) {
			return;
		}

		int index;
		String message = update.getMessage().getText().replaceFirst("/edit ", "");
		try {
			index = Integer.valueOf(message.split(" ")[0]);
			message = message.replaceFirst(index + " ", "");
		} catch (NumberFormatException e) {
			String response = "*Invalid number!* Please select a number between 1 and "
					+ allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		allReminder.get(index - 1).setMessage(message);
		Reminder.writeData(allReminder, Paths.get("reminder_data/" + update.getMessage().getChatId() + ".json"));
		sendAnswer(chatID, "*Reminder edited!*", true);
	}

	private void listAllReminders(Update update) {
		List<Reminder> allReminder = getReminders(update);
		if (allReminder == null) {
			return;
		}

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
		StringBuilder strBuilder = new StringBuilder("Here is a list of all your reminders:\n\n");
		for (Reminder reminder : allReminder) {
			strBuilder.append("*[").append(allReminder.indexOf(reminder) + 1).append(" - ")
					.append(reminder.getCreation().format(dateFormat)).append("]*\n")
					.append("_").append(reminder.getMessage()).append("_\n\n");
		}

		sendAnswer(update.getMessage().getChatId(), strBuilder.toString(), true);
	}

	private void searchReminder(Update update) {
		List<Reminder> allReminder = getReminders(update);
		if (allReminder == null) {
			return;
		}

		String searchText = update.getMessage().getText().replaceAll("/search ", "").toLowerCase();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
		StringBuilder strBuilder = new StringBuilder("Found these matching reminders:\n\n");
		for (Reminder reminder : allReminder) {
			if (!reminder.getMessage().toLowerCase().contains(searchText)) {
				continue;
			}

			strBuilder.append("*[").append(allReminder.indexOf(reminder) + 1).append(" - ")
					.append(reminder.getCreation().format(dateFormat)).append("]*\n")
					.append("_").append(reminder.getMessage()).append("_\n\n");
		}

		sendAnswer(update.getMessage().getChatId(), strBuilder.toString(), true);
	}

	private void deleteReminder(Update update) {
		List<Reminder> allReminder = getReminders(update);
		if (allReminder == null) {
			return;
		}

		int index;
		long chatID = update.getMessage().getChatId();
		String message = update.getMessage().getText().replaceFirst("\\s", "");
		try {
			index = Integer.valueOf(message.replace("/delete", "")) - 1;
		} catch (NumberFormatException e) {
			String response = "*Invalid number!* Please select a number between 1 and "
					+ allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		if (index < 0 || index >= allReminder.size()) {
			String response = "*Number not in range!* Please select a number between 1 and "
					+ allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		allReminder.remove(index);
		Reminder.writeData(allReminder, Paths.get("reminder_data/" + update.getMessage().getChatId() + ".json"));
		sendAnswer(chatID, "*Reminder removed!*", true);
	}

	private List<Reminder> getReminders(Update update) {
		Path reminderFile = Paths.get("reminder_data/" + update.getMessage().getChatId() + ".json");
		List<Reminder> allReminder = Reminder.readData(reminderFile);

		if (allReminder.isEmpty()) {
			sendAnswer(update.getMessage().getChatId(), "*You do not have any reminders set yet!*", true);
			return null;
		}

		return allReminder;
	}

	private void yesOrNo(Update update) {
		SendMessage answer = new SendMessage();
		answer.setChatId(update.getMessage().getChatId());
		double rdm = Math.random();
		long chatID = update.getMessage().getChatId();

		if (rdm <= 0.5) {
			String response = update.getMessage().getFrom().getUserName() + ", my answer is: YES!";
			sendAnswer(chatID, response, false);
			return;
		}

		String response = update.getMessage().getFrom().getUserName() + " today you get a NO!";
		sendAnswer(chatID, response, false);
	}

	private void sendAnswer(long chatID, String message, boolean markdown) {
		try {
			SendMessage answer = new SendMessage();
			answer.setChatId(chatID);
			answer.setText(message);
			answer.enableMarkdown(markdown);
			execute(answer);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
