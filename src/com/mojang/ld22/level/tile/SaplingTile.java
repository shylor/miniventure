package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class SaplingTile extends Tile {
	private Tile onType; // The tile it grows on (Grass/Sand)
	private Tile growsTo; // What the sapling grows into (Tree/Cactus)

	public SaplingTile(int id, Tile onType, Tile growsTo) {
		super(id); // Assigns the id
		this.onType = onType; // Assigns the tile it grows on
		this.growsTo = growsTo; // Assigns the tile it grows into
		connectsToSand = onType.connectsToSand; //Becomes connect to sand if the type it grows on can connect to sand.
		connectsToGrass = onType.connectsToGrass; //Becomes connect to grass if the type it grows on can connect to grass.
		connectsToWater = onType.connectsToWater; //Becomes connect to water if the type it grows on can connect to water.
		connectsToLava = onType.connectsToLava; //Becomes connect to lava if the type it grows on can connect to sand.
	}

	public void render(Screen screen, Level level, int x, int y) {
		onType.render(screen, level, x, y); // Calls the render method of the tile it grows on
		int col = Color.get(10, 40, 50, -1); // Color of the sapling
		screen.render(x * 16 + 4, y * 16 + 4, 11 + 3 * 32, col, 0); // renders the small sprite of the sapling
	}


	public void tick(Level level, int x, int y) {
		int age = level.getData(x, y) + 1; // Gets the data of the age of the sapling
		if (age > 100) {
			level.setTile(x, y, growsTo, 0); //If that age number is above 100 then it grows to the 'growsTo' tile.
		} else {
			level.setData(x, y, age); // If it is equal or smaller than 100, then it will add 1 to the age
		}
	}

	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
		level.setTile(x, y, onType, 0); //If the sapling is hit, then it will turn back (disappear) into the block it grew on.
	}
}