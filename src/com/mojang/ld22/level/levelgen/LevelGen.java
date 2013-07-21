package com.mojang.ld22.level.levelgen;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.mojang.ld22.level.tile.Tile;

public class LevelGen {
	private static final Random random = new Random(); //Initializes the random class
	public double[] values; //An array of doubles, used to help making noise for the map
	private int w, h; // width and height of the map
	
	/** This creates noise to create random values for level generation */
	public LevelGen(int w, int h, int featureSize) {
		this.w = w; // assigns the width of the map
		this.h = h; // assigns the height of the map

		values = new double[w * h]; // creates the size of the value array (width * height)

		for (int y = 0; y < w; y += featureSize) { // Loops through the width of the map, going up by the featureSize value each time. 
			for (int x = 0; x < w; x += featureSize) { // Loops through the width of the map a second time, going up by the featureSize value each time.
				setSample(x, y, random.nextFloat() * 2 - 1); // sets a random value at a x and y point.
			}
		}

		int stepSize = featureSize; // stepSize is the featureSize that is given when you call the method. 
		double scale = 1.0 / w; // scale of the map
		double scaleMod = 1; // scale modification
		do { //do this...
			int halfStep = stepSize / 2;  // Half of stepSize
			for (int y = 0; y < w; y += stepSize) { // Loops through the width value of the map, going up by the stepSize value each time. 
				for (int x = 0; x < w; x += stepSize) { // Loops through the width value of the map, going up by the stepSize value each time. 
					double a = sample(x, y); // gets a sample value from the x and y value.
					double b = sample(x + stepSize, y); // gets a sample value from the next value of x, and the current y value.
					double c = sample(x, y + stepSize); // gets a sample value from the current x, and next value of y.
					double d = sample(x + stepSize, y + stepSize); // gets a sample value from the next x value and next y value.
					
					/* Well doesn't this one look complicated? No worries, just look at it step by step. 
					 *  The first thing it does is add up a+b+c+d as one variable. Then divides that number by 4 (making an average).
					 *  Since java follows the pemdas rule, lets look at the right side next.
					 *  "(random.nextFloat() * 2 - 1) * stepSize * scale"
					 *   random.nextFloat() creates any random value between 0 to 1. For example: 0.39541882 is a value that can be, lets call it r.
					 *   Now at the start it's simple numbers,  but as we go farther below in the code we see these values change in this loop.
					 *   So the value of e can be simplified as: (Average of a,b,c,d) + ((value from 0 to 1) * 2 - 1) * (stepSize value) * (scale value).
					 *   hope this helps a little bit, I'm not an algebra teacher lol. */
					double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale;
					
					setSample(x + halfStep, y + halfStep, e); // sets the value e at the next x value and next y value. repeat these until loop is done.
				}
			}
			for (int y = 0; y < w; y += stepSize) { // Loops through the width value of the map, going up by the stepSize value each time. 
				for (int x = 0; x < w; x += stepSize) { // Loops through the width value of the map, going up by the stepSize value each time. 
					double a = sample(x, y); // gets a sample value from the x and y value.
					double b = sample(x + stepSize, y); // gets a sample value from the next value of x, and the current y value.
					double c = sample(x, y + stepSize); // gets a sample value from the current x, and next value of y.
					double d = sample(x + halfStep, y + halfStep); // gets a sample value from the next x value and next y value.
					double e = sample(x + halfStep, y - halfStep); // gets a sample value from the next x value and the previous y value.
					double f = sample(x - halfStep, y + halfStep); // gets a sample value from the previous x value and the next y value.

					/* H & g are the same as e from the last paragraph. So see that for more info. */
					
					/* (Average of a,b,d,e) + ((value from 0 to 1) * 2 - 1) * (stepSize value) * (scale value) * 0.5 */
					double H = (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
					
					/* (Average of a,c,d,f) + ((value from 0 to 1) * 2 - 1) * (stepSize value) * (scale value) * 0.5 */
					double g = (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
					
					setSample(x + halfStep, y, H); // sets the H value at the half-way position of the next x value, and the current y value. 
					setSample(x, y + halfStep, g); // sets the g value at the current x value, and half-way position of the next y value.
				}
			}
			stepSize /= 2; // cuts the stepSize value in half.
			scale *= (scaleMod + 0.8); // Multiplies the scale by (scaleMod value + 0.8)
			scaleMod *= 0.3; // multiplies the scaleMod by 0.3
		} while (stepSize > 1); // ...if stepSize is larger than 1.
	}

	/** Returns a value from the values array based on the X and Y coordinates */
	private double sample(int x, int y) {
		return values[(x & (w - 1)) + (y & (h - 1)) * w];
	}

	/** Sets a value in the values array based on the X and Y coordinates */
	private void setSample(int x, int y, double value) {
		values[(x & (w - 1)) + (y & (h - 1)) * w] = value;
	}

	/** Creates and determines if the surface map is ready to be played. */
	public static byte[][] createAndValidateTopMap(int w, int h) {
		do { // Keep repeating this loop until it's done...
			byte[][] result = createTopMap(w, h); // creates the terrain.

			int[] count = new int[256]; // creates a new integer array

			/* The '& 0xff' part gets the last 8 bits of the 32-bit integer. */
			for (int i = 0; i < w * h; i++) { // Loops though the Width * Height of the map
				count[result[0][i] & 0xff]++; // Increases the data value by 1, trust me it's important.
			}
			
			 /* continue (in this context), will start the loop all over again. */
			
			if (count[Tile.rock.id & 0xff] < 100) continue; // If there are less than 100 rock tiles on the map, then restart the loop
			if (count[Tile.sand.id & 0xff] < 100) continue; // If there are less than 100 sand tiles on the map, then restart the loop
 			if (count[Tile.grass.id & 0xff] < 100) continue; // If there are less than 100 grass tiles, then restart the loop
			if (count[Tile.tree.id & 0xff] < 100) continue; // If there are less than 100 trees, then restart the loop
			if (count[Tile.stairsDown.id & 0xff] < 2) continue; // If there are less than 2 staircases going down, then restart the loop

			return result; // return the resulting map, and use it for the game

		} while (true); // While there is no returned result, keep looping.
	}

	/** Creates and determines if the underground map is ready to be played. */
	public static byte[][] createAndValidateUndergroundMap(int w, int h, int depth) {
		do { // Keep repeating this loop until it's done...
			byte[][] result = createUndergroundMap(w, h, depth); // creates the terrain.

			int[] count = new int[256]; // creates a new integer array

			/* The '& 0xff' part gets the last 8 bits of the 32-bit integer. */
			for (int i = 0; i < w * h; i++) { // Loops though the Width * Height of the map
				count[result[0][i] & 0xff]++; // Increases the data value by 1, trust me it's important.
			}
			if (count[Tile.rock.id & 0xff] < 100) continue; // If there are less than 100 rock tiles on the map, then restart the loop
			if (count[Tile.dirt.id & 0xff] < 100) continue; // If there are less than 100 dirt tiles on the map, then restart the loop
			if (count[(Tile.ironOre.id & 0xff) + depth - 1] < 20) continue; // If there are less than 20 ore tiles on the map, then restart the loop
			if (depth < 3) if (count[Tile.stairsDown.id & 0xff] < 2) continue; // if there is less than 2 stairs down on the map (besides lava level), then restart the loop.

			return result; // return the resulting map, and use it for the game

		} while (true); // While there is no returned result, keep looping.
	}

	/** Creates and determines if the sky map is ready to be played. */
	public static byte[][] createAndValidateSkyMap(int w, int h) {
		do { // Keep repeating this loop until it's done...
			byte[][] result = createSkyMap(w, h); // creates the terrain.

			int[] count = new int[256]; // creates a new integer array

			/* The '& 0xff' part gets the last 8 bits of the 32-bit integer. */
			for (int i = 0; i < w * h; i++) { // Loops though the Width * Height of the map
				count[result[0][i] & 0xff]++; // Increases the data value by 1, trust me it's important.
			}
			if (count[Tile.cloud.id & 0xff] < 2000) continue; //If there are less than 2000 clouds on the map, then restart the loop
			if (count[Tile.stairsDown.id & 0xff] < 2) continue; //If there are less than 2 stairs down on the map, then restart the loop

			return result; // return the resulting map, and use it for the game

		} while (true); // While there is no returned result, keep looking.
	}

	/** Creates the surface map */
	private static byte[][] createTopMap(int w, int h) {
		
		LevelGen mnoise1 = new LevelGen(w, h, 16); // creates noise used for map generation, see the top of this class for more info.
		LevelGen mnoise2 = new LevelGen(w, h, 16); // creates noise used for map generation, see the top of this class for more info.
		LevelGen mnoise3 = new LevelGen(w, h, 16); // creates noise used for map generation, see the top of this class for more info.

		LevelGen noise1 = new LevelGen(w, h, 32); // creates noise used for map generation, see the top of this class for more info.
		LevelGen noise2 = new LevelGen(w, h, 32); // creates noise used for map generation, see the top of this class for more info.

		byte[] map = new byte[w * h]; // The tiles of the map
		byte[] data = new byte[w * h]; // the data of the tiles
		for (int y = 0; y < h; y++) { // Loops through the height of the map
			for (int x = 0; x < w; x++) { // A loop inside a loop that loops through the width of the map.
				int i = x + y * w; // Current tile being edited.

				/* Math.abs() gets the absolute value of a number, which means it gets the number distance away from 0.
				 *  Examples:
				 *  Math.abs(4) = 4
				 *  Math.abs(-4) = 4
				 *  Math.abs(.12) = 0.12
				 *  Math.abs(-845.15) = 845.15
				 *  etc, etc */
				
				/* Gets a absolute value from the values from both noise objects, times it by 3 and subtracts by 2.*/
				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
				
				/* Gets a absolute value from the values from 2 noise objects */
				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				
				/* Gets a absolute value from the values from the previous mval object and mnoise3, times it by 3 and subtracts by 2.*/
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1; // The x distance: (x value) / ((width value) - 1) * 2 - 1
				double yd = y / (h - 1.0) * 2 - 1; // The y distance: (y value) / ((height value) - 1) * 2 - 1
				if (xd < 0) xd = -xd; // If the x distance is smaller than 0, it reverses the value (turning it from negative to positive).
				if (yd < 0) yd = -yd; // If the y distance is smaller than 0, it reverses the value (turning it from negative to positive).
				double dist = xd >= yd ? xd : yd; // distance of water, between the border and land. Chooses either xd or yd depending on which is bigger
				dist = dist * dist * dist * dist; // Multiplies itself by 4, get rid of some dists if you want more water around the edges
				dist = dist * dist * dist * dist; // Multiplies itself by 4 (again)
				val = val + 1 - dist * 20; // new value of val, takes distance into account.
				if (val < -0.5) { // If the final value is less than -0.5...
					map[i] = Tile.water.id; // the tile will become water
				} else if (val > 0.5 && mval < -1.5) { // else if the val is larger 0.5 and mval is smaller than -1.5...
					map[i] = Tile.rock.id; // the tile will become rock
				} else { // If the values don't agree with those then...
					map[i] = Tile.grass.id; // the tile will become grass.
				}
			}
		}
		
		/* Note: The next few blocks of code are very similar. They are used to populate the world with tiles. */
		
		
		 /* A loop that will occur if a value "i" is smaller than the Width * Height / 2800,  put in different number 
		  instead of 2800 to have different effects on the terrain. (Bigger number, more of that tile will spawn)*/
		for (int i = 0; i < w * h / 2800; i++) {
			int xs = random.nextInt(w); // A random number between 0 to the map's width (minus 1, because 0 is the first number)
			int ys = random.nextInt(h); // A random number between 0 to the map's height (minus 1, because 0 is the first number)
			for (int k = 0; k < 10; k++) { // a loop inside the main loop that occurs 10 times.
				int x = xs + random.nextInt(21) - 10; // x value which is: xs + (random value between 0 to 20) - 10
				int y = ys + random.nextInt(21) - 10; // y value which is: ys + (random value between 0 to 20) - 10
				for (int j = 0; j < 100; j++) { // A loop inside a loop inside the main loop, repeats 100 times.
					int xo = x + random.nextInt(5) - random.nextInt(5); // xo value: x + (random value between 0 to 4) - (random val between 0 to 4)
					int yo = y + random.nextInt(5) - random.nextInt(5); // yo value: y + (random value between 0 to 4) - (random val between 0 to 4)
					for (int yy = yo - 1; yy <= yo + 1; yy++) // Loopception, Loops if yy is smaller or equal to yo + 1
						for (int xx = xo - 1; xx <= xo + 1; xx++) // Loopception, Loops if xx is smaller or equal to xo + 1 
							if (xx >= 0 && yy >= 0 && xx < w && yy < h) { // if xx or yy is equal or larger than 0, and smaller than the width and height of the map...
								if (map[xx + yy * w] == Tile.grass.id) { // If the specific xx and yy coordinates happen to be a grass tile...
									map[xx + yy * w] = Tile.sand.id; // Then replace that tile with a sand tile.
								}
							}
				}
			}
		}

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);// A random number between 0 to the map's width (minus 1, because 0 is the first number)
			int y = random.nextInt(h);// A random number between 0 to the map's height (minus 1, because 0 is the first number)
			for (int j = 0; j < 200; j++) { // A loop that occurs 200 times
				int xx = x + random.nextInt(15) - random.nextInt(15); // x + (random value between 0 to 14) - (random value between 0 to 14)
				int yy = y + random.nextInt(15) - random.nextInt(15); // y + (random value between 0 to 14) - (random value between 0 to 14)
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) { // if xx or yy is equal or larger than 0, and smaller than the width and height of the map...
					if (map[xx + yy * w] == Tile.grass.id) { // If the specific xx and yy coordinates happen to be a grass tile...
						map[xx + yy * w] = Tile.tree.id; // replace the tile with a tree
					}
				}
			}
		}

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);// A random number between 0 to the map's width (minus 1, because 0 is the first number)
			int y = random.nextInt(h);// A random number between 0 to the map's height (minus 1, because 0 is the first number)
			int col = random.nextInt(4); // random number between 0 to 3
			for (int j = 0; j < 30; j++) { // loop that occurs 30 times
				int xx = x + random.nextInt(5) - random.nextInt(5); // x + (random value between 0 to 4) - (random value between 0 to 4)
				int yy = y + random.nextInt(5) - random.nextInt(5); // y + (random value between 0 to 4) - (random value between 0 to 4)
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) { // if xx or yy is equal or larger than 0, and smaller than the width and height of the map...
					if (map[xx + yy * w] == Tile.grass.id) { // If the specific xx and yy coordinates happen to be a grass tile...
						map[xx + yy * w] = Tile.flower.id; // replace the tile with a flower tile
						data[xx + yy * w] = (byte) (col + random.nextInt(4) * 16); // Adds data to the tile, (flipping it sideways)
					}
				}
			}
		}

		for (int i = 0; i < w * h / 100; i++) {
			int xx = random.nextInt(w);// A random number between 0 to the map's width (minus 1, because 0 is the first number)
			int yy = random.nextInt(h);// A random number between 0 to the map's height (minus 1, because 0 is the first number)
			if (xx >= 0 && yy >= 0 && xx < w && yy < h) { // if xx or yy is equal or larger than 0, and smaller than the width and height of the map...
				if (map[xx + yy * w] == Tile.sand.id) { // If the specific xx and yy coordinates happen to be a sand tile...
					map[xx + yy * w] = Tile.cactus.id; // replaces that sand tile with a cactus tile
				}
			}
		}

		int count = 0; // number of stairs in the map
		stairsLoop: for (int i = 0; i < w * h / 100; i++) {
			int x = random.nextInt(w - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.
			int y = random.nextInt(h - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.

			for (int yy = y - 1; yy <= y + 1; yy++) // Loops if yy is smaller or equal to y + 1
				for (int xx = x - 1; xx <= x + 1; xx++) { // Loops if xx is smaller or equal to x + 1
					if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop; // If the current tile is NOT a rock tile, then it skips the loops back to the top.
				}

			map[x + y * w] = Tile.stairsDown.id; // replaces the stone tile with a stairsDown tile.
			count++; // adds the count of stairs by 1.
			if (count == 4) break; // If the count is equal to 4, then stop the loop.
		}

		return new byte[][] { map, data }; // returns the map's tiles and data.
	}

	/** Creates the underground maps (mines, water mines, lava mines) */
	private static byte[][] createUndergroundMap(int w, int h, int depth) {
		LevelGen mnoise1 = new LevelGen(w, h, 16);
		LevelGen mnoise2 = new LevelGen(w, h, 16);  /* creates noise used for map generation, see the top of this class for more info. */
		LevelGen mnoise3 = new LevelGen(w, h, 16);

		LevelGen nnoise1 = new LevelGen(w, h, 16);
		LevelGen nnoise2 = new LevelGen(w, h, 16);  /* creates noise used for map generation, see the top of this class for more info. */
		LevelGen nnoise3 = new LevelGen(w, h, 16);

		LevelGen wnoise1 = new LevelGen(w, h, 16);
		LevelGen wnoise2 = new LevelGen(w, h, 16);  /* creates noise used for map generation, see the top of this class for more info. */
		LevelGen wnoise3 = new LevelGen(w, h, 16);

		LevelGen noise1 = new LevelGen(w, h, 32);  /* creates noise used for map generation, see the top of this class for more info. */
		LevelGen noise2 = new LevelGen(w, h, 32);

		byte[] map = new byte[w * h];  // The tiles of the map
		byte[] data = new byte[w * h];  // The data of the tiles
		for (int y = 0; y < h; y++) { // Loops through the height of the map
			for (int x = 0; x < w; x++) { // Loops through the width of the map
				int i = x + y * w; // current tile in the map

				/* Math.abs() gets the absolute value of a number, which means it gets the number distance away from 0.
				 *  Examples:
				 *  Math.abs(4) = 4
				 *  Math.abs(-4) = 4
				 *  Math.abs(.12) = 0.12
				 *  Math.abs(-845.15) = 845.15
				 *  etc, etc */
				
				/* Gets a absolute value from the values from both noise objects, times it by 3 and subtracts by 2.*/
				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
				
				/* Gets a absolute value from the values from 2 noise objects */
				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				/* Gets a absolute value from the values from the previous mval object and mnoise3, times it by 3 and subtracts by 2.*/
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				/* Gets a absolute value from the values from 2 noise objects */
				double nval = Math.abs(nnoise1.values[i] - nnoise2.values[i]);
				/* Gets a absolute value from the values from the previous nval object and mnoise3, times it by 3 and subtracts by 2.*/
				nval = Math.abs(nval - nnoise3.values[i]) * 3 - 2;

				/* Gets a absolute value from the values from 2 noise objects */
				double wval = Math.abs(wnoise1.values[i] - wnoise2.values[i]);
				/* Gets a absolute value from the values from the previous wval object and mnoise3, times it by 3 and subtracts by 2.*/
				wval = Math.abs(nval - wnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1; // The x distance: (x value) / ((width value) - 1) * 2 - 1
				double yd = y / (h - 1.0) * 2 - 1; // The y distance: (y value) / ((height value) - 1) * 2 - 1
				if (xd < 0) xd = -xd; // If the x distance is smaller than 0, it reverses the value (turning it from negative to positive).
				if (yd < 0) yd = -yd; // If the y distance is smaller than 0, it reverses the value (turning it from negative to positive).
				double dist = xd >= yd ? xd : yd; // distance of dirt in the cave. Chooses either xd or yd depending on which is bigger
				dist = dist * dist * dist * dist; // dist multiplies itself by 4
				dist = dist * dist * dist * dist; // multiplies itself by 4 (again)
				val = val + 1 - dist * 20; // new value of val, takes distance into account.

				if (val > -2 && wval < -2.0 + (depth) / 2 * 3) {//if val is larger than -2, and wval is larger than -2 + (currentdepth / 2 * 3)
					if (depth > 2) // if the depth is larger than 2...
						map[i] = Tile.lava.id; // the tile will become lava
					else
						map[i] = Tile.water.id; // else it will become water
				} else if (val > -2 && (mval < -1.7 || nval < -1.4)) {//if val is larger than -2, and mval is smaller than -1.7 OR nval is smaller than -1.4 then...
					map[i] = Tile.dirt.id; // the till will be dirt
				} else {
					map[i] = Tile.rock.id; // else it will be rock
				}
			}
		}

		{
			int r = 2; // Radius? (assuming it is)
			 /* A loop that will occur if a value "i" is smaller than the Width * Height / 400,  put in different number 
			  instead of 400 to have different effects on the terrain. (Bigger number, more of that tile will spawn)*/
			for (int i = 0; i < w * h / 400; i++) {
				int x = random.nextInt(w);  // A random number between 0 to the map's width (minus 1, because 0 is the first number)
				int y = random.nextInt(h); // A random number between 0 to the map's height (minus 1, because 0 is the first number)
				for (int j = 0; j < 30; j++) { // A loop that occurs 30 times
					int xx = x + random.nextInt(5) - random.nextInt(5); // x + (random number between 0 to 4) - (random number between 0 to 4)
					int yy = y + random.nextInt(5) - random.nextInt(5); // y + (random number between 0 to 4) - (random number between 0 to 4)
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) { // If xx & yy are equal to or larger than r, and smaller than (w - r) and (h - r) then...
						if (map[xx + yy * w] == Tile.rock.id) { // If the current tile is a rock tile...
							map[xx + yy * w] = (byte) ((Tile.ironOre.id & 0xff) + depth - 1); // Then set the ore tile (changes from Iron, Gold, & Gem depending on the depth)
						}
					}
				}
			}
		}

		if (depth < 3) { // If the depth is smaller than 3 then...
			int count = 0; // count of stairs
			stairsLoop: for (int i = 0; i < w * h / 100; i++) {
				int x = random.nextInt(w - 20) + 10; // A random number between 0 to the map's width minus 20, plus 10.
				int y = random.nextInt(h - 20) + 10; // A random number between 0 to the map's width minus 20, plus 10.

				for (int yy = y - 1; yy <= y + 1; yy++)  // Loops if yy is smaller or equal to y + 1
					for (int xx = x - 1; xx <= x + 1; xx++) {  // Loops if xx is smaller or equal to x + 1
						if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop; // If the current Tile is not a rock then start the main loop all over.
					}

				map[x + y * w] = Tile.stairsDown.id; // sets the tile to a stairsDown tile
				count++; //increases the count value
				if (count == 4) break; // if count is equal to 4, then stop the loop
			}
		}

		return new byte[][] { map, data };  // returns the map's tiles and data.
	}

	private static byte[][] createSkyMap(int w, int h) {
		LevelGen noise1 = new LevelGen(w, h, 8);  // creates noise used for map generation, see the top of this class for more info.
		LevelGen noise2 = new LevelGen(w, h, 8);  // creates noise used for map generation, see the top of this class for more info.
 
		byte[] map = new byte[w * h]; // The tiles of the map
		byte[] data = new byte[w * h]; // The data of the tiles
		for (int y = 0; y < h; y++) { // loops through the height of the map
			for (int x = 0; x < w; x++) { // loops though the width of the map
				int i = x + y * w; // current tile being used in the loop

				/* Math.abs() gets the absolute value of a number, which means it gets the number distance away from 0.
				 *  Examples:
				 *  Math.abs(4) = 4
				 *  Math.abs(-4) = 4
				 *  Math.abs(.12) = 0.12
				 *  Math.abs(-845.15) = 845.15
				 *  etc, etc */
				
				/* Gets a absolute value from the values from both noise objects, times it by 3 and subtracts by 2.*/
				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1; // The x distance: (x value) / ((width value) - 1) * 2 - 1
				double yd = y / (h - 1.0) * 2 - 1; // The y distance: (y value) / ((height value) - 1) * 2 - 1
				if (xd < 0) xd = -xd; // If the x distance is smaller than 0, it reverses the value (turning it from negative to positive).
				if (yd < 0) yd = -yd; // If the y distance is smaller than 0, it reverses the value (turning it from negative to positive).
				double dist = xd >= yd ? xd : yd; // distance of clouds in the sky. Chooses either xd or yd depending on which is bigger
				dist = dist * dist * dist * dist; // Multiplies itself 4 times.
				dist = dist * dist * dist * dist; // Multiplies itself 4 times. (again)
				val = -val * 1 - 2.2; // Reverses itself, then minuses by 2.2
				val = val + 1 - dist * 20; // new value of val, takes distance into account. 

				if (val < -0.25) { // If val is smaller than -0.25 then...
					map[i] = Tile.infiniteFall.id; // the tile is a infiniteFall tile
				} else {
					map[i] = Tile.cloud.id; // else it will be a cloud tile
				}
			}
		}

		
		cactusLoop: for (int i = 0; i < w * h / 50; i++) { //loops through all the numbers that are less than (width * height / 50)
			int x = random.nextInt(w - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.
			int y = random.nextInt(h - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.

			for (int yy = y - 1; yy <= y + 1; yy++) // Loops if yy is smaller or equal to y + 1
				for (int xx = x - 1; xx <= x + 1; xx++) { // Loops if xx is smaller or equal to x + 1
					if (map[xx + yy * w] != Tile.cloud.id) continue cactusLoop; // If the current tile is NOT a cloud tile, then it skips the loops back to the top.
				}

			map[x + y * w] = Tile.cloudCactus.id; // replaces the cloud tile with a cloud cactus tile.
		}

		int count = 0;// number of stairs in the map
		stairsLoop: for (int i = 0; i < w * h; i++) { //loops through the entire map
			int x = random.nextInt(w - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.
			int y = random.nextInt(h - 2) + 1; // A random number between 0 to the map's width minus 2, plus one.

			for (int yy = y - 1; yy <= y + 1; yy++) // Loops if yy is smaller or equal to y + 1
				for (int xx = x - 1; xx <= x + 1; xx++) { // Loops if xx is smaller or equal to x + 1
					if (map[xx + yy * w] != Tile.cloud.id) continue stairsLoop; // If the current tile is NOT a cloud tile, then it skips the loops back to the top.
				}

			map[x + y * w] = Tile.stairsDown.id; // replaces the cloud tile with a stairs tile.
			count++; // increases the count value by 1
			if (count == 2) break; // If count is equal to 2, then break the main loop
		}

		return new byte[][] { map, data }; // returns the map's tiles and data.
	}

	/** Yep, LevelGen has a main method. When you run this class it will show a generator. */
	public static void main(String[] args) {
		/* Note: I changed a bit of this method to make it a lot better. -David */
		
		int d=0; // Depth used when looking at the underground map
		boolean hasquit = false; // Determines if the player has quit the program or not.
		while (!hasquit) { //If the player has not quit the map
			int w = 128; // width of the map
			int h = 128; // height of the map
			int m = 0; // map being looked at (0 = overworld, 1 = underground, 2 = sky)
			byte[] map; // the map
			
			/* The switch statement is like a short if-else method.
			  In this case we are switching the map variable based on what m is.
			  If m = 1, then it will be case 1. Which is the underground
			  if m = 2, then it will be case 2. Which is the sky
			  If m is anything else, it will create the sky map. (that is what default stands for) */
			switch(m){
			default: 
			map = LevelGen.createAndValidateTopMap(w, h)[0]; // Map will show the surface.
			break; // breaks the switch so it won't go to case 1.
			case 1:
			map = LevelGen.createAndValidateUndergroundMap(w, h, (d++ % 3) + 1)[0];// Map will show the underground, switching depths each time.
			break; // breaks the switch so it won't go to case 2.
			case 2:
			map = LevelGen.createAndValidateSkyMap(w, h)[0]; // Map will show the sky.
			break; // breaks the switch.
			}

			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // creates an image
			int[] pixels = new int[w * h]; // The pixels in the image. (an integer array, the size is Width * height)
			for (int y = 0; y < h; y++) { // Loops through the height of the map
				for (int x = 0; x < w; x++) { // (inner-loop)Loops through the entire width of the map
					int i = x + y * w; // current tile of the map.
					
					/*The colors used in the pixels are hexadecimal (0xRRGGBB). 
				     0xff0000 would be fully red
					 0x00ff00 would be fully blue
					 0x0000ff would be fully green
					 0x000000 would be black
					 and 0xffffff would be white
					 etc. */
					if (map[i] == Tile.water.id) pixels[i] = 0x000080; // If the tile is water, then the pixel will be blue
					if (map[i] == Tile.grass.id) pixels[i] = 0x208020; // If the tile is grass, then the pixel will be green
					if (map[i] == Tile.rock.id) pixels[i] = 0xa0a0a0; // if the tile is rock, then the pixel will be gray
					if (map[i] == Tile.dirt.id) pixels[i] = 0x604040; // if the tile is dirt, then the pixel will be brown
					if (map[i] == Tile.sand.id) pixels[i] = 0xa0a040;  // if the tile is sand, then the pixel will be yellow
					if (map[i] == Tile.tree.id) pixels[i] = 0x003000; // if the tile is tree, then the pixel will be a darker green
					if (map[i] == Tile.lava.id) pixels[i] = 0xff2020; // if the tile is lava, then it will be red
					if (map[i] == Tile.cloud.id) pixels[i] = 0xeeeeee; // if the tile is a cloud, then it will be light gray
					if (map[i] == Tile.stairsDown.id) pixels[i] = 0xffffff; // if the tile is a stairs down, then it will be white.
					if (map[i] == Tile.stairsUp.id) pixels[i] = 0xffffff; // if the tile is a stairs up, then it will be white.
					if (map[i] == Tile.cloudCactus.id) pixels[i] = 0xdd55dd; // if the tile is a cloud cactus, then it will be pink
					if (map[i] == Tile.infiniteFall.id) pixels[i] = 0xcccccc; // if the tile is a cloud cactus, then it will be darker gray
				}
			}
			img.setRGB(0, 0, w, h, pixels, 0, w); // sets the pixels into the image
			
			String[] options = {"Another", "Quit"}; //Name of the buttons used for the window.
			
			int o = JOptionPane.showOptionDialog( // creates a new window dialog (It's an integer because it returns a number)
			null, // this would normally be used for a parent component (parent window), but we don't have one so it's null.
			null, // this would normally be used for a message, but since we use a image so it's null.
			"Map Generator", // Title of the window
			JOptionPane.YES_NO_OPTION, // Option type
			JOptionPane.QUESTION_MESSAGE, // message type (not important)
			new ImageIcon(img.getScaledInstance(w * 4, h * 4, Image.SCALE_AREA_AVERAGING)), // creates the image, and scales it up 4 times as big
			options, // lists the buttons below the image
			null // start value (not important)
			);
			/* Now you noticed that we made the dialog an integer. This is because when you click a button it will return a number.
		       Since we passed in 'options', the window will return 0 if you press "Another" and it will return 1 when you press "Quit".
			   If you press the red "x" close mark, the window will return -1 */
			
			// If the dialog returns -1 (red "x" button) or 1 ("Quit" button) then...
			if(o == -1 || o == 1) hasquit = true; // stop the loop and close the program.
		}
	}
}