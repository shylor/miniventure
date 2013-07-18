package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class AboutMenu extends Menu {
	private Menu parent; // Creates a parent object to go back to

	/** The about menu is a read menu about what the game was made for. Only contains text and a black background */
	public AboutMenu(Menu parent) {
		this.parent = parent; // The parent Menu that it will go back to.
	}

	/** The update method. 60 updates per second. */
	public void tick() {
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent); // If the user presses the "Attack" or "Menu" button, it will go back to the parent menu.
		}
	}

	/** Renders the text on the screen */
	public void render(Screen screen) {
		screen.clear(0); // clears the screen to be a black color.

		/* Font.draw Parameters: Font.draw(String text, Screen screen, int x, int y, int color) */
		
		Font.draw("About Minicraft", screen, 2 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555)); //draws Title text
		Font.draw("Minicraft was made", screen, 0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("by Markus Persson", screen, 0 * 8 + 4, 4 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("For the 22'nd ludum", screen, 0 * 8 + 4, 5 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("dare competition in", screen, 0 * 8 + 4, 6 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("december 2011.", screen, 0 * 8 + 4, 7 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("it is dedicated to", screen, 0 * 8 + 4, 9 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("my father. <3", screen, 0 * 8 + 4, 10 * 8, Color.get(0, 333, 333, 333)); // draws text
	}
}
