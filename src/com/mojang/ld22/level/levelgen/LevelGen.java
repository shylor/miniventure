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

	public static byte[][] createAndValidateTopMap(int w, int h) {
		int attempt = 0;
		do {
			byte[][] result = createTopMap(w, h);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.rock.id & 0xff] < 100) continue;
			if (count[Tile.sand.id & 0xff] < 100) continue;
			if (count[Tile.grass.id & 0xff] < 100) continue;
			if (count[Tile.tree.id & 0xff] < 100) continue;
			if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	public static byte[][] createAndValidateUndergroundMap(int w, int h, int depth) {
		int attempt = 0;
		do {
			byte[][] result = createUndergroundMap(w, h, depth);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.rock.id & 0xff] < 100) continue;
			if (count[Tile.dirt.id & 0xff] < 100) continue;
			if (count[(Tile.ironOre.id & 0xff) + depth - 1] < 20) continue;
			if (depth < 3) if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	public static byte[][] createAndValidateSkyMap(int w, int h) {
		int attempt = 0;
		do {
			byte[][] result = createSkyMap(w, h);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.cloud.id & 0xff] < 2000) continue;
			if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	private static byte[][] createTopMap(int w, int h) {
		LevelGen mnoise1 = new LevelGen(w, h, 16);
		LevelGen mnoise2 = new LevelGen(w, h, 16);
		LevelGen mnoise3 = new LevelGen(w, h, 16);

		LevelGen noise1 = new LevelGen(w, h, 32);
		LevelGen noise2 = new LevelGen(w, h, 32);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = xd >= yd ? xd : yd;
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				if (val < -0.5) {
					map[i] = Tile.water.id;
				} else if (val > 0.5 && mval < -1.5) {
					map[i] = Tile.rock.id;
				} else {
					map[i] = Tile.grass.id;
				}
			}
		}

		for (int i = 0; i < w * h / 2800; i++) {
			int xs = random.nextInt(w);
			int ys = random.nextInt(h);
			for (int k = 0; k < 10; k++) {
				int x = xs + random.nextInt(21) - 10;
				int y = ys + random.nextInt(21) - 10;
				for (int j = 0; j < 100; j++) {
					int xo = x + random.nextInt(5) - random.nextInt(5);
					int yo = y + random.nextInt(5) - random.nextInt(5);
					for (int yy = yo - 1; yy <= yo + 1; yy++)
						for (int xx = xo - 1; xx <= xo + 1; xx++)
							if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
								if (map[xx + yy * w] == Tile.grass.id) {
									map[xx + yy * w] = Tile.sand.id;
								}
							}
				}
			}
		}

		/*
		 * for (int i = 0; i < w * h / 2800; i++) { int xs = random.nextInt(w); int ys = random.nextInt(h); for (int k = 0; k < 10; k++) { int x = xs + random.nextInt(21) - 10; int y = ys + random.nextInt(21) - 10; for (int j = 0; j < 100; j++) { int xo = x + random.nextInt(5) - random.nextInt(5); int yo = y + random.nextInt(5) - random.nextInt(5); for (int yy = yo - 1; yy <= yo + 1; yy++) for (int xx = xo - 1; xx <= xo + 1; xx++) if (xx >= 0 && yy >= 0 && xx < w && yy < h) { if (map[xx + yy * w] == Tile.grass.id) { map[xx + yy * w] = Tile.dirt.id; } } } } }
		 */

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			for (int j = 0; j < 200; j++) {
				int xx = x + random.nextInt(15) - random.nextInt(15);
				int yy = y + random.nextInt(15) - random.nextInt(15);
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
					if (map[xx + yy * w] == Tile.grass.id) {
						map[xx + yy * w] = Tile.tree.id;
					}
				}
			}
		}

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int col = random.nextInt(4);
			for (int j = 0; j < 30; j++) {
				int xx = x + random.nextInt(5) - random.nextInt(5);
				int yy = y + random.nextInt(5) - random.nextInt(5);
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
					if (map[xx + yy * w] == Tile.grass.id) {
						map[xx + yy * w] = Tile.flower.id;
						data[xx + yy * w] = (byte) (col + random.nextInt(4) * 16);
					}
				}
			}
		}

		for (int i = 0; i < w * h / 100; i++) {
			int xx = random.nextInt(w);
			int yy = random.nextInt(h);
			if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
				if (map[xx + yy * w] == Tile.sand.id) {
					map[xx + yy * w] = Tile.cactus.id;
				}
			}
		}

		int count = 0;
		stairsLoop: for (int i = 0; i < w * h / 100; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.stairsDown.id;
			count++;
			if (count == 4) break;
		}

		return new byte[][] { map, data };
	}

	private static byte[][] createUndergroundMap(int w, int h, int depth) {
		LevelGen mnoise1 = new LevelGen(w, h, 16);
		LevelGen mnoise2 = new LevelGen(w, h, 16);
		LevelGen mnoise3 = new LevelGen(w, h, 16);

		LevelGen nnoise1 = new LevelGen(w, h, 16);
		LevelGen nnoise2 = new LevelGen(w, h, 16);
		LevelGen nnoise3 = new LevelGen(w, h, 16);

		LevelGen wnoise1 = new LevelGen(w, h, 16);
		LevelGen wnoise2 = new LevelGen(w, h, 16);
		LevelGen wnoise3 = new LevelGen(w, h, 16);

		LevelGen noise1 = new LevelGen(w, h, 32);
		LevelGen noise2 = new LevelGen(w, h, 32);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double nval = Math.abs(nnoise1.values[i] - nnoise2.values[i]);
				nval = Math.abs(nval - nnoise3.values[i]) * 3 - 2;

				double wval = Math.abs(wnoise1.values[i] - wnoise2.values[i]);
				wval = Math.abs(nval - wnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = xd >= yd ? xd : yd;
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				if (val > -2 && wval < -2.0 + (depth) / 2 * 3) {
					if (depth > 2)
						map[i] = Tile.lava.id;
					else
						map[i] = Tile.water.id;
				} else if (val > -2 && (mval < -1.7 || nval < -1.4)) {
					map[i] = Tile.dirt.id;
				} else {
					map[i] = Tile.rock.id;
				}
			}
		}

		{
			int r = 2;
			for (int i = 0; i < w * h / 400; i++) {
				int x = random.nextInt(w);
				int y = random.nextInt(h);
				for (int j = 0; j < 30; j++) {
					int xx = x + random.nextInt(5) - random.nextInt(5);
					int yy = y + random.nextInt(5) - random.nextInt(5);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
						if (map[xx + yy * w] == Tile.rock.id) {
							map[xx + yy * w] = (byte) ((Tile.ironOre.id & 0xff) + depth - 1);
						}
					}
				}
			}
		}

		if (depth < 3) {
			int count = 0;
			stairsLoop: for (int i = 0; i < w * h / 100; i++) {
				int x = random.nextInt(w - 20) + 10;
				int y = random.nextInt(h - 20) + 10;

				for (int yy = y - 1; yy <= y + 1; yy++)
					for (int xx = x - 1; xx <= x + 1; xx++) {
						if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop;
					}

				map[x + y * w] = Tile.stairsDown.id;
				count++;
				if (count == 4) break;
			}
		}

		return new byte[][] { map, data };
	}

	private static byte[][] createSkyMap(int w, int h) {
		LevelGen noise1 = new LevelGen(w, h, 8);
		LevelGen noise2 = new LevelGen(w, h, 8);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = xd >= yd ? xd : yd;
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = -val * 1 - 2.2;
				val = val + 1 - dist * 20;

				if (val < -0.25) {
					map[i] = Tile.infiniteFall.id;
				} else {
					map[i] = Tile.cloud.id;
				}
			}
		}

		stairsLoop: for (int i = 0; i < w * h / 50; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.cloudCactus.id;
		}

		int count = 0;
		stairsLoop: for (int i = 0; i < w * h; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.stairsDown.id;
			count++;
			if (count == 2) break;
		}

		return new byte[][] { map, data };
	}

	public static void main(String[] args) {
		int d = 0;
		while (true) {
			int w = 128;
			int h = 128;

			byte[] map = LevelGen.createAndValidateTopMap(w, h)[0];
			// byte[] map = LevelGen.createAndValidateUndergroundMap(w, h, (d++ % 3) + 1)[0];
			// byte[] map = LevelGen.createAndValidateSkyMap(w, h)[0];

			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			int[] pixels = new int[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int i = x + y * w;

					if (map[i] == Tile.water.id) pixels[i] = 0x000080;
					if (map[i] == Tile.grass.id) pixels[i] = 0x208020;
					if (map[i] == Tile.rock.id) pixels[i] = 0xa0a0a0;
					if (map[i] == Tile.dirt.id) pixels[i] = 0x604040;
					if (map[i] == Tile.sand.id) pixels[i] = 0xa0a040;
					if (map[i] == Tile.tree.id) pixels[i] = 0x003000;
					if (map[i] == Tile.lava.id) pixels[i] = 0xff2020;
					if (map[i] == Tile.cloud.id) pixels[i] = 0xa0a0a0;
					if (map[i] == Tile.stairsDown.id) pixels[i] = 0xffffff;
					if (map[i] == Tile.stairsUp.id) pixels[i] = 0xffffff;
					if (map[i] == Tile.cloudCactus.id) pixels[i] = 0xff00ff;
				}
			}
			img.setRGB(0, 0, w, h, pixels, 0, w);
			JOptionPane.showMessageDialog(null, null, "Another", JOptionPane.YES_NO_OPTION, new ImageIcon(img.getScaledInstance(w * 4, h * 4, Image.SCALE_AREA_AVERAGING)));
		}
	}
}