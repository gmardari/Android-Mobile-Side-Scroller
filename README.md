# Android-Mobile-Side-Scroller

<h1>Introduction</h1>
Pixel Dash is a simple and exciting mobile side scrolling game where each map consists of a platform of coloured tiles. The player chooses one colour at a time and may scroll through all the colours without limit, and he may only collide with blocks of the same colour as his selected colour. This means he will fall off the platform if he does not select the right colour in time, ending the game. This is primarily a reaction based game, and it includes extras such as collectable rubies. The following explains what each file in the source code does.

<h2>PixelDash.java</h2>
The main file which handles the entry of the program at runtime and stores the information on the graphic sprites, game screens, game assets, and save states.

<h2>MenuScreen.java</h2>
Contains all the UI elements needed to create the screen. Adds functionality to the UI elements such as button response, and renders the UI.
This applies to LoadingScreen, SplashScreen, and PlayScreen.

<h2>SaveState.java</h2>
An object which utilizes a hash map to store information about the user's progress and preferences.

<h2>SaveStateManager.java</h2>
Uses JSON structures and base 64 encoding schemes to write and read a save state from a file.

<h2>CollisionListener.java</h2>
Keeps tracks of when the player collides with a tile.

<h2>Player.java</h2>
Defines the physical body of the player, and initializes the animated sprites of the player. Handles flipping through the selected colour.

