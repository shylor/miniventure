package com.mojang.ld22.screen;

import com.mojang.ld22.entity.Inventory;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class ContainerMenu extends Menu {
	private Player player; // The player that is looking inside the chest
	private Inventory container; // The inventory of the chest
	private int selected = 0; // The selected item
	private String title; // The title of the chest
	private int oSelected; // the old selected option (this is used to temporarily save spots moving from chest to inventory & vice-versa)
	private int window = 0; // currently selected window (player's inventory, or chest's inventory)

	/** The container menu class is used for chests */
	public ContainerMenu(Player player, String title, Inventory container) {
		this.player = player; // Player looking inside the chest
		this.title = title; // Title of the chest
		this.container = container; // Inventory of the chest
	}

	public void tick() {
		if (input.menu.clicked) game.setMenu(null); // If the player selects the "menu" key, then it will exit the chest

		if (input.left.clicked) { //if the left key is pressed...
			window = 0; // The current window will be of the chest
			int tmp = selected; // temp integer will be the currently selected
			selected = oSelected; // selected will become the oSelected
			oSelected = tmp; // oSelected will become the temp integer (save spot for when you switch)
		}
		if (input.right.clicked) { //if the right key is pressed...
			window = 1; // The current window will be of the player's inventory
			int tmp = selected; // temp integer will be the currently selected
			selected = oSelected; // selected will become the oSelected
			oSelected = tmp; // oSelected will become the temp integer (save spot for when you switch)
		}

		/* Below is an example of the "ternary operator"  
		  
		  which is read like (example): 
		  'boolean statement ? true result : false result;'
		  
		  It's the exact same thing as:
		  
		  if (boolean statement) {
    		true result;
			} else {
    		false result;
		  }
		  
		  It's just for convince sake, Google " ternary operator " for more info
		  
		 * */
		
		Inventory i = window == 1 ? player.inventory : container; // If the window is equal to 1, then the main inventory is the player inventory, else it's the chest's
		Inventory i2 = window == 0 ? player.inventory : container; // If the window is equal to 0, then the backup inventory is the player inventory, else it's the chest's

		int len = i.items.size(); // Size of the main inventory
		if (input.up.clicked) selected--; // If the up key is press then the selection will go up one item
		if (input.down.clicked) selected++; // If the down key is pressed then the selection will go down one item

		if (len == 0) selected = 0; // If the size of the inventory is 0, then it will stay selected at 0
		if (selected < 0) selected += len; // If the current selection is less than 0 (first entry) than it will loop to the bottom.
		if (selected >= len) selected -= len; // If the current selection goes past the bottom entry, then it will loop to the top.

		if (input.attack.clicked && len > 0) { // If the "Attack" key is pressed and the inventory's size is bigger than 0...
			i2.add(oSelected, i.items.remove(selected)); // It will add the item to the new inventory, and remove it from the old one.
			if (selected >= i.items.size()) selected = i.items.size() - 1; // This fixes the selected item to the latest one.
		}
	}

	public void render(Screen screen) {
		if (window == 1) screen.setOffset(6 * 8, 0); // Offsets the windows for when the player's inventory is selected
		Font.renderFrame(screen, title, 1, 1, 12, 11); // Renders the chest's window
		renderItemList(screen, 1, 1, 12, 11, container.items, window == 0 ? selected : -oSelected - 1); // renders all the items from the chest's inventory

		Font.renderFrame(screen, "inventory", 13, 1, 13 + 11, 11); // renders the player's inventory
		renderItemList(screen, 13, 1, 13 + 11, 11, player.inventory.items, window == 1 ? selected : -oSelected - 1); // renders all the items from the player's inventory
		screen.setOffset(0, 0); // Fixes the offset back to normal
	}
}