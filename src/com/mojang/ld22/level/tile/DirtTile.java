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

public class DirtTile extends Tile {
	public DirtTile(int id) {
		super(id); //assigns the id
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(level.dirtColor, level.dirtColor, level.dirtColor - 111, level.dirtColor - 111); // Colors of the dirt (more info in level.java)
		screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0); // renders the top-left part of the tile
		screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0); // renders the top-right part of the tile
		screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0); // renders the bottom-left part of the tile
		screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0); // renders the bottom-right part of the tile
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { // if the player's current item is a tool...
			ToolItem tool = (ToolItem) item; // Makes a ToolItem conversion of item.
			if (tool.type == ToolType.shovel) { // if the tool is a shovel...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					level.setTile(xt, yt, Tile.hole, 0); //sets the tile to a hole
					level.add(new ItemEntity(new ResourceItem(Resource.dirt), xt * 16 + random.nextInt(10) + 3, yt * 16 + random.nextInt(10) + 3)); // pops out a dirt resource
					Sound.monsterHurt.play();// sound plays
					return true;
				}
			}
			if (tool.type == ToolType.hoe) { // if the tool is a hoe...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					level.setTile(xt, yt, Tile.farmland, 0); //sets the tile to a FarmTile
					Sound.monsterHurt.play(); //sound plays
					return true;
				}
			}
		}
		return false;
	}
}
