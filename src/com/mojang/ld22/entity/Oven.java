package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.CraftingMenu;

public class Oven extends Furniture {
	
	/* This is a sub-class of furniture.java, go there for more info */
	
	public Oven() {
		super("Oven"); // Name of the oven.
		col = Color.get(-1, 000, 332, 442); // Color of the oven
		sprite = 2; // Location of the sprite
		xr = 3; // Width of the oven 
		yr = 2; // Height of the oven 
	}

	/** This is what occurs when the player uses the "Menu" command near this */
	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.ovenRecipes, player)); // Sets the menu to the crafting menu with oven recipes.
		return true;
	}
}