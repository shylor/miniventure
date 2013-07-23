package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
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

public class WheatTile extends Tile {
	public WheatTile(int id) {
		super(id); // assigns the id
	}

	/** Draws the tile to the screen */
	public void render(Screen screen, Level level, int x, int y) {
		int age = level.getData(x, y); // gets the tile's age
		int col = Color.get(level.dirtColor - 121, level.dirtColor - 11, level.dirtColor, 50); // gets the color of the tile
		int icon = age / 10; // gets the icon of the tile based on it's age
		if (icon >= 3) { // if the icon is larger or equal to 3.
			col = Color.get(level.dirtColor - 121, level.dirtColor - 11, 50 + (icon) * 100, 40 + (icon - 3) * 2 * 100); // adds more color based on the icon
			if (age == 50) { // if the age is equal to 50 then...
				col = Color.get(0, 0, 50 + (icon) * 100, 40 + (icon - 3) * 2 * 100); // changes the color again (for fully grown wheat)
			}
			icon = 3; // sets the icon value to 3
		}

		screen.render(x * 16 + 0, y * 16 + 0, 4 + 3 * 32 + icon, col, 0); // renders the top-left part of the tile
		screen.render(x * 16 + 8, y * 16 + 0, 4 + 3 * 32 + icon, col, 0); // renders the top-right part of the tile
		screen.render(x * 16 + 0, y * 16 + 8, 4 + 3 * 32 + icon, col, 1); // renders the bottom-left part of the tile
		screen.render(x * 16 + 8, y * 16 + 8, 4 + 3 * 32 + icon, col, 1); // renders the bottom-right part of the tile
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick(Level level, int xt, int yt) {
		/* random.nextBoolean() gives a random choice between true or false */
		if (random.nextBoolean() == false) return; // if the random boolean is false, then skip the rest of the code

		int age = level.getData(xt, yt); // gets the current age of the tile
		if (age < 50) level.setData(xt, yt, age + 1); // if the age of the tile is under 50, then add 1 to the age.
	}

	/** determines what happens when an item is used in the tile */
	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { // if the item is a tool...
			ToolItem tool = (ToolItem) item; // converts an Item object into a ToolItem object
			if (tool.type == ToolType.shovel) { // if the type is a shovel
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					level.setTile(xt, yt, Tile.dirt, 0); // then set the tile to a dirt tile
					return true;
				}
			}
		}
		return false;
	}

	/** What happens when you step on the tile */
	public void steppedOn(Level level, int xt, int yt, Entity entity) {
		if (random.nextInt(60) != 0) return; // if a random number between 0 and 59 does NOT equal 0, then skip the rest of this code
		if (level.getData(xt, yt) < 2) return; // if the age of this tile is less than 2, then skip the rest of this code
		harvest(level, xt, yt); // harvest the tile
	}

	/** What happens when you punch the tile */
	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
		harvest(level, x, y); // harvest the tile
	}

	private void harvest(Level level, int x, int y) {
		int age = level.getData(x, y); // gets the current age of the tile

		int count = random.nextInt(2); // creates a random amount from 0 to 1 
		for (int i = 0; i < count; i++) { // cycles through the count
			level.add(new ItemEntity(new ResourceItem(Resource.seeds), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds seeds to the world
		}

		count = 0; // reset the count
		if (age == 50) { // if the age is equal to 50 (fully grown) then...
			count = random.nextInt(3) + 2; // count will be anywhere between 2 to 4
		} else if (age >= 40) { // if the age is larger or equal to 40 then...
			count = random.nextInt(2) + 1; // count will be anywhere between 1 and 2
		}
		for (int i = 0; i < count; i++) { // loops through the count
			level.add(new ItemEntity(new ResourceItem(Resource.wheat), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3)); // adds wheat to the world
		}

		level.setTile(x, y, Tile.dirt, 0); // sets the tile to a dirt tile
	}
}
