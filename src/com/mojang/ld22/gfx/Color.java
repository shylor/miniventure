package com.mojang.ld22.gfx;

public class Color {

	
	/* To explain this class, you have to know what a int (integer) is in Java and how Bit-Shifting works.
	I made a small post, so go here if you don't already know: http://minicraftforums.com/viewtopic.php?f=9&t=2256
	*/
	
	/* Note: this class still confuses me a bit, lol. -David */
	
	/** This returns a integer with 4 rgb color values. */
	public static int get(int a, int b, int c, int d) {
		return (get(d) << 24) // gets the d value, and shifts its bit's 24 times to the left and adds...
		+ (get(c) << 16) // gets the c value, and shifts its bit's 16 times to the left and adds...
		+ (get(b) << 8) // gets the b value, and shifts its bit's 8 times to the left and adds...
		+ (get(a)); // gets the a value.
	}

	/** gets the color to use based off an integer */
	public static int get(int d) {
		if (d < 0) return 255; // if d is smaller than 0, then return 255.
		int r = d / 100 % 10; // the red value is the remainder of (d/100) / 10
		int g = d / 10 % 10; // the green value is the remainder of (d/10) / 10
		int b = d % 10; // the blue value is the remainder of d / 10.
		return r * 36 + g * 6 + b; // returns (red value * 36) + (green value * 6) + (blue value)
		
		// Why do we need all this math to get the colors? I don't even know. -David
	}

}