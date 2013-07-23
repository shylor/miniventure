package com.mojang.ld22.gfx;

public class Font {
	private static String chars = "" + 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ      " + 
			"0123456789.,!?'\"-+=/\\%()<>:;     " + 
			""; // This is all the characters that will be translated to the screen. (The spaces are important).
	
	/* The order of the letters in the chars string is represented in the order that they appear in the sprite-sheet. */

	/* Note: I am thinking of changing this system in the future so that it's much simpler -David */
	
	/** Draws the message to the x & y coordinates on the screen. */
	public static void draw(String msg, Screen screen, int x, int y, int col) {
		msg = msg.toUpperCase(); // turns all the characters you type in into upper case letters.
		for (int i = 0; i < msg.length(); i++) { // Loops through all the characters that you typed
			int ix = chars.indexOf(msg.charAt(i)); // the current letter in the message loop
			if (ix >= 0) { // if that character's position is larger than or equal to 0 then...
				screen.render(x + i * 8, y, ix + 30 * 32, col, 0); // render the character on the screen
			}
		}
	}

	/** This renders the blue frame you see when you open up the crafting/inventory menus.
	 *  The width & height are based on 4 points (Staring x & y positions (0), and Ending x & y positions (1)). */
	public static void renderFrame(Screen screen, String title, int x0, int y0, int x1, int y1) {
		for (int y = y0; y <= y1; y++) { // loops through the height of the frame
			for (int x = x0; x <= x1; x++) { // loops through the width of the frame
				if (x == x0 && y == y0) // if the current x & y positions are at their starting positions...
					screen.render(x * 8, y * 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0); // render a corner point
				else if (x == x1 && y == y0) // if the current x position is at the end & the y is at the start...
					screen.render(x * 8, y * 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1); // render a corner point
				else if (x == x0 && y == y1) // if the x position is at the start & the y point is at the end...
					screen.render(x * 8, y * 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2); // render a corner point
				else if (x == x1 && y == y1) // if the current x & y positions are at their end positions...
					screen.render(x * 8, y * 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3); // render a corner point
				else if (y == y0) // if the y position is at it's starting position...
					screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0); // render a top end point
				else if (y == y1) // if the y position is at it's ending position...
					screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2); // render a bottom end point
				else if (x == x0) // if the x position is at it's begging position...
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0); // render a left end point
				else if (x == x1)  // if the x position is at it's ending position...
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1); // render a right end point
				else // if anything else...
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(5, 5, 5, 5), 1); // render a blue square
			}
		}

		draw(title, screen, x0 * 8 + 8, y0 * 8, Color.get(5, 5, 5, 550));

	}
}