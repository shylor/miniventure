package com.mojang.ld22.gfx;

public class Screen {
	public int xOffset; // the x offset of the screen.
	public int yOffset; // the y offset of the screen

	public static final int BIT_MIRROR_X = 0x01; // used for mirroring an image 
	public static final int BIT_MIRROR_Y = 0x02; // used for mirroring an image

	public final int w, h; // width and height of the screen
	public int[] pixels; // pixels on the screen

	private SpriteSheet sheet; // sprite sheet used in the game

	public Screen(int w, int h, SpriteSheet sheet) {
		this.sheet = sheet; // assigns the sprite-sheet
		this.w = w; // assigns width of the screen
		this.h = h; // assigns height of the screen

		pixels = new int[w * h]; // integer array of all the pixels on the screen.

	}

	/** Clears all the colors on the screen */
	public void clear(int color) {
		for (int i = 0; i < pixels.length; i++) // Loops through each pixel on the scren
			pixels[i] = color; // turns each pixel into a single color (clearing the screen!)
	}

	/** Renders an object from the sprite sheet based on screen coordinates, tile (SpriteSheet location), colors, and bits (for mirroring) */
	public void render(int xp, int yp, int tile, int colors, int bits) {
		xp -= xOffset; // horizontal offset of the screen
		yp -= yOffset; // vertical offset of the screen
		boolean mirrorX = (bits & BIT_MIRROR_X) > 0; // determines if the image should be mirrored horizontally.
		boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; // determines if the image should be mirrored vertically.

		/* Whenever you see a '%' sign it means that java will divide the two numbers and get the remainder instead of the answer
		 * 	For example:
		 * 	6 / 3 = 2. because 3 goes into 6 two times.
		 *  6 % 3 = 0. because 3 goes into 6 evenly and the remainder is 0.
		 *  However:
		 *  8 / 3 = 2. because java doesn't round the number and gets the whole number out of it.
		 *  8 % 3 = 2. because when you divide the two number, 3 goes into 6 two times and the remainder is 2.
		 *  
		 *  Math lesson over :) */
		
		int xTile = tile % 32; // gets the x position of the tile by taking the number and getting the remainder when you divide it by 32. 
		int yTile = tile / 32; // gets the y position of the tile by taking the number and diving it by 32
		int toffs = xTile * 8 + yTile * 8 * sheet.width; // Get's the offset, the 8's represent the size of the tile. (8 by 8 pixels)

		// You can space each line out if it looks too complicated at once.
		
		for (int y = 0; y < 8; y++) { // Loops 8 times (because of the height of the tile)
			int ys = y; // current y pixel
			if (mirrorY) ys = 7 - y; // Reverses the pixel for a mirroring effect
			if (y + yp < 0 || y + yp >= h) continue; // If the pixel is out of bounds, then skip the rest of the loop.
			for (int x = 0; x < 8; x++) { // Loops 8 times (because of the width of the tile)
				if (x + xp < 0 || x + xp >= w) continue; // If the pixel is out of bounds, then skip the rest of the loop.
				int xs = x; // current x pixel
				if (mirrorX) xs = 7 - x;  // Reverses the pixel for a mirroring effect
				int col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255; // gets the color based on the passed in colors value.
				if (col < 255) pixels[(x + xp) + (y + yp) * w] = col; // Inserts the colors into the image.
			}
		}
	}

	/** Sets the offset of the screen */
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset; // assigns the horizontal offset
		this.yOffset = yOffset; // assigns the vertical offset
	}

	 /* Used for the scattered dots at the edge of the light radius underground. */
	private int[] dither = new int[] { 0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5, };

	/** Overlays the screen with pixels*/
	public void overlay(Screen screen2, int xa, int ya) {
		int[] oPixels = screen2.pixels; // Integer array of pixels to overlay the screen with.
		int i = 0; // current pixel on the screen
		for (int y = 0; y < h; y++) { // Loops through the height of the screen.
			for (int x = 0; x < w; x++) { // Loops through the width of the screen.
				 /* if the current pixel divided by 10 is smaller than the dither thingy with a complicated formula
					then it will fill the pixel with a black color. Yep, Nailed it! */
				if (oPixels[i] / 10 <= dither[((x + xa) & 3) + ((y + ya) & 3) * 4]) pixels[i] = 0;
				i++; // moves to the next pixel.
			}

		}
	}

	public void renderLight(int x, int y, int r) {
		x -= xOffset; // applies the horizontal offset to the x position
		y -= yOffset; // applies the vertical offset to the y position
		int x0 = x - r; // starting x position of the circle
		int x1 = x + r; // ending x position of the circle
		int y0 = y - r; // starting y position of the circle
		int y1 = y + r; // ending y position of the circle

		if (x0 < 0) x0 = 0; // If the starting position of the circle is less than 0, then move it to 0
		if (y0 < 0) y0 = 0; // If the starting position of the circle is less than 0, then move it to 0
		if (x1 > w) x1 = w; // If the ending position of the circle is more than the width, then move it to the width
		if (y1 > h) y1 = h; // If the ending position of the circle is more than the height, then move it to the height
		for (int yy = y0; yy < y1; yy++) { // Loops for (the difference between y0 and y1) times
			int yd = yy - y; // the vertical difference between the current pixel and the y position
			yd = yd * yd; // squares the yd value
			for (int xx = x0; xx < x1; xx++) { // Loops for (the difference between x0 and x1) times
				int xd = xx - x; // the horizontal difference between the current pixel and the x position
				int dist = xd * xd + yd; // squares the distance of xd and adds yd for total distance.
				if (dist <= r * r) { // if distance is smaller or equal to r (radius) squared then...
					int br = 255 - dist * 255 / (r * r); // (255 - (distance value * 255) / r²) the area where light will be rendered
					if (pixels[xx + yy * w] < br) pixels[xx + yy * w] = br; // If the current pixel is smaller than br, then the pixel will equal br.
				}
			}
		}
	}
}