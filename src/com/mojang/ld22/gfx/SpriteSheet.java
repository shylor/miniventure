package com.mojang.ld22.gfx;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	public int width, height; // width and height of the sprite sheet
	public int[] pixels; // the pixels on the image (integer array)

	public SpriteSheet(BufferedImage image) {
		width = image.getWidth(); // assigns the width from the image
		height = image.getHeight(); // assigns the height from the image
		pixels = image.getRGB(0, 0, width, height, null, 0, width); // Assigns the pixels of the image
		for (int i = 0; i < pixels.length; i++) { //loops through all the pixels
			/* The '& 0xff' part gets the last 8 bits of the 32-bit integer. */
			pixels[i] = (pixels[i] & 0xff) / 64; //divides the last 8 bits of the pixel by 64. Doesn't seem to do much at all.
		}
	}
}