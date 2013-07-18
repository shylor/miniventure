package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class DeadMenu extends Menu {
	private int inputDelay = 60; // variable to delay the input of the player, so they won't skip the dead menu the first second.

	/* WonMenu & DeadMenu are very similar... scratch that, the exact same class with text changes. */
	
	public DeadMenu() {
	}
	
	/** Update Method, 60 updates (ticks) per second */
	public void tick() {
		if (inputDelay > 0) //If the input delay is above 0 (it starts at 60) then...
			inputDelay--; // the inputDelay will minus by 1. 
		else if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(new TitleMenu()); //If the delay is equal or lower than 0, then the person can go back to the title menu.
		}
	}

	/** Render method, draws stuff on the screen. */
	public void render(Screen screen) {
		Font.renderFrame(screen, "", 1, 3, 18, 9); // Draws a box frame based on 4 points. You can include a title as well.
		Font.draw("You died! Aww!", screen, 2 * 8, 4 * 8, Color.get(-1, 555, 555, 555)); // Draws text

		int seconds = game.gameTime / 60; // The current amount of seconds in the game.
		int minutes = seconds / 60; // The current amount of minutes in the game.
		int hours = minutes / 60; // The current amount of hours in the game.
		minutes %= 60; // fixes the number of minutes in the game. Without this, 1h 24min would look like: 1h 84min.
		seconds %= 60; // fixes the number of seconds in the game. Without this, 2min 35sec would look like: 2min 155sec.

		String timeString = ""; //Full text of time.
		if (hours > 0) {
			timeString = hours + "h" + (minutes < 10 ? "0" : "") + minutes + "m";// If over an hour has passed, then it will show hours and minutes.
		} else {
			timeString = minutes + "m " + (seconds < 10 ? "0" : "") + seconds + "s";// If under an hour has passed, then it will show minutes and seconds.
		}
		Font.draw("Time:", screen, 2 * 8, 5 * 8, Color.get(-1, 555, 555, 555));// Draws "Time:" on the frame
		Font.draw(timeString, screen, (2 + 5) * 8, 5 * 8, Color.get(-1, 550, 550, 550));// Draws the current time next to "Time:"
		Font.draw("Score:", screen, 2 * 8, 6 * 8, Color.get(-1, 555, 555, 555));// Draws "Score:" on the frame
		Font.draw("" + game.player.score, screen, (2 + 6) * 8, 6 * 8, Color.get(-1, 550, 550, 550));// Draws the current score next to "Score:"
		Font.draw("Press C to lose", screen, 2 * 8, 8 * 8, Color.get(-1, 333, 333, 333));//Draws text
	}
}
