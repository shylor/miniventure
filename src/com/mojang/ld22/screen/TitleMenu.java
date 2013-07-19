package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class TitleMenu extends Menu {
	private int selected = 0; // Currently selected option
	
	private static final String[] options = { "Start game", "How to play", "About"}; // Options that are on the main menu, each seperated by a comma.

	public TitleMenu() {
	}

	public void tick() {
		if (input.up.clicked) selected--; // If the player presses the up key, then move up 1 option in the list
		if (input.down.clicked) selected++; // If the player presses the down key, then move down 1 option in the list

		int len = options.length; // The size of the list (normally 3 options)
		if (selected < 0) selected += len; // If the selected option is less than 0, then move it to the last option of the list.
		if (selected >= len) selected -= len; // If the selected option is more than or equal to the size of the list, then move it back to 0;

		if (input.attack.clicked || input.menu.clicked) { //If either the "Attack" or "Menu" keys are pressed then...
			if (selected == 0) { //If the selection is 0 ("Start game")
				Sound.test.play(); //Play a sound
				game.resetGame(); //Reset the game
				game.setMenu(null); //Set the menu to null (No menus active)
			}
			if (selected == 1) game.setMenu(new InstructionsMenu(this)); //If the selection is 1 ("How to play") then go to the instructions menu.
			if (selected == 2) game.setMenu(new AboutMenu(this)); //If the selection is 2 ("About") then go to the about menu.
		}
	}

	public void render(Screen screen) {
		screen.clear(0);// Clears the screen to a black color.

		/* This section is used to display the minicraft title */
		
		int h = 2; // Height of squares (on the spritesheet)
		int w = 13; // Width of squares (on the spritesheet)
		int titleColor = Color.get(0, 010, 131, 551); //Colors of the title
		int xo = (screen.w - w * 8) / 2; // X location of the title
		int yo = 24; // Y location of the title
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0); // Loops through all the squares to render them all on the screen.
			}
		}
		
		/* This section is used to display this options on the screen */
		
		for (int i = 0; i < options.length; i++) { // Loops through all the options in the list
			String msg = options[i]; // Text of the current option
			int col = Color.get(0, 222, 222, 222); // Color of unselected text
			if (i == selected) { // If the current option is the option that is selected
				msg = "> " + msg + " <"; // Add the cursors to the sides of the message
				col = Color.get(0, 555, 555, 555); // change the color of the option
			}
			Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8, col); // Draw the current option to the screen
		}

		Font.draw("(Arrow keys,X and C)", screen, 0, screen.h - 8, Color.get(0, 111, 111, 111)); // Draw text at the bottom
	}
}