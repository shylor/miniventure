package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class TreeTile extends Tile {
	public TreeTile(int id) {
		super(id); // assigns the tile's id
		connectsToGrass = true; // this tile can connect to grass
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(10, 30, 151, level.grassColor); // main top tree color
		int barkCol1 = Color.get(10, 30, 430, level.grassColor); // Lighter bark color
		int barkCol2 = Color.get(10, 30, 320, level.grassColor); // Darker bark color

		boolean u = level.getTile(x, y - 1) == this; // checks if the tile below it is a tree tile.
		boolean l = level.getTile(x - 1, y) == this; // checks if the tile to the left is a tree tile.
		boolean r = level.getTile(x + 1, y) == this; // checks if the tile to the right is a tree tile.
		boolean d = level.getTile(x, y + 1) == this; // checks if the tile above it is a tree tile.
		boolean ul = level.getTile(x - 1, y - 1) == this; // checks if the upper-left tile is a tree tile.
		boolean ur = level.getTile(x + 1, y - 1) == this; // checks if the upper-right tile is a tree tile.
		boolean dl = level.getTile(x - 1, y + 1) == this; // checks if the bottom-left tile is a tree tile.
		boolean dr = level.getTile(x + 1, y + 1) == this; // checks if the bottom-right tile is a tree tile.
		
		if (u && ul && l) { // if there is a tree above, to the left, and to the upper left of this tile then...
			screen.render(x * 16 + 0, y * 16 + 0, 10 + 1 * 32, col, 0); // render a tree tile sprite that will connect to other trees. (top-left)
		} else {
			screen.render(x * 16 + 0, y * 16 + 0, 9 + 0 * 32, col, 0); // else render the normal top-left part of the tree
		}
		if (u && ur && r) { // if there is a tree above, to the right, and to the upper right of this tile then...
			screen.render(x * 16 + 8, y * 16 + 0, 10 + 2 * 32, barkCol2, 0); // render a tree tile sprite that will connect to other trees. (top-right)
		} else {
			screen.render(x * 16 + 8, y * 16 + 0, 10 + 0 * 32, col, 0); // else render the normal top-right part of the tree
		}
		if (d && dl && l) { // if there is a tree below, to the left, and to the bottom left of this tile then...
			screen.render(x * 16 + 0, y * 16 + 8, 10 + 2 * 32, barkCol2, 0); // render a tree tile sprite that will connect to other trees. (bottom-left)
		} else {
			screen.render(x * 16 + 0, y * 16 + 8, 9 + 1 * 32, barkCol1, 0); // else render the normal bottom-left part of the tree
		}
		if (d && dr && r) { // if there is a tree below, to the right, and to the bottom right of this tile then...
			screen.render(x * 16 + 8, y * 16 + 8, 10 + 1 * 32, col, 0); // render a tree tile sprite that will connect to other trees. (bottom-right)
		} else {
			screen.render(x * 16 + 8, y * 16 + 8, 10 + 3 * 32, barkCol2, 0); // else render the normal bottom-right part of the tree
		}
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt); // gets the damage from the tree
		if (damage > 0) level.setData(xt, yt, damage - 1); // if the damage is above 0, then decrease the damage by 1.
		
		// Huh, so trees and cactuses can heal themselves, weird. - David.
	}

	/** Determines if you can pass through the tile */
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false; // You can't walk through a tree, silly.
	}

	/** What happens when you punch the tree. */
	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
		hurt(level, x, y, dmg); // you do damage to it
	}

	/** What happens when you use a item on the tree */
	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { // if the item is a tool...
			ToolItem tool = (ToolItem) item; // converts the Item object to a ToolItem object
			if (tool.type == ToolType.axe) { // if the type of tool is an axe...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina
					hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10); // do extra damage to the tree
					return true;
				}
			}
		}
		return false;
	}

	private void hurt(Level level, int x, int y, int dmg) {
		{
			int count = random.nextInt(10) == 0 ? 1 : 0; //if a random number between 0 to 9 equals 0, then count will equal 1. else it will be 0.
			for (int i = 0; i < count; i++) { // loop through the count
				level.add(new ItemEntity(new ResourceItem(Resource.apple), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));//add an apple to the world
			}
		}
		int damage = level.getData(x, y) + dmg; // adds damage value to the tree's data.
		level.add(new SmashParticle(x * 16 + 8, y * 16 + 8)); // creates a smash particle
		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500))); // creates a text particle to tell how much damage the player did.
		if (damage >= 20) { // if damage is larger than or equal to 0
			int count = random.nextInt(2) + 1; // random number between 1 to 2
			for (int i = 0; i < count; i++) { // cycles through the count
				level.add(new ItemEntity(new ResourceItem(Resource.wood), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds wood to the world
			}
			count = random.nextInt(random.nextInt(4) + 1); // random number between 1 to 4
			for (int i = 0; i < count; i++) { // cycles through the count
				level.add(new ItemEntity(new ResourceItem(Resource.acorn), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds an acorn to the world
			}
			level.setTile(x, y, Tile.grass, 0); // sets the tile to grass
		} else {
			level.setData(x, y, damage); // else it will set the current damage to the tree
		}
	}
}
