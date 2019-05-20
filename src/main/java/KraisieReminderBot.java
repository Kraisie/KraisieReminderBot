import Data.Message;
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
	 *  U+2795  = plus sign
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
			// edited messages are not supported
			return;
		}

		long chatID = update.getMessage().getChatId();
		Message message = tokenizeMessage(update);
		switch (message.getCommand()) {
			case "/help":
				showHelp(chatID);
				break;
			case "/reminder":
				setNewReminder(chatID, message.getMessage());
				break;
			case "/edit":
				editReminder(chatID, message.getIndex(), message.getMessage());
				break;
			case "/add":
				addReminder(chatID, message.getIndex(), message.getMessage());
				break;
			case "/list":
				listAllReminders(chatID);
				break;
			case "/search":
				searchReminder(chatID, message.getMessage());
				break;
			case "/delete":
				deleteReminder(chatID, message.getIndex());
				break;
			case "/yesorno":
				yesOrNo(chatID);
				break;
			default:
				unknownCommand(chatID);
		}
	}

	private Message tokenizeMessage(Update update) {
		Message message = new Message();
		String text = update.getMessage().getText();
		String[] commands = {"/help", "/reminder", "/edit", "/add", "/list", "/search", "/delete", "/yesorno"};

		// get command
		for (String command : commands) {
			if(!text.toLowerCase().startsWith(command.toLowerCase())) {
				continue;
			}
			message.setCommand(command);
			text = text.substring(command.length());
			break;
		}

		if (text.isEmpty()) {
			return message;
		}

		// remove all spaces before the index or message
		while(text.charAt(0) == ' ') {
			if (text.length() > 1) {
				text = text.substring(1);
				continue;
			}
			break;
		}

		// get index if given except when it is a new reminder/search as those may start with numbers which are not the index
		if (!message.getCommand().equals("/reminder") && !message.getCommand().equals("/search")) {
			StringBuilder sbIndex = new StringBuilder();
			while (Character.isDigit(text.charAt(0))) {
				sbIndex.append(text.charAt(0));

				if (text.length() > 1) {
					text = text.substring(1);
					continue;
				}
				break;
			}

			if (sbIndex.toString().length() != 0) {
				message.setIndex(Integer.valueOf(sbIndex.toString()));
			}
		}

		// remove all spaces before the message
		while(text.charAt(0) == ' ') {
			if (text.length() > 1) {
				text = text.substring(1);
				continue;
			}
			return message;
		}

		message.setMessage(text);
		return message;
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

	private void showHelp(long chatID) {
		String response = "\u2139 */help* - Show the help message (which you found already)\n" +
				"\u23F0 */reminder <message>* - Create a new reminder\n" +
				"\u2709 */list* - List all saved reminders\n" +
				"\u270F */edit <number> <message>* - Replace the text of the reminder with that number on the list with a new text\n" +
				"\u2795 */add <number> <message>* - Add text to the end of the reminder - start with //s to add a space at the beginning\n" +
				"\u26A0 */delete <number>* - Delete the reminder with that number on the list\n" +
				"\u2049 */yesorno* - Get a random yes or no";

		sendAnswer(chatID, response, true);
	}

	/*
	 * 	the reminder file only got the chat id as name option as there might be groups that organize
	 * 	their reminders/deadlines/whatever as teams. chatID_senderID.json would prevent that.
	 */
	private void setNewReminder(long chatID, String message) {
		updateReminder(chatID, message);
		sendAnswer(chatID, "*Reminder saved!*", true);
	}

	private void updateReminder(long chatID, String message) {
		Path reminderFile = Paths.get(
				"reminder_data/"
						+ chatID
						+ ".json"
		);

		List<Reminder> allReminder = Reminder.readData(reminderFile);
		Reminder reminder = new Reminder(LocalDateTime.now(), message);
		allReminder.add(reminder);
		Reminder.writeData(allReminder, reminderFile);
	}

	private void editReminder(long chatID, int index, String message) {
		List<Reminder> allReminder = getReminders(chatID);
		if (allReminder == null) {
			return;
		}

		if (index == -1 || index > allReminder.size()) {
			String response = "*Invalid number!* Please select a number between 1 and " + allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		allReminder.get(index - 1).setMessage(message);
		Reminder.writeData(allReminder, Paths.get("reminder_data/" + chatID + ".json"));
		sendAnswer(chatID, "*Reminder edited!*", true);
	}

	private void addReminder(long chatID, int index, String message) {
		List<Reminder> allReminder = getReminders(chatID);
		if (allReminder == null) {
			return;
		}

		if (index == -1 || index > allReminder.size()) {
			String response = "*Invalid number!* Please select a number between 1 and " + allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		String newMessage;
		if(message.startsWith("//s")) {
			newMessage = allReminder.get(index - 1).getMessage() + message.replaceFirst("//s", " ");
		} else {
			newMessage = allReminder.get(index - 1).getMessage() + message;
		}

		allReminder.get(index - 1).setMessage(newMessage);
		Reminder.writeData(allReminder, Paths.get("reminder_data/" + chatID + ".json"));
		sendAnswer(chatID, "*Added text to the reminder!*", true);
	}

	private void listAllReminders(long chatID) {
		List<Reminder> allReminder = getReminders(chatID);
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

		sendAnswer(chatID, strBuilder.toString(), true);
	}

	private void searchReminder(long chatID, String searchText) {
		List<Reminder> allReminder = getReminders(chatID);
		if (allReminder == null) {
			return;
		}

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
		StringBuilder strBuilder = new StringBuilder();
		boolean found = false;
		for (Reminder reminder : allReminder) {
			if (!reminder.getMessage().toLowerCase().contains(searchText)) {
				continue;
			}

			found = true;
			strBuilder.append("*[").append(allReminder.indexOf(reminder) + 1).append(" - ")
					.append(reminder.getCreation().format(dateFormat)).append("]*\n")
					.append("_").append(reminder.getMessage()).append("_\n\n");
		}

		if (found) {
			strBuilder.insert(0, "Found these matching reminders:\n\n");
		} else {
			strBuilder.append("*No matching reminder found!*");
		}

		sendAnswer(chatID, strBuilder.toString(), true);
	}

	private void deleteReminder(long chatID, int index) {
		List<Reminder> allReminder = getReminders(chatID);
		if (allReminder == null) {
			return;
		}

		if (index == -1 || index > allReminder.size()) {
			String response = "*Invalid number!* Please select a number between 1 and " + allReminder.size() + ".";
			sendAnswer(chatID, response, true);
			return;
		}

		allReminder.remove(index - 1);
		Reminder.writeData(allReminder, Paths.get("reminder_data/" + chatID + ".json"));
		sendAnswer(chatID, "*Reminder removed!*", true);
	}

	private List<Reminder> getReminders(long chatID) {
		Path reminderFile = Paths.get("reminder_data/" + chatID + ".json");
		List<Reminder> allReminder = Reminder.readData(reminderFile);

		if (allReminder.isEmpty()) {
			sendAnswer(chatID, "*You do not have any reminders set yet!*", true);
			return null;
		}

		return allReminder;
	}

	private void yesOrNo(long chatID) {
		double rdm = Math.random();
		if (rdm <= 0.5) {
			String response = "My answer is: YES!";
			sendAnswer(chatID, response, false);
			return;
		}

		String response = "Today you get a NO!";
		sendAnswer(chatID, response, false);
	}

	private void unknownCommand(long chatID) {
		sendAnswer(chatID, "*Unknown command!* Please make sure you typed the command correct.", true);
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
