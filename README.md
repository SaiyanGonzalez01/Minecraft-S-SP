
# Eaglercraft 1.5.2 Special - Service Pack 

### A Modification of Eaglercraft SP
### 

![eaglercraft](https://g.deev.is/eaglercraft/cover.png)
:-:
Minecraft 1.5.2 S-SP will add new features to your game.

Minecraft 1.5.2 S-SP, or "Special Service Pack" is modification that will add new features to 1.5.2, and also make new updates to the game.

# Singleplayer

Simply press the 'Singleplayer' button on the main menu and you can create a regular vanilla minecraft and play it any time.

## Importing and Exporting Worlds
The worlds are stored in your browser's local storage, **you can export them as EPK files and import them again on all other Eaglercraft sites that also support singleplayer.** You can even copy an exported world to an entirely different computer, or send it to a friend, and import it and continue playing with all your progress saved.

# Creating your own server

There are ***multiple parts*** **to a server**, mainly consisting of a **regular 1.5.2 Bukkit server**, and a **modified version of Bungeecord** called **EaglercraftBungee**, which on top of the regular Bungeecord functionality, it translates WebSocket connections to raw TCP connections which Bukkit can understand.

You may also want to set up your own **client**, allowing you to *control default server listings, resource packs, and an overall faster connection due to less load.*

If you want to use a domain for your server, **a reverse proxy** can be set up to enable extra functionality within EaglercraftBungee. **NGINX** is recommended, and a tutorial is included **[here](#Creating-a-Reverse-Proxy---NGINX)**. **This is optional, and can be skipped by just connecting with the IP.**

### If replit is acceptable, you can use [this](https://replit.com/@ayunami2000/eaglercraft-server) to automatically set up everything for a server, otherwise, look below for instructions

## Creating a server - Bukkit

1. **Check if Java is installed.** You can download it from [https://www.java.com/en/download/](https://www.java.com/en/download/)
2. Download the [stable-download/stable-download.zip](https://github.com/lax1dude/eaglercraft/raw/main/stable-download/stable-download.zip) file from this repository
4. Extract the ZIP file you downloaded to a new folder
5. Open the new folder, go into the `java/bukkit_command` folder
6. In Windows, double-click `run.bat`. It should open a new terminal window  
![run.bat](https://i.gyazo.com/2b0f6b3e5b2e5a5a102c62ea5b6fba3f.png)  
**Some computers may just say 'run' instead of 'run.bat', both are correct**
7. On macOS or Linux, google how to open the terminal and use the `cd` command to navigate to `java/bukkit_command`  
Then, in that folder, run `chmod +x run_unix.sh` and then run `./run_unix.sh`. It should start the same server
8. To add some bukkit plugins, download the plugin's JAR file for CraftBukkit 1.5.2 and place it in `java/bukkit_command/plugins`
(See [https://github.com/lax1dude/eaglercraft-plugins/](https://github.com/lax1dude/eaglercraft-plugins/) to download some supported plugins)

## Creating a server - EaglercraftBungee
1. In the same new folder, go into the `java/bungee_command` folder
2. In Windows, double-click `run.bat`. It should open a second terminal window  
Keep both the first and second terminal window you opened, just minimize them, don't close
3. On macOS or Linux, repeat step 7 in [Creating a Server - Bukkit](#Creating-a-server---Bukkit), but navigate to `java/bungee_command` this time
4. To add some bungee plugins, download the plugin's JAR file and place it in `java/bungee_command/plugins`

There are alot more configurations in bungeecord, but this should set you up

**Your server is now ready.** Visit any client, and go to 'Multiplayer' from the main menu. Select 'Direct Connect', type `127.0.0.1:25565` and press 'Join Server'
**It should allow you to connect, if not, check the two terminal windows for errors**
	
## Compiling

To compile for the web, run the gradle 'teavm' compile target to generate the classes.js file.

The LWJGL runtime is no longer supported it is only included for reference

## Creating a resource pack

- To make a custom resource pack for your site, clone this repository and edit the files in [lwjgl-rundir/resources](https://github.com/lax1dude/eaglercraft/tree/main/lwjgl-rundir/resources).
- When you are done, navigate to [epkcompiler/](https://github.com/lax1dude/eaglercraft/tree/main/epkcompiler) and double-click `run.bat`. Wait for the window to say `Press any key to continue...` and close it. Then, go to `../javascript` in the repository and copy `javascript/assets.epk` to the `assets.epk` on your website
- If you're on mac or linux, navigate to the epkcompiler folder via `cd` and run `chmod +x run_unix.sh` and then `./run_unix.sh` to do this, then copy the same `javascript/assets.epk` to the `assets.epk` on your website

## Project Credits

- Saiyan Gonzalez; Project Creator
- Lax1dude; Eaglercraft Creator
- Ayunmani2000; Assisted the SP update
