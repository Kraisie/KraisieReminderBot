# Simple Telegram Reminder Bot

### [THIS BOT IS DISCONTINUED]
A simple program to keep all your thoughts and plans or even tasks in a organized Telegram chat instead on Post-Its, diverse Hardware or in a goldfish brain ;)

### HowTo
You will need a Telegram bot token to use this program. To get one please follow this [tutorial](https://www.sohamkamani.com/blog/2016/09/21/making-a-Telegram-bot/) until step 4.
Just download the repository as a `.zip` file and extract it.
You can either create a new `Bot.txt` file that contains your bot token and the bot name or let it create by the program on startup. If you wish to set it up while starting please do not start the program with the command to surpress all outputs! The `.txt` is as simple as:
```
123456789:abcdefghijklmnopqrstuvwxyzabcdefgh
nameOfTheBot
```

##### Linux
If you created the `Bot.txt` file already you can now start the bot using:
```cmd
cd folderWithZipContent
./gradlew run
```
To suppress all outputs use the following command (do **NOT** use that if your `Bot.txt` is not created/has no content yet as the program asks for these if not found):
```cmd
cd folderWithZipContent
./gradlew run -q > /dev/null 2>&1
```
Afterwards you can also detach the process from the terminal. To do that start the bot with one of the ways above and press `ctrl+z`. You can now type commands in the terminal again.
Type in `bg` and afterwards `disown`. A message like `bash: warning: deleting stopped job 1 with process group 6807` will appear. The number at the end is the PID. To end the process later type in `kill PID` while the PID is the number from before. If you do not know the PID anymore you can always get it using `ps`. It will be one of the java processes in the list. You can also end all gradle processes using `./gradlew --stop`.

##### Linux with tmux
If you are using tmux you can open `tmux` via commandline on your server. Start the bot like above, press `ctrl+z` and afterwards `ctrl+b` followed by `d`. You can now log out of the ssh session without the disowned bot getting terminated.
You can list all `tmux` processes with `tmux list-sessions`. Yo can then use `tmux kill-session -t <id>` to kill a specific `tmux` process. If you want to end all `tmux` processes you can also use `tmux kill-server`. 

##### Windows
Just download the repository as a `.zip` file and extract it.
You can either create a new `Bot.txt` file that contains your bot token and the bot name or let it create by the program on startup. The `.txt` is as simple as:
```
123456789:abcdefghijklmnopqrstuvwxyzabcdefgh
nameOfTheBot
```
If you created the `Bot.txt` file already you can now start the bot using:
```cmd
cd folderWithZipContent
./gradlew run
```
To suppress all outputs use the following command (do **NOT** use that if your `Bot.txt` is not created/has no content yet as the program asks for these if not found):
```cmd
cd folderWithZipContent
./gradlew run 2> nul
```
Unfortunately you can not detach the process from the terminal on windows as easily as on Linux so to do that you would need to find a way by yourself.

### Commands
The bot currently supports these commands:
* `/help` - Show a list of all commands and a description of what they are doing.
* `/reminder <text>` - Create a new reminder. `<text>` is the content that you want to save as a reminder.
* `/list` - List all of your saved reminders.
* `/edit <number> <text>` - Edit the text of a reminder. `<number>` is the number of the reminder on the list, `<text>` is the new text for the reminder.
* `/add <number> <text>` - Add the `<text>` to the end of the reminder with the given `<number>` on the list. You can start the text with `//s` to start with a whitespace. 
* `/remove <number> <text>` - Remove the `<text>` in the reminder with the `<number>` on the list. You can use  `//n` to also remove line breaks.
* `/search <text>` - Search for `<text>` in your reminders. Shows all reminders that contain `<text>`.
* `/delete <number>` - Delete a reminder of your list. The number is the number the reminder has in the list.
* `/yesorno` - Answers with a yes or a no. Maybe helpful if you want to decide something randomly.

### Examples
Here is an example using some of the commands above:

<details><summary>Example</summary><p>

Commands are marked with `>>`

Bot replies are marked with `==`
```
>>  /reminder Conquer Jerusalem. DEUS VULT!
==  Reminder saved!
>>  /reminder Do the dishes
==  Reminder saved!
>>  /list
==  [1 - 27.11.1095, 20:59]
    Conquer Jerusalem. DEUS VULT!
    [2 - 01.01.2019, 13:15]
    Do the dishes
>>  /edit 1 Survive Siege of Jerusalem
==  Reminder edited!
>>  /add 2 //sand do homework
==  Added text to the reminder!
>>  /list
==  [1 - 27.11.1095, 20:59]
    Survive Siege of Jerusalem
    [2 - 01.01.2019, 13:15]
    Do the dishes and do homework
>>  /remove 2 do the dishes and
==  Removed text from reminder!
>>  /search jerusalem
==  Found these matching reminders:
    [1 - 27.11.1095, 20:59]
    Survive Siege of Jerusalem
>>  /search this is not a reminder I have saved before
==  No matching reminder found!
>>  /list
==  [1 - 27.11.1095, 20:59]
    Survive Siege of Jerusalem
    [2 - 01.01.2019, 13:15]
    do homework
>>  /delete 1
==  Reminder removed!
>>  /list
==  [1 - 01.01.2019, 13:15]
    do homework
>>  /yesorno
==  Today you get a NO!
>>  /yesorno
==  My answer is: YES! 
```

</p>
</details>

### Data
All data provided to the bot can be found in the `reminder_data` folder. All files have the name of the chat id.
The data is not protected/encrypted whatsoever. Everyone with access to those files can read the content. As the server owner has all the source it is irrelevant to implement an encryption as everyone could just remove that code or tamper with it in some form which would render an encryption/other serialization useless.
At the moment I can just recommend to run the bot for yourself.

### Credits
This bot uses a [library](https://github.com/rubenlagus/TelegramBots) by [rubenlagus](https://github.com/rubenlagus) to access the Telegram API.
