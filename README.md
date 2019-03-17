# Simple Telegram Reminder Bot

A simple program to keep all your thoughts and plans or even tasks in a organized Telegram chat instead on Post-Its, diverse Hardware or in a goldfish brain ;)

### HowTo
You will need a Telegram bot token to use this program. To get one please follow this [tutorial](https://www.sohamkamani.com/blog/2016/09/21/making-a-Telegram-bot/) until step 4.
Just download the repository as a `.zip` file and extract it.
You can either create a new `Bot.txt` file that contains your bot token and the bot name or let it create by the program on startup. The `.txt` is as simple as:
```
123456789:abcdefghijklmnopqrstuvwxyzabcdefgh
nameOfTheBot
```

##### Linux
Wether or not you created the `Bot.txt` file you can now start the bot using:
```cmd
cd folderWithZipCpontent
./gradlew run
```
To suppress all outputs use the following command:
```cmd
cd folderWithZipCpontent
./gradlew run -q > /dev/null 2>&1
```
Afterwards you can also detach the process from the terminal. To do that start the bot with one of the ways above and press `ctrl+z`. You can now type commands in the terminal again.
Type in `bg` and afterwards `disown`. A message like `bash: warning: deleting stopped job 1 with process group 6807` will appear. The number at the end is the PID. To end the process later type in `kill PID` while the PID is the number from before. If you do not know the PID anymore you can always get it using `ps`. It will be one of the java processes in the list. You can also end all gradle processes using `./gradlew --stop`.

If you are using tmux you can open `tmux` via commandline on your server. Start the bot like above, press `ctrl+z` and afterwards `ctrl+b` followed by `d`. You can now log out of the ssh session without the disowned bot getting terminated.
##### Windows
Just download the repository as a `.zip` file and extract it.
You can either create a new `Bot.txt` file that contains your bot token and the bot name or let it create by the program on startup. The `.txt` is as simple as:
```
123456789:abcdefghijklmnopqrstuvwxyzabcdefgh
nameOfTheBot
```
Wether or not you created the `Bot.txt` file you can now start the bot using:
```cmd
cd folderWithZipCpontent
./gradlew run
```
To suppress all outputs use the following command:
```cmd
cd folderWithZipCpontent
./gradlew run 2> nul
```
Unfortunately you can not detach the process from the terminal on windows.

### Data
All data provided to the bot can be found in the `reminder_data` folder. All files have the name of the chat id.
The data is not protected/encrypted whatsoever. Everyone with access to those files can read the content.
At the moment I can just recommend to run the bot for yourself.

### Credits
This bot uses a [library](https://github.com/rubenlagus/TelegramBots) by rubenlagus to access the Telegram API.