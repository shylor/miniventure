package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class FlowerTile extends GrassTile {
	public FlowerTile(int id) {
		super(id); // this is here so that errors won't yell at us. Calls the GrassTile.java part of this class
		tiles[id] = this; // assigns the id
		connectsToGrass = true; // this tile can connect to grass.
	}

	/** Draws the tile on the screen */
	public void render(Screen screen, Level level, int x, int y) {
	super.render(screen, level, x, y); // calls the render method of GrassTile.java

	int data = level.getData(x, y); // gets the data of this tile
	int shape = (data / 16) % 2; // gets the shape of this tile. shape = the remainder of ((data/16) / 2)
	int flowerCol = Color.get(10, level.grassColor, 555, 440); // color of the flower

	if (shape == 0) screen.render(x * 16 + 0, y * 16 + 0, 1 + 1 * 32, flowerCol, 0); // if shape is equal to 0, then render it at the top-left part of the grass tile.
	if (shape == 1) screen.render(x * 16 + 8, y * 16 + 0, 1 + 1 * 32, flowerCol, 0); // if shape is equal to 1, then render it at the top-right part of the grass tile.
	if (shape == 1) screen.render(x * 16 + 0, y * 16 + 8, 1 + 1 * 32, flowerCol, 0); // if shape is equal to 1, then render it at the bottom-left part of the grass tile.
	if (shape == 0) screen.render(x * 16 + 8, y * 16 + 8, 1 + 1 * 32, flowerCol, 0); // if shape is equal to 0, then render it at the bottom-right part of the grass tile.
	}

	/** What happens when you use an item on the tile */
	public boolean interact(Level level, int x, int y, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { // if the item happens to be a tool...
			ToolItem tool = (ToolItem) item; // converts the Item object into a ToolItem object
			if (tool.type == ToolType.shovel) { // if the type of the tool is a shovel...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
				level.add(new ItemEntity(new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds a flower to the level
				level.add(new ItemEntity(new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds a flowre to the level
				level.setTile(x, y, Tile.grass, 0); // sets the tile to a grass tile
				return true;
				}
			}
		}
		return false;
	}

	/** What happens when you punch the tile */
	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
		int count = random.nextInt(2) + 1; // random count between 1 and 2.
		for (int i = 0; i < count; i++) { // cycles through the count
			level.add(new ItemEntity(new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds a flower to the world
		}
		level.setTile(x, y, Tile.grass, 0); // sets the tile to a grass tile
	}
}