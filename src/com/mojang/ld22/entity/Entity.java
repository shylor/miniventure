package com.mojang.ld22.entity;

import java.util.List;
import java.util.Random;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class Entity {
	protected final Random random = new Random(); // Used for random numbers, example: random.randInt(5)
	public int x, y;// x & y coordinates on the map
	public int xr = 6; // horizontal radius of entity
	public int yr = 6; // vertical radius of entity
	public boolean removed; // Determines if the entity should be removed from the level
	public Level level; // the level that the entity is on

	/** Render method, (Look in the specific entity's class) */
	public void render(Screen screen) {
	}

	/** Update method, (Look in the specific entity's class) */
	public void tick() {
	}

	/** Removes the entity from the world */
	public void remove() {
		removed = true; // sets the removed value to true
	}

	/** Initialization method, called when the entity is created */
	public final void init(Level level) {
		this.level = level; // assigns the level (to this class) that the entity is on
	}

	/** If this entity intersects 4 points then return true */
	public boolean intersects(int x0, int y0, int x1, int y1) {
		/* if (x position + horizontal radius) is NOT smaller than x0 OR... 
		 * if (y position + vertical radius) is NOT smaller than y0 OR... 
		 * if (x position - horizontal radius) is NOT larger than x1 OR... 
		 * if (y position - vertical radius) is NOT larger than y1. Then return true.
		 *  */
		return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1); 
	}

	/** Extended in Mob.java & Furniture.java */
	public boolean blocks(Entity e) {
		return false;
	}

	/** Extended in Mob.java */
	public void hurt(Mob mob, int dmg, int attackDir) {
	}

	/** Extended in Mob.java */
	public void hurt(Tile tile, int x, int y, int dmg) {
	}

	/** Moves an entity with horizontal acceleration, and vertical acceleration */
	public boolean move(int xa, int ya) {
		if (xa != 0 || ya != 0) { // If the horizontal acceleration OR vertical acceleration does NOT equal 0 then...
			boolean stopped = true; // stopped value, used for checking if the entity has stopped.
			if (xa != 0 && move2(xa, 0)) stopped = false; // If the horizontal acceleration and the movement was successful then stopped equals false.
			if (ya != 0 && move2(0, ya)) stopped = false; // If the vertical acceleration and the movement was successful then stopped equals false.
			if (!stopped) { // if stopped equals false then...
				
				/* I guess I should explain something real quick. The coordinates between tiles and entities are different.
				 * The world coordinates for tiles is 128x128
				 * The world coordinates for entities is 2048x2048
				 * This is because each tile is 16x16 pixels big
				 * 128 x 16 = 2048.
				 * When ever you see a ">>", it means that it is a right shift operator. This means it shifts bits to the right (making them smaller)
				 * x >> 4 is the equivalent to x / (2^4). Which means it's dividing the X value by 16. (2x2x2x2 = 16)
				 * xt << 4 is the equivalent to xt * (2^4). Which means it's multiplying the X tile value by 16.
				 * 
				 * These bit shift operators are used to easily get the X & Y coordinates of a tile that the entity is standing on. */
				
				int xt = x >> 4; // the x tile coordinate that the entity is standing on.
				int yt = y >> 4; // the y tile coordinate that the entity is standing on.
				level.getTile(xt, yt).steppedOn(level, xt, yt, this); // Calls the steppedOn() method in a tile's class. (like sand or lava)
			}
			return !stopped; // returns the opposite of stopped
		}
		return true; // returns true
	}

	/** Second part to the move method (moves in one direction at a time) */
	protected boolean move2(int xa, int ya) {
		/* If the x acceleration and y acceleration are BOTH NOT 0, then throw an error */
		if (xa != 0 && ya != 0) throw new IllegalArgumentException("Move2 can only move along one axis at a time!");

		/* Note: I was tired when typing this part, please excuse grammar quirks in the writing. (Or just re-write it to make it more sensible, lol) */
		
		int xto0 = ((x) - xr) >> 4; // gets the tile coordinate of the position to the left of the sprite
		int yto0 = ((y) - yr) >> 4; // gets the tile coordinate of the position to the top of the sprite
		int xto1 = ((x) + xr) >> 4; // gets the tile coordinate of the position to the right of the sprite
		int yto1 = ((y) + yr) >> 4; // gets the tile coordinate of the position to the bottom of the sprite

		int xt0 = ((x + xa) - xr) >> 4; // gets the tile coordinate of the position to the left of the sprite + the horizontal acceleration
		int yt0 = ((y + ya) - yr) >> 4; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
		int xt1 = ((x + xa) + xr) >> 4; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
		int yt1 = ((y + ya) + yr) >> 4; // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
		
		boolean blocked = false; // determines if the next tile can block you.
		for (int yt = yt0; yt <= yt1; yt++) // cycles through yt0 to yt1
			for (int xt = xt0; xt <= xt1; xt++) { // cycles through xt0 to xt1
				/* If...
				 * xt is larger or equal to xto0 AND...
				 * xt is smaller or equal to xto1 AND...
				 * yt is larger or equal to yto0 AND...
				 * yt is smaller or equal to yto1 then, skip the rest of the code and go to the next cycle */
				if (xt >= xto0 && xt <= xto1 && yt >= yto0 && yt <= yto1) continue;
				
				level.getTile(xt, yt).bumpedInto(level, xt, yt, this);  // Calls the bumpedInto function in a tile's class (like cactus)
				if (!level.getTile(xt, yt).mayPass(level, xt, yt, this)) { // If the entity cannot pass this block...
					blocked = true; // blocked value set to true
					return false;  // return false
				}
			}
		if (blocked) return false; // if blocked is equal to true, then return false

		List<Entity> wasInside = level.getEntities(x - xr, y - yr, x + xr, y + yr); // gets all of the entities that are inside this entity (aka: colliding)
		List<Entity> isInside = level.getEntities(x + xa - xr, y + ya - yr, x + xa + xr, y + ya + yr); // gets the entities that this entity will touch.
		for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
			Entity e = isInside.get(i); // current entity in the list
			if (e == this) continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.

			e.touchedBy(this); // calls the touchedBy(entity) method in that entity's class
		}
		isInside.removeAll(wasInside); // removes all the entities that are in the wasInside from the isInside list.
		for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
			Entity e = isInside.get(i); // current entity in the list
			if (e == this) continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.

			if (e.blocks(this)) { // if the entity can block this entity then...
				return false; // return false
			}
		}

		x += xa; // moves horizontally based on the x acceleration
		y += ya; // moves vertically based on the y acceleration
		return true; // return true
	}

	/** if this entity is touched by another entity (extended by sub-classes) */
	protected void touchedBy(Entity entity) {
	}

	/** returns if mobs can block you (aka: can't pass through them) */
	public boolean isBlockableBy(Mob mob) {
		return true; // yes they can block you
	}

	/** Used in ItenEntity.java, extended with Player.java */
	public void touchItem(ItemEntity itemEntity) {
	}

	/** Determines if the entity can swim (extended in sub-classes) */
	public boolean canSwim() {
		return false;
	}

	/** Item interact, used in player.java */
	public boolean interact(Player player, Item item, int attackDir) {
		return item.interact(player, this, attackDir); // calls the interact method in the passes in item
	}

	/** sees if the player has used an item in a direction (extended in player.java) */
	public boolean use(Player player, int attackDir) {
		return false;
	}

	/** Gets the light radius used for lighting up the underground (extended in other classes) */
	public int getLightRadius() {
		return 0;
	}
}