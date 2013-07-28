package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class HoleTile extends Tile {
	public HoleTile(int id) {
		super(id);
		connectsToSand = true; // this tile can connect to sand
		connectsToWater = true; // this tile can connect to water
		connectsToLava= true; // this tile can connect to lava
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(111, 111, 110, 110); // the main color for the hole
		int transitionColor1 = Color.get(3, 111, level.dirtColor - 111, level.dirtColor); // the transition color for dirt
		int transitionColor2 = Color.get(3, 111, level.sandColor - 110, level.sandColor); // the transition color for sand

		boolean u = !level.getTile(x, y - 1).connectsToLiquid(); // Checks if the tile above can NOT connect to a liquid
		boolean d = !level.getTile(x, y + 1).connectsToLiquid(); // Checks if the tile above can NOT connect to a liquid
		boolean l = !level.getTile(x - 1, y).connectsToLiquid(); // Checks if the tile above can NOT connect to a liquid
		boolean r = !level.getTile(x + 1, y).connectsToLiquid(); // Checks if the tile above can NOT connect to a liquid

		boolean su = u && level.getTile(x, y - 1).connectsToSand; // Checks u, and sees if the tile above can connect to sand
		boolean sd = d && level.getTile(x, y + 1).connectsToSand; // Checks d, and sees if the tile down can connect to sand 
		boolean sl = l && level.getTile(x - 1, y).connectsToSand; // Checks l, and sees if the tile to the left can connect to sand 
		boolean sr = r && level.getTile(x + 1, y).connectsToSand; // Checks r, and sees if the tile to the right can connect to sand 

		if (!u && !l) { // if the tile to the left, and the tile above can connect to liquids...
			screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0); // renders the top-left part of the tile
		} else
			/* Renders the the top-left part with a corner depending on if the tile is grass or sand */
			screen.render(x * 16 + 0, y * 16 + 0, (l ? 14 : 15) + (u ? 0 : 1) * 32, (su || sl) ? transitionColor2 : transitionColor1, 0);

		if (!u && !r) { // if the tile to the right, and the tile above can connect to liquids...
			screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0); // renders the top-right part of the tile
		} else
			/* Renders the the top-right part with a corner depending on if the tile is grass or sand */
			screen.render(x * 16 + 8, y * 16 + 0, (r ? 16 : 15) + (u ? 0 : 1) * 32, (su || sr) ? transitionColor2 : transitionColor1, 0);

		if (!d && !l) { // if the tile to the left, and the tile below can connect to liquids...
			screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0); // renders the bottom-left part of the tile
		} else
			/* Renders the the top-right part with a corner depending on if the tile is grass or sand */
			screen.render(x * 16 + 0, y * 16 + 8, (l ? 14 : 15) + (d ? 2 : 1) * 32, (sd || sl) ? transitionColor2 : transitionColor1, 0);
		
		if (!d && !r) { // if the tile to the right, and the tile below can connect to liquids...
			screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0); // renders the bottom-right part of the tile
		} else
			/* Renders the the top-right part with a corner depending on if the tile is grass or sand */
			screen.render(x * 16 + 8, y * 16 + 8, (r ? 16 : 15) + (d ? 2 : 1) * 32, (sd || sr) ? transitionColor2 : transitionColor1, 0);
	}

	/** Determines if the entity can pass this tile */
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e.canSwim(); // if the entity can swim, then he can pass
	}

}
