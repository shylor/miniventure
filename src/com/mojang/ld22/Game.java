package com.mojang.ld22;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import com.mojang.ld22.screen.WonMenu;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	/* random is a class that can create random numbers. 
	 * Example: 'int r = random.randInt(20);'
	 * r will be a number between (0 to 19) [0 counts as the first value)
	 */
	private Random random = new Random();
	// This is the name on the application window
	public static final String NAME = "Miniventure";
	// This is the hight of the game * scale
	public static final int HEIGHT = 200;
	// This is the width of the game * scale
	public static final int WIDTH = 267;
	private static final int SCALE = 3; // scales the window

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); //creates an image to be displayed on the screen.
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // the array of pixels that will be displayed on the screen.
	private boolean running = false; // This stores if the game is running or paused
	private Screen screen; // Creates the main screen
	private Screen lightScreen; // Creates a front screen to render the darkness in caves (Fog of war).
	private InputHandler input = new InputHandler(this); // Creates the class (InputHandler.java) that will take in out inputs (aka: pressing the 'W' key).

	private int[] colors = new int[256]; // All of the colors put into an array
	private int tickCount = 0; // Used in the ticking system
	public int gameTime = 0; // Main value in the timer used on the dead screen.

	private Level level; // This is the current level you are on.
	// This array is about the different levels.
	// Remember that arrays start at 0 so you have 0,1,2,3,4
	private Level[] levels = new Level[5];
	// This is the level the player is on.
	// This is set to 3 which is the surface.
	private int currentLevel = 3;
	public Player player; // the player himself

	public Menu menu; // the current menu you are on.
	private int playerDeadTime; // the paused time when you die before the dead menu shows up.
	private int pendingLevelChange; // used to determined if the player should change levels or not.
	private int wonTimer = 0; // the paused time when you win before the win menu shows up.
	public boolean hasWon = false; 	// If the player wins this is set to true

	//Blue text is used in eclipse to set the description of a method. Put your mouse over the "SetMenu(Menu menu)" method text to see it.
	/** Use this method to switch to another menu. */
	public void setMenu(Menu menu) {
		this.menu = menu;
		if (menu != null) menu.init(this, input);
	}
	
	/** This starts the game logic after a pause */
	public void start() {
		running = true;
		new Thread(this).start();
	}

	/** This pauses the game */
	public void stop() {
		running = false;
	}

	/** This resets the game*/
	public void resetGame() {
		// Resets all values
		playerDeadTime = 0;
		wonTimer = 0;
		gameTime = 0;
		hasWon = false;

		levels = new Level[5];
		currentLevel = 3;

		// generates new maps
		levels[4] = new Level(128, 128, 1, null); // creates the sky map
		levels[3] = new Level(128, 128, 0, levels[4]); // creates the overworld
		levels[2] = new Level(128, 128, -1, levels[3]); // creates the mines (iron level)
		levels[1] = new Level(128, 128, -2, levels[2]); // creates the deep mines (water/gold level)
		levels[0] = new Level(128, 128, -3, levels[1]); // creates the nether (lava/gem level)

		/* Please note: the terms "Mines", "Deep Mines", and "Nether" are not the real names used in the code
		   I just got those names from the wiki where someone named them that. Those levels don't have any real names yet -David 
		*/
		
		level = levels[currentLevel]; // puts level to the current level (surface)
		player = new Player(this, input); // creates a new player
		player.findStartPos(level); // finds the start level for the player

		level.add(player); // adds the player to the current level

		for (int i = 0; i < 5; i++) {
			levels[i].trySpawn(5000); // populates all 5 levels with mobs.
		}
	}
	
	/** Initialization step, this is called when the game first starts. Sets up the colors and the screens. */
	private void init() {
		int pp = 0;
		/* This loop below creates the 216 colors in minicraft. */
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);
					int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

					int r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10;
					int g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10;
					int b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10;
					colors[pp++] = r1 << 16 | g1 << 8 | b1;

				}
			}
		}
		/* This sets up the screens, loads the icons.png spritesheet. */
		try {
			screen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/icons.png"))));
			lightScreen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/icons.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		resetGame(); // starts a new game... for some reason?
		setMenu(new TitleMenu());  // Sets the menu to the title menu.
	}

	/** This is the main loop that runs the game
	 It keeps track of the amount of time that. It keeps track of the amount
	 of time that has passed and fires the ticks needed to run the game. It
	 also fires the command to render out the screen. */
	public void run() {
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		// Nanoseconds per Tick
		double nsPerTick = 1000000000.0 / 60; // There are 60 ticks per second.
		int frames = 0;
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis(); // current time in milliseconds.

		init(); // preps the game by setting up the screens and colors.

		while (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick; //figures out the processed time between now and last Time.
			lastTime = now;
			boolean shouldRender = true;
			while (unprocessed >= 1) { // If there is unprocessed time, then tick.
				ticks++; //increases amount of ticks.
				tick(); // calls the tick method (in which it calls the other tick methods throughout the code.
				unprocessed -= 1; //the method is now processed. so it minuses by 1.
				shouldRender = true; // causes the should render to be true... why is this here since it was already true? whatever.
			}

			try {
				Thread.sleep(2);//makes a small pause for 2 milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldRender) {
				frames++; // increases the amount of frames
				render(); // renders the screen
			}

			if (System.currentTimeMillis() - lastTimer1 > 1000) { //updates every 1 second
				lastTimer1 += 1000;//adds a second to the timer
				System.out.println(ticks + " ticks, " + frames + " fps"); // prints out the number of ticks, and the amount of frames to the console.
				frames = 0;// resets the frames value.
				ticks = 0;// resets the ticks value.
			}
		}
	}
	
	/** The tick method is the updates that happen in the game, there are 60 ticks that happen per second. */
	public void tick() {
		tickCount++; //increases tickCount by 1, not really used for anything.
		if (!hasFocus()) {
			input.releaseAll(); // If the player is not focused on the screen, then all the current inputs will be set to off (well up).
		} else {
			if (!player.removed && !hasWon) gameTime++; //increases tickCount by 1, this is used for the timer on the death screen.

			input.tick(); // calls the tick() method in InputHandler.java
			if (menu != null) { 
				menu.tick(); // If there is a menu active, it will call the tick method of that menu.
			} else {
				if (player.removed) {
					playerDeadTime++;
					if (playerDeadTime > 60) {
						setMenu(new DeadMenu()); // If the player has been removed and a second has passed, then set the menu to the dead menu.
					}
				} else {
					if (pendingLevelChange != 0) {
						setMenu(new LevelTransitionMenu(pendingLevelChange)); //if the player hits a stairs, then a screen transition menu will appear.
						pendingLevelChange = 0;
					}
				}
				if (wonTimer > 0) {
					if (--wonTimer == 0) {
						setMenu(new WonMenu()); // if the wonTimer is above 0, this will be called and if it hits 0 then it actives the win menu.
					}
				}
				level.tick(); // calls the tick() method in Level.java
				Tile.tickCount++; // increases the tickCount in Tile.java. Used for Water.java and Lava.java.
			}
		}
	}

	/** This method changes the level that the player is currently on.
	 * It takes 1 integer variable, which is used to tell the game which direction to go.
	 * For example, 'changeLevel(1)' will make you go up a level, while 'changeLevel(-1)' will make you go down a level.
	 */
	public void changeLevel(int dir) {
		level.remove(player); // removes the player from the current level.
		currentLevel += dir; // changes the current level by the amount
		level = levels[currentLevel]; // sets the level to the current level
		player.x = (player.x >> 4) * 16 + 8; //sets the player's x coord (to center yourself on the stairs)
		player.y = (player.y >> 4) * 16 + 8; //sets the player's y coord (to center yourself on the stairs)
		level.add(player); // adds the player to the level.
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}

		int xScroll = player.x - screen.w / 2;
		int yScroll = player.y - (screen.h - 8) / 2;
		if (xScroll < 16) xScroll = 16;
		if (yScroll < 16) yScroll = 16;
		if (xScroll > level.w * 16 - screen.w - 16) xScroll = level.w * 16 - screen.w - 16;
		if (yScroll > level.h * 16 - screen.h - 16) yScroll = level.h * 16 - screen.h - 16;
		if (currentLevel > 3) {
			int col = Color.get(20, 20, 121, 121);
			for (int y = 0; y < 14; y++)
				for (int x = 0; x < 24; x++) {
					screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
				}
		}

		level.renderBackground(screen, xScroll, yScroll);
		level.renderSprites(screen, xScroll, yScroll);

		if (currentLevel < 3) {
			lightScreen.clear(0);
			level.renderLight(lightScreen, xScroll, yScroll);
			screen.overlay(lightScreen, xScroll, yScroll);
		}

		renderGui();

		if (!hasFocus()) renderFocusNagger();

		for (int y = 0; y < screen.h; y++) {
			for (int x = 0; x < screen.w; x++) {
				int cc = screen.pixels[x + y * screen.w];
				if (cc < 255) pixels[x + y * WIDTH] = colors[cc];
			}
		}

		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());

		int ww = WIDTH * 3;
		int hh = HEIGHT * 3;
		int xo = (getWidth() - ww) / 2;
		int yo = (getHeight() - hh) / 2;
		g.drawImage(image, xo, yo, ww, hh, null);
		g.dispose();
		bs.show();
	}

	private void renderGui() {
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 20; x++) {
				screen.render(x * 8, screen.h - 16 + y * 8, 0 + 12 * 32, Color.get(000, 000, 000, 000), 0);
			}
		}

		for (int i = 0; i < 10; i++) {
			if (i < player.health)
				screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 200, 500, 533), 0);
			else
				screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 100, 000, 000), 0);

			if (player.staminaRechargeDelay > 0) {
				if (player.staminaRechargeDelay / 4 % 2 == 0)
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 555, 000, 000), 0);
				else
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
			} else {
				if (i < player.stamina)
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 220, 550, 553), 0);
				else
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
			}
		}
		if (player.activeItem != null) {
			player.activeItem.renderInventory(screen, 10 * 8, screen.h - 16);
		}

		if (menu != null) {
			menu.render(screen);
		}
	}

	private void renderFocusNagger() {
		String msg = "Click to focus!";
		int xx = (WIDTH - msg.length() * 8) / 2;
		int yy = (HEIGHT - 8) / 2;
		int w = msg.length();
		int h = 1;

		screen.render(xx - 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
		screen.render(xx + w * 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
		screen.render(xx - 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
		screen.render(xx + w * 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3);
		for (int x = 0; x < w; x++) {
			screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
			screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
		}
		for (int y = 0; y < h; y++) {
			screen.render(xx - 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
			screen.render(xx + w * 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
		}

		if ((tickCount / 20) % 2 == 0) {
			Font.draw(msg, screen, xx, yy, Color.get(5, 333, 333, 333));
		} else {
			Font.draw(msg, screen, xx, yy, Color.get(5, 555, 555, 555));
		}
	}

	public void scheduleLevelChange(int dir) {
		pendingLevelChange = dir;
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		JFrame frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(game, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.start();
	}

	public void won() {
		wonTimer = 60 * 3;
		hasWon = true;
	}
}