package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.CraftingMenu;

public class Furnace extends Furniture {
	
	/* This is a sub-class of furniture.java, go there for more info */
	
	public Furnace() {
		super("Furnace"); // Name of the furnace
		col = Color.get(-1, 000, 222, 333); // Color of the furnace
		sprite = 3; // Location of the sprite
		xr = 3; // Width of the furnace (in-game, not sprite) 
		yr = 2; // Height of the furnace (in-game, not sprite) 
	}

	/** This is what occurs when the player uses the "Menu" command near this */
	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.furnaceRecipes, player)); // Sets the menu to the crafting menu with furnace recipes.
		return true;
	}
}