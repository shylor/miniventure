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

public class RockTile extends Tile {
	public RockTile(int id) {
		super(id); // assigns the id
	}
	
	Tile t = this; // this tile, (The reason why this is here is for HardRockTile.java)
	int mainColor = 444; // main color of the rock
	int darkColor = 111; // dark color of the rock

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(mainColor, mainColor, mainColor - 111, mainColor - 111); // color of the rock
		int transitionColor = Color.get(darkColor, mainColor, mainColor + 111, level.dirtColor); // transitional color for the rock

		boolean u = level.getTile(x, y - 1) != t; // sees if the tile above this is not a rock tile.
		boolean d = level.getTile(x, y + 1) != t; // sees if the tile below this is not a rock tile.
		boolean l = level.getTile(x - 1, y) != t; // sees if the tile to the left this is not a rock tile.
		boolean r = level.getTile(x + 1, y) != t; // sees if the tile to the right this is not a rock tile.

		boolean ul = level.getTile(x - 1, y - 1) != t; // sees if the tile to the upper-left is not a rock tile.
		boolean dl = level.getTile(x - 1, y + 1) != t; // sees if the tile to the lower-left is not a rock tile.
		boolean ur = level.getTile(x + 1, y - 1) != t; // sees if the tile to the upper-right is not a rock tile.
		boolean dr = level.getTile(x + 1, y + 1) != t; // sees if the tile to the lower-right is not a rock tile.

		if (!u && !l) { // if there is a rock tile above, or to the left of this then...
			if (!ul) // if there is a rock tile to the upper-left of this one then...
				screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);  // render a corner piece. (upper-left sprite)
			else
				screen.render(x * 16 + 0, y * 16 + 0, 7 + 0 * 32, transitionColor, 3); // render a flat tile
		} else
			screen.render(x * 16 + 0, y * 16 + 0, (l ? 6 : 5) + (u ? 2 : 1) * 32, transitionColor, 3); // else render an end piece.

		if (!u && !r) { // if there is a rock tile above, or to the right of this then...
			if (!ur) // if there is a rock tile to the upper-right of this one then...
				screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);  // render a corner piece. (upper-right sprite)
			else
				screen.render(x * 16 + 8, y * 16 + 0, 8 + 0 * 32, transitionColor, 3); // render a flat tile
		} else
			screen.render(x * 16 + 8, y * 16 + 0, (r ? 4 : 5) + (u ? 2 : 1) * 32, transitionColor, 3); // else render an end piece.

		if (!d && !l) { // if there is a rock tile below, or to the left of this then...
			if (!dl) // if there is a rock tile to the lower-left of this one then...
				screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);  // render a corner piece. (lower-left sprite)
			else
				screen.render(x * 16 + 0, y * 16 + 8, 7 + 1 * 32, transitionColor, 3); // render a flat tile
		} else
			screen.render(x * 16 + 0, y * 16 + 8, (l ? 6 : 5) + (d ? 0 : 1) * 32, transitionColor, 3); // else render an end piece.
		if (!d && !r) { // if there is a rock tile below, or to the right of this then...
			if (!dr) // if there is a rock tile to the lower-right of this one then...
				screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);  // render a corner piece. (lower-right sprite)
			else
				screen.render(x * 16 + 8, y * 16 + 8, 8 + 1 * 32, transitionColor, 3); // render a flat tile
		} else
			screen.render(x * 16 + 8, y * 16 + 8, (r ? 4 : 5) + (d ? 0 : 1) * 32, transitionColor, 3); // else render an end piece.
	}

	/** Determines of the player can pass through this tile */
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false; // the player cannot pass through it.
	}

	/** What happens when you punch the tile */
	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
		hurt(level, x, y, dmg); // do a punch amount of damage to it (1-3)
	}

	/** What happens when you use an item in this tile (like a pick-axe) */
	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) { //if the item is a tool
			ToolItem tool = (ToolItem) item; // converts the Item object to a ToolItem object
			if (tool.type == ToolType.pickaxe) { // if the type of tool is a pickaxe...
				if (player.payStamina(4 - tool.level)) { // if the player can pay the stamina...
					hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10); // hurts the tile, damage based on the tool's level.
					return true;
				}
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + dmg; // adds the damage.
		level.add(new SmashParticle(x * 16 + 8, y * 16 + 8)); // creates a smash particle
		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500))); // adds text telling how much damage you did.
		if (damage >= 50) { // if the damage is larger or equal to 50 then...
			int count = random.nextInt(4) + 1; // count is between 1 to 4
			for (int i = 0; i < count; i++) { //loops through the count
				/* adds stone to the world */
				level.add(new ItemEntity(new ResourceItem(Resource.stone), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
			}
			count = random.nextInt(2); // count is between 0 and 1
			for (int i = 0; i < count; i++) { // loops through the count
				/* adds coal to the world */
				level.add(new ItemEntity(new ResourceItem(Resource.coal), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
			}
			level.setTile(x, y, Tile.dirt, 0); // sets the tile to dirt
		} else {
			level.setData(x, y, damage); // else just set the damage data.
		}
	}

	/** Update method */
	public void tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt); // gets the current damage of the tile
		if (damage > 0) level.setData(xt, yt, damage - 1); // if the damage is larger than 0, then minus the current damage by 1.
	}
}
