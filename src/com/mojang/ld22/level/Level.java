package com.mojang.ld22.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Slime;
import com.mojang.ld22.entity.Zombie;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.levelgen.LevelGen;
import com.mojang.ld22.level.tile.Tile;

public class Level {
	private Random random = new Random(); // creates a random object to be used.

	public int w, h; // width and height of the level

	public byte[] tiles; // an array of all the tiles in the world.
	public byte[] data; // an array of the data of the tiles in the world.
	public List<Entity>[] entitiesInTiles; // An array of each entity in each tile in the world

	public int grassColor = 141; // color of grass
	public int dirtColor = 322; // color of dirt
	public int sandColor = 550; // color of sand
	private int depth; // depth level of the level
	public int monsterDensity = 8; // affects the number of monsters that are on the level, bigger the number the less monsters spawn.

	public List<Entity> entities = new ArrayList<Entity>(); // A list of all the entities in the world
	private Comparator<Entity> spriteSorter = new Comparator<Entity>() { // creates a sorter for all the entities to be rendered.
		public int compare(Entity e0, Entity e1) { // compares 2 entities
			if (e1.y < e0.y) return +1; // If the y position of the first entity is less (higher up) than the second entity, then it will be moved up in sorting.
			if (e1.y > e0.y) return -1; // If the y position of the first entity is more (lower) than the second entity, then it will be moved down in sorting.
			return 0; // ends the method
		}
	};

	@SuppressWarnings("unchecked") // @SuppressWarnings ignores the warnings (yellow underline) in this method.
	/** Level which the world is contained in */
	public Level(int w, int h, int level, Level parentLevel) {
		if (level < 0) { // If the level is less than 0...
			dirtColor = 222; // dirt Color will become gray (222)
		}
		this.depth = level; // assigns the depth variable
		this.w = w; // assigns the width
		this.h = h; // assigns the height
		byte[][] maps; // multidimensional array (an array within a array), used for the map

		if (level == 0) // If the level is 0 (surface)...
			maps = LevelGen.createAndValidateTopMap(w, h); // create a surface map for the level
		else if (level < 0) { // if the level is less than 0 (underground)...
			maps = LevelGen.createAndValidateUndergroundMap(w, h, -level); // create a underground map (depending on the level)
			monsterDensity = 4; // lowers the monsterDensity value, which makes more enemies spawn
		} else { // if level is anything else, aka: above 0 (sky) then...
			maps = LevelGen.createAndValidateSkyMap(w, h);  // creates a sky map
			monsterDensity = 4; // lowers the monsterDensity value, which makes more enemies spawn
		}

		tiles = maps[0]; // assigns the tiles in the map
		data = maps[1]; // assigns the data of the tiles

		if (parentLevel != null) { // If the level above this one is not null (aka, not sky)
			for (int y = 0; y < h; y++) // Loops through the height of the map
				for (int x = 0; x < w; x++) { // Loops through the width of the map
					if (parentLevel.getTile(x, y) == Tile.stairsDown) { // If the tile in the level above the current one is a stairs down then...

						setTile(x, y, Tile.stairsUp, 0); // set a stairs up tile in the same position on the current level
						
						Tile tile = Tile.dirt; // assigns a tile to be a dirt
						if(level == 0) tile = Tile.hardRock; // if the level is 0 (surface) then reassign the tile to be a hard rock.
						
						setTile(x - 1, y, tile, 0); // places the tile to the left of the stairs.
						setTile(x + 1, y, tile, 0); // places the tile to the right of the stairs.
						setTile(x, y - 1, tile, 0); // places the tile to the above of the stairs.
						setTile(x, y + 1, tile, 0); // places the tile to the below of the stairs.
						setTile(x - 1, y - 1, tile, 0); // places the tile to the upper-left position of the stairs.
						setTile(x - 1, y + 1, tile, 0); // places the tile to the lower-left position of the stairs.
						setTile(x + 1, y - 1, tile, 0); // places the tile to the upper-right position of the stairs.
						setTile(x + 1, y + 1, tile, 0); // places the tile to the lower-right position of the stairs.
					}

				}
		}

		entitiesInTiles = new ArrayList[w * h]; // Creates a new arrayList with the size of width * height.
		for (int i = 0; i < w * h; i++) { // Loops (width * height) times
			entitiesInTiles[i] = new ArrayList<Entity>(); // Adds a entity list in that tile.
		}
		
		if (level==1) { // If the level is 1 (sky) then...
			AirWizard aw = new AirWizard(); // Create the air wizard
			aw.x = w*8; // set his position to the middle of the map (x-position)
			aw.y = h*8; // set his position to the middle of the map (y-position)
			add(aw); // adds the air wizard to the level
		}
	}

	/** This method renders all the tiles in the game */
	public void renderBackground(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4; // the player horizontal scroll offset.
		int yo = yScroll >> 4; // the player vertical scroll offset.
		int w = (screen.w + 15) >> 4; // width of the screen being rendered
		int h = (screen.h + 15) >> 4; // height of the screen being rendered
		screen.setOffset(xScroll, yScroll); // sets the scroll offsets.
		for (int y = yo; y <= h + yo; y++) { // loops through the vertical positions
			for (int x = xo; x <= w + xo; x++) { // loops through the horizontal positions
				getTile(x, y).render(screen, this, x, y); // renders the tile on the screen
			}
		}
		screen.setOffset(0, 0); // resets the offset.
	}

	private List<Entity> rowSprites = new ArrayList<Entity>(); // list of entities to be rendered

	public Player player; // the player object

	public void renderSprites(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;

		screen.setOffset(xScroll, yScroll);
		for (int y = yo; y <= h + yo; y++) {
			for (int x = xo; x <= w + xo; x++) {
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
				rowSprites.addAll(entitiesInTiles[x + y * this.w]);
			}
			if (rowSprites.size() > 0) {
				sortAndRender(screen, rowSprites);
			}
			rowSprites.clear();
		}
		screen.setOffset(0, 0);
	}

	public void renderLight(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;

		screen.setOffset(xScroll, yScroll);
		int r = 4;
		for (int y = yo - r; y <= h + yo + r; y++) {
			for (int x = xo - r; x <= w + xo + r; x++) {
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
				List<Entity> entities = entitiesInTiles[x + y * this.w];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					int lr = e.getLightRadius();
					if (lr > 0) screen.renderLight(e.x - 1, e.y - 4, lr * 8);
				}
				int lr = getTile(x, y).getLightRadius(this, x, y);
				if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
			}
		}
		screen.setOffset(0, 0);
	}

	private void sortAndRender(Screen screen, List<Entity> list) {
		Collections.sort(list, spriteSorter);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).render(screen);
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
		return Tile.tiles[tiles[x + y * w]];
	}

	public void setTile(int x, int y, Tile t, int dataVal) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		tiles[x + y * w] = t.id;
		data[x + y * w] = (byte) dataVal;
	}

	public int getData(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return 0;
		return data[x + y * w] & 0xff;
	}

	public void setData(int x, int y, int val) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		data[x + y * w] = (byte) val;
	}

	public void add(Entity entity) {
		if (entity instanceof Player) {
			player = (Player) entity;
		}
		entity.removed = false;
		entities.add(entity);
		entity.init(this);

		insertEntity(entity.x >> 4, entity.y >> 4, entity);
	}

	public void remove(Entity e) {
		entities.remove(e);
		int xto = e.x >> 4;
		int yto = e.y >> 4;
		removeEntity(xto, yto, e);
	}

	private void insertEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		entitiesInTiles[x + y * w].add(e);
	}

	private void removeEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		entitiesInTiles[x + y * w].remove(e);
	}

	public void trySpawn(int count) {
		for (int i = 0; i < count; i++) {
			Mob mob;

			int minLevel = 1;
			int maxLevel = 1;
			if (depth < 0) {
				maxLevel = (-depth) + 1;
			}
			if (depth > 0) {
				minLevel = maxLevel = 4;
			}

			int lvl = random.nextInt(maxLevel - minLevel + 1) + minLevel;
			if (random.nextInt(2) == 0)
				mob = new Slime(lvl);
			else
				mob = new Zombie(lvl);

			if (mob.findStartPos(this)) {
				this.add(mob);
			}
		}
	}

	public void tick() {
		trySpawn(1);

		for (int i = 0; i < w * h / 50; i++) {
			int xt = random.nextInt(w);
			int yt = random.nextInt(w);
			getTile(xt, yt).tick(this, xt, yt);
		}
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			int xto = e.x >> 4;
			int yto = e.y >> 4;

			e.tick();

			if (e.removed) {
				entities.remove(i--);
				removeEntity(xto, yto, e);
			} else {
				int xt = e.x >> 4;
				int yt = e.y >> 4;

				if (xto != xt || yto != yt) {
					removeEntity(xto, yto, e);
					insertEntity(xt, yt, e);
				}
			}
		}
	}

	public List<Entity> getEntities(int x0, int y0, int x1, int y1) {
		List<Entity> result = new ArrayList<Entity>();
		int xt0 = (x0 >> 4) - 1;
		int yt0 = (y0 >> 4) - 1;
		int xt1 = (x1 >> 4) + 1;
		int yt1 = (y1 >> 4) + 1;
		for (int y = yt0; y <= yt1; y++) {
			for (int x = xt0; x <= xt1; x++) {
				if (x < 0 || y < 0 || x >= w || y >= h) continue;
				List<Entity> entities = entitiesInTiles[x + y * this.w];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					if (e.intersects(x0, y0, x1, y1)) result.add(e);
				}
			}
		}
		return result;
	}
}