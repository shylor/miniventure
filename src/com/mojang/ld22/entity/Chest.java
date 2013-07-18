package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.ContainerMenu;

public class Chest extends Furniture {
	public Inventory inventory = new Inventory(); // Inventory of the chest

	/* This is a sub-class of furniture.java, go there for more info */
	
	public Chest() {
		super("Chest"); //Name of the chest
		col = Color.get(-1, 110, 331, 552); // Color of the chest
		sprite = 1; // Location of the sprite
	}

	/** This is what occurs when the player uses the "Menu" command near this */
	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new ContainerMenu(player, "Chest", inventory)); // Opens up a menu with the player's inventory and the chest's inventory
		return true;
	}
}