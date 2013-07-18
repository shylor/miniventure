package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.CraftingMenu;

public class Anvil extends Furniture {
	
	/* This is a sub-class of furniture.java, go there for more info */
	
	public Anvil() {
		super("Anvil"); // Name of the Anvil
		col = Color.get(-1, 000, 111, 222); //Color of the anvil
		sprite = 0; //Sprite location 
		xr = 3; // Width of the anvil (in-game, not sprite) 
		yr = 2; // Height of the anvil (in-game, not sprite)
	}

	/** This is what occurs when the player uses the "Menu" command near this */
	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.anvilRecipes, player)); // Sets the menu to the crafting menu with anvil recipes.
		return true;
	}
}