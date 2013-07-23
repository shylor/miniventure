package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class InfiniteFallTile extends Tile {
	
	/* This will be easy :D */
	
	public InfiniteFallTile(int id) {
		super(id); // assigns the id
	}

	/** Infinite fall tile doesn't render anything! */
	public void render(Screen screen, Level level, int x, int y) {
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick(Level level, int xt, int yt) {
	}

	/** Determines if an entity can pass through this tile */
	public boolean mayPass(Level level, int x, int y, Entity e) {
		if (e instanceof AirWizard) return true; // If the entity is an Air Wizard, than it can pass through
		return false; // else the entity can't pass through
	}
}
