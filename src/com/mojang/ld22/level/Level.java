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
		int xo = xScroll >> 4; // the game's horizontal scroll offset.
		int yo = yScroll >> 4; // the game's vertical scroll offset.
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

	/** Renders all the entity sprites on the screen */
	public void renderSprites(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4; // the game's horizontal scroll offset.
		int yo = yScroll >> 4; // the game's vertical scroll offset.
		int w = (screen.w + 15) >> 4; // width of the screen being rendered
		int h = (screen.h + 15) >> 4; // height of the screen being rendered

		screen.setOffset(xScroll, yScroll); // sets the scroll offsets.
		for (int y = yo; y <= h + yo; y++) { // loops through the vertical positions
			for (int x = xo; x <= w + xo; x++) { // loops through the horizontal positions
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue; // If the x & y positions of the sprites are within the map's boundaries
				rowSprites.addAll(entitiesInTiles[x + y * this.w]); // adds all of the sprites in the entitiesInTiles array.
			}
			if (rowSprites.size() > 0) { // If the rowSprites list size is larger than 0...
				sortAndRender(screen, rowSprites); // sorts and renders the sprites on the screen
			}
			rowSprites.clear(); // clears the list
		}
		screen.setOffset(0, 0); // resets the offset.
	}

	/** Renders the light off tiles and entities in the underground */
	public void renderLight(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4; // the game's horizontal scroll offset.
		int yo = yScroll >> 4; // the game's vertical scroll offset.
		int w = (screen.w + 15) >> 4; // width of the screen being rendered
		int h = (screen.h + 15) >> 4; // height of the screen being rendered

		screen.setOffset(xScroll, yScroll); // sets the scroll offsets.
		int r = 4; // radius that plays a part of how far away you can be before light stops rendering
		for (int y = yo - r; y <= h + yo + r; y++) { // loops through the vertical positions + r
			for (int x = xo - r; x <= w + xo + r; x++) { // loops through the horizontal positions + r
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue; // If the x & y positions of the sprites are within the map's boundaries
				List<Entity> entities = entitiesInTiles[x + y * this.w]; // gets all the entities in the level
				for (int i = 0; i < entities.size(); i++) { // loops through the list of entities
					Entity e = entities.get(i); // gets the current entity
					int lr = e.getLightRadius(); // gets the light radius from the entity.
					if (lr > 0) screen.renderLight(e.x - 1, e.y - 4, lr * 8); // If the light radius is above 0, then render the light.
				}
				int lr = getTile(x, y).getLightRadius(this, x, y); // gets the light radius from local tiles (like lava)
				if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8); // if the light radius is above 0, then render the light.
			}
		}
		screen.setOffset(0, 0); // resets the offset.
	}

	/** Sorts and renders sprites from an entity list */
	private void sortAndRender(Screen screen, List<Entity> list) {
		Collections.sort(list, spriteSorter); // sorts the list by the spriteSorter
		for (int i = 0; i < list.size(); i++) { // loops through the entity list
			list.get(i).render(screen); // renders the sprite on the screen
		}
	}

	/** Gets a tile from the world. */
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock; // If the tile request is outside the world's boundaries (like x = -5), then returns a rock.
		return Tile.tiles[tiles[x + y * w]]; // Returns the tile that is at the position
	}

	/** Sets a tile to the world */
	public void setTile(int x, int y, Tile t, int dataVal) {
		if (x < 0 || y < 0 || x >= w || y >= h) return; // If the tile request position is outside the world boundaries (like x = -1337), then stop the method.
		tiles[x + y * w] = t.id; // Places the tile at the x & y location
		data[x + y * w] = (byte) dataVal; // sets the data value of the tile
	}

	/** Gets the data from the x & y position */
	public int getData(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return 0; // If the data request position is outside the world boundaries, then stop the method.
		return data[x + y * w] & 0xff; // Returns the last 8 bits(& 0xff) of the data from that position.
	}

	/** Sets a data to the x & y positioned tile */
	public void setData(int x, int y, int val) {
		if (x < 0 || y < 0 || x >= w || y >= h) return; // If the data request position is outside the world boundaries, then stop the method.
		data[x + y * w] = (byte) val; // sets the data as a byte (8-bits) for the data.
	}

	/** Adds a entity to the level */
	public void add(Entity entity) {
		if (entity instanceof Player) { // if the entity happens to be a player
			player = (Player) entity; // the player object will be this entity
		}
		entity.removed = false; // sets the entity's removed value to false
		entities.add(entity); // adds the entity to the entities list
		entity.init(this); // Initializes the entity

		insertEntity(entity.x >> 4, entity.y >> 4, entity); // inserts the entity into the world
	}

	/** Removes a entity */
	public void remove(Entity e) { 
		entities.remove(e); // removes the entity from the entities list
		int xto = e.x >> 4; // gets the x position of the entity
		int yto = e.y >> 4; // gets the y position of the entity
		removeEntity(xto, yto, e); // removes the entity based on the x & y position.
	}

	/** Inserts an entity to the entitiesInTiles list */
	private void insertEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return; // if the entity's position is outside the world, then stop the method.
		entitiesInTiles[x + y * w].add(e); // adds the entity to the entitiesInTiles list array.
	}

	/** Removes an entity in the entitiesInTiles list */
	private void removeEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return; // if the entity's position is outside the world, then stop the method.
		entitiesInTiles[x + y * w].remove(e); // removes the entity to the entitiesInTiles list array.
	}

	/** Tries to spawn an entity in the world */
	public void trySpawn(int count) {
		for (int i = 0; i < count; i++) { // Loops through the count
			Mob mob; // the mob to be spawned

			int minLevel = 1; // min level (green,red,black colored mob)
			int maxLevel = 1; // max level (green,red,black colored mob)
			if (depth < 0) { // if the depth is smaller than 0...
				maxLevel = (-depth) + 1; // the max level changes depending of the depth
			}
			if (depth > 0) { // if the depth is larger than 0...
				minLevel = maxLevel = 4; // the max level and the min level are 4.
			}

			int lvl = random.nextInt(maxLevel - minLevel + 1) + minLevel; // the lvl that the mob will be.
			if (random.nextInt(2) == 0) // if a random variable (0 to 1) is equal to 0 then...
				mob = new Slime(lvl); // mob will be a slime
			else
				mob = new Zombie(lvl); // else it will be a zombie

			if (mob.findStartPos(this)) { // If the mob can find a start position...
				this.add(mob); // then add the mob to the world.
			}
		}
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick() {
		trySpawn(1); // tries to spawn 1 mob.
		
		for (int i = 0; i < w * h / 50; i++) { // Loops (Width * Height / 50) times
			int xt = random.nextInt(w); // Finds a random value from 0 to (Width - 1)
			int yt = random.nextInt(h); // Finds a random value from 0 to (Height - 1)
			getTile(xt, yt).tick(this, xt, yt); // updates the tile at that location.
		}
		for (int i = 0; i < entities.size(); i++) { // Loops through all the entities inside the entities list
			Entity e = entities.get(i); // the current entity
			int xto = e.x >> 4; // gets the entity's x coordinate
			int yto = e.y >> 4; // gets the entity's y coordinate

			e.tick(); // calls the entity's tick() method.

			if (e.removed) { // if the entity's removed value is true...
				entities.remove(i--); // removes the entity from the entities list and makes the list smaller.
				removeEntity(xto, yto, e); // Removes the entity from the world
			} else { // if the entity's removed value is false...
				int xt = e.x >> 4; // gets the entity's x coordinate
				int yt = e.y >> 4; // gets the entity's y coordinate

				if (xto != xt || yto != yt) { // If xto and xt, & yto and yt don't match... 
					removeEntity(xto, yto, e); // remove the entity from xto & yto position 
					insertEntity(xt, yt, e); // adds the entity at the xt & yt position
				}
			}
		}
	}

	/** Gets all the entities from a square area of 4 points. */
	public List<Entity> getEntities(int x0, int y0, int x1, int y1) {
		List<Entity> result = new ArrayList<Entity>(); // result list of entities
		int xt0 = (x0 >> 4) - 1; // location of x0
		int yt0 = (y0 >> 4) - 1; // location of y0
		int xt1 = (x1 >> 4) + 1; // location of x1
		int yt1 = (y1 >> 4) + 1; // location of y1
		for (int y = yt0; y <= yt1; y++) { // Loops through the difference between y0 and y1
			for (int x = xt0; x <= xt1; x++) { // Loops through the difference between x0 & x1
				if (x < 0 || y < 0 || x >= w || y >= h) continue; // if the x & y position is outside the world, then skip the rest of this loop.
				List<Entity> entities = entitiesInTiles[x + y * this.w]; // gets the entity from the x & y position
				for (int i = 0; i < entities.size(); i++) { // Loops through all the entities in the entities list
					Entity e = entities.get(i); // gets the current entity
					if (e.intersects(x0, y0, x1, y1)) result.add(e); // if the entity intersects these 4 points, then add it to the result list.
				}
			}
		}
		return result; // returns the result list of entities
	}
}