package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.sound.Sound;

public class GrassTile extends Tile {
	public GrassTile(int id) {
		super(id); // assigns the id
		connectsToGrass = true; // this tile can connect to grass tiles.
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(level.grassColor, level.grassColor, level.grassColor + 111, level.grassColor + 111); // the color of the grass
		int transitionColor = Color.get(level.grassColor - 111, level.grassColor, level.grassColor + 111, level.dirtColor); // the transition color.

		boolean u = !level.getTile(x, y - 1).connectsToGrass; // sees if the tile above this one can NOT connect to grass.
		boolean d = !level.getTile(x, y + 1).connectsToGrass; // sees if the tile below this one can NOT connect to grass.
		boolean l = !level.getTile(x - 1, y).connectsToGrass; // sees if the tile to the left of this one can NOT connect to grass.
		boolean r = !level.getTile(x + 1, y).connectsToGrass; // sees if the tile to the right of this one can NOT connect to grass.

		if (!u && !l) { // if the tile above and the tile to the left can connect to grass then...
			screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0); // renders a flat grass sprite in the upper-left corner of the sprite.
		} else
			screen.render(x * 16 + 0, y * 16 + 0, (l ? 11 : 12) + (u ? 0 : 1) * 32, transitionColor, 0); // else render a end piece.

		if (!u && !r) {  // if the tile above and the tile to the right can connect to grass then...
			screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0); // renders a flat grass sprite in the upper-right corner of the sprite
		} else
			screen.render(x * 16 + 8, y * 16 + 0, (r ? 13 : 12) + (u ? 0 : 1) * 32, transitionColor, 0); // else render a end piece.

		if (!d && !l) {  // if the tile below and the tile to the left can connect to grass then...
			screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0); // renders a flat grass sprite in the lower-left corner of the sprite
		} else
			screen.render(x * 16 + 0, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transitionColor, 0); // else render a end piece.
		
		if (!d && !r) {  // if the tile below and the tile to the right can connect to grass then...
			screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0); // renders a flat grass sprite in the lower-right corner of the sprite
		} else
			screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transitionColor, 0); // else render a end piece.
	}

	/** Update method, updates (ticks) every 60 seconds. */
	public void tick(Level level, int xt, int yt) {
		int xn = xt; // next x position
		int yn = yt; // next y position

		if (random.nextBoolean()) // makes a random decision of true or false
			xn += random.nextInt(2) * 2 - 1; // if that decision is true, then the next x position = (random value between 0 to 1) * 2 - 1
		else
			yn += random.nextInt(2) * 2 - 1; // if that decision is false, then the next y position = (random value between 0 to 1) * 2 - 1

		if (level.getTile(xn, yn) == Tile.dirt) { // if the next positions are a dirt tile then...
			level.setTile(xn, yn, this, 0); // set that dirt tile to a grass tile
		}
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { // if the item happens to be a tool
			ToolItem tool = (ToolItem) item; // converts the Item object into a ToolItem object.
			
			if (tool.type == ToolType.shovel) { // if the type of tool is a shovel...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					level.setTile(xt, yt, Tile.dirt, 0); // sets the tile to a dirt tile
					Sound.monsterHurt.play(); // plays a sound
					if (random.nextInt(5) == 0) { // if a random value between 0 to 4 equals 0 then...
						//Adds seeds to the world
						level.add(new ItemEntity(new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3, yt * 16 + random.nextInt(10) + 3));
						return true;
					}
				}
			}
			
			if (tool.type == ToolType.hoe) { // if the type of tool is a hoe...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					Sound.monsterHurt.play(); // plays a sound
					if (random.nextInt(5) == 0) { // if a random value between 0 to 4 equals 0 then...
						//Adds seeds to the world
						level.add(new ItemEntity(new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3, yt * 16 + random.nextInt(10) + 3));
						return true; // skips the rest of the code
					}
					level.setTile(xt, yt, Tile.farmland, 0); // sets the tile to farmland
					return true;
				}
			}
		}
		return false;

	}
}
