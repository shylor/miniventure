package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class FoodResource extends Resource {
	private int heal; // the amount of health the food heals you for.
	private int staminaCost; // the amount of stamina it costs to consume the food.

	public FoodResource(String name, int sprite, int color, int heal, int staminaCost) {
		super(name, sprite, color); // assigns the name, sprite, and color
		this.heal = heal; // assigns the heal amount
		this.staminaCost = staminaCost; // assigns the stamina cost
	}

	/** What happens when the players uses the item on a tile */
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (player.health < player.maxHealth && player.payStamina(staminaCost)) { // If the player's health is less than the max health AND he can pay the stamina
			player.heal(heal); // heal the player.
			return true; // return true
		}
		return false; // return false
	}
}
