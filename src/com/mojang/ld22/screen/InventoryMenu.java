package com.mojang.ld22.screen;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;

public class InventoryMenu extends Menu {
	private Player player; // The player that this inventory belongs to
	private int selected = 0; // The selected item of the inventory.

	public InventoryMenu(Player player) {
		this.player = player; // Assigns the player that this inventory belongs to.

		if (player.activeItem != null) { // If the player has an active item, then...
			player.inventory.items.add(0, player.activeItem); // that active item will go into the inventory
			player.activeItem = null; // the player will not have an active item anymore.
		}
	}

	public void tick() {
		if (input.menu.clicked) game.setMenu(null); // If the player presses the "Menu" key, then the game will go back to the normal game

		if (input.up.clicked) selected--; // If the player presses up, then the selection on the list will go up by 1.
		if (input.down.clicked) selected++; // If the player presses down, then the selection on the list will go down by 1.

		int len = player.inventory.items.size(); // Counts how many items are in  your inventory
		if (len == 0) selected = 0; // If your inventory is 0, then the selected item is 0.
		if (selected < 0) selected += len; // If you go past the top item in your inventory, it will loop back to the bottom
		if (selected >= len) selected -= len; // If you go past the bottom item in your inventory, it will loop to the top

		if (input.attack.clicked && len > 0) { // If your inventory is not empty, and the player presses the "Attack" key...
			Item item = player.inventory.items.remove(selected); // The item will be removed from the inventory
			player.activeItem = item; // and that item will be placed as the player's active item
			game.setMenu(null); // the game will go back to the gameplay
		}
	}

	public void render(Screen screen) {
		Font.renderFrame(screen, "inventory", 1, 1, 12, 11); // renders the blue box for the inventory
		renderItemList(screen, 1, 1, 12, 11, player.inventory.items, selected); // renders the icon's and names of all the items in your inventory.
	}
}