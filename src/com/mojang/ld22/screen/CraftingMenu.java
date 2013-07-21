package com.mojang.ld22.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mojang.ld22.crafting.Recipe;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.sound.Sound;

public class CraftingMenu extends Menu {
	private Player player; // the player that opened this menu
	private int selected = 0; // current selected item

	private List<Recipe> recipes; // List of recipes used in this menu (workbench, anvil, oven, etc)

	public CraftingMenu(List<Recipe> recipes, Player player) {
		this.recipes = new ArrayList<Recipe>(recipes); // Assigns the recipes
		this.player = player; // the player

		for (int i = 0; i < recipes.size(); i++) {
			this.recipes.get(i).checkCanCraft(player); // Checks if the player can craft the item(s)
		}

		/* This sorts the recipes so that the ones you can craft will appear on top */
		Collections.sort(this.recipes, new Comparator<Recipe>() {
			public int compare(Recipe r1, Recipe r2) {
				if (r1.canCraft && !r2.canCraft) return -1; // if the first item can be crafted while the second can't, the first one will go above in the list
				if (!r1.canCraft && r2.canCraft) return 1; // if the second item can be crafted while the first can't, the second will go over that one.
				return 0; // else don't change position
			}
		});
	}

	public void tick() {
		if (input.menu.clicked) game.setMenu(null); // if the player presses the "Menu" key then it will exit this menu

		if (input.up.clicked) selected--; // If the up key is press then the selection will go up one item
		if (input.down.clicked) selected++; // If the down key is pressed then the selection will go down one item

		int len = recipes.size(); // Size of the recipe list
		if (len == 0) selected = 0; // If the size of the list is 0, then it will stay selected at 0
		if (selected < 0) selected += len; // If the current selection is less than 0 (first entry) than it will loop to the bottom.
		if (selected >= len) selected -= len; // If the current selection goes past the bottom entry, then it will loop to the top.

		if (input.attack.clicked && len > 0) {// If the "Attack" key is pressed and the recipe's size is bigger than 0...
			Recipe r = recipes.get(selected); // The current recipe selected
			r.checkCanCraft(player); // Checks if the player can craft this recipe
			if (r.canCraft) { // If the player can craft the item...
				r.deductCost(player); // Then it will deduct the items from the player's inventory
				r.craft(player); // It will craft (add) the item into the player's inventory
				Sound.craft.play(); // Plays a sound to tell you that a item has been crafted
			}
			for (int i = 0; i < recipes.size(); i++) { // Loops through all the recipes in the list
				recipes.get(i).checkCanCraft(player); // Refreshes the recipe list if the player can now craft a new item.
			}
		}
	}

	public void render(Screen screen) {
		Font.renderFrame(screen, "Have", 12, 1, 19, 3); // renders the 'have' items window
		Font.renderFrame(screen, "Cost", 12, 4, 19, 11); // renders the 'cost' items window
		Font.renderFrame(screen, "Crafting", 0, 1, 11, 11); // renders the main crafting window
		renderItemList(screen, 0, 1, 11, 11, recipes, selected); // renders all the items in the recipe list

		if (recipes.size() > 0) { // If the size of the recipe list is above 0...
			Recipe recipe = recipes.get(selected); // currently selected recipe
			int hasResultItems = player.inventory.count(recipe.resultTemplate); // Counts the number of items to see if you can craft the recipe
			int xo = 13 * 8; // x coordinate of the items in the 'have' and 'cost' windows
			screen.render(xo, 2 * 8, recipe.resultTemplate.getSprite(), recipe.resultTemplate.getColor(), 0); // Renders the sprites in the 'have' & 'cost' windows
			Font.draw("" + hasResultItems, screen, xo + 8, 2 * 8, Color.get(-1, 555, 555, 555)); // draws the amount in the 'have' menu

			List<Item> costs = recipe.costs; // the list items that is needed to make the recipe
			for (int i = 0; i < costs.size(); i++) { // Loops through the costs list
				Item item = costs.get(i); // Current cost item
				int yo = (5 + i) * 8; // y coordinate of the cost item
				screen.render(xo, yo, item.getSprite(), item.getColor(), 0); // renders the cost item
				int requiredAmt = 1; // required amount need to craft (normally 1)
				if (item instanceof ResourceItem) { // If the item is a resource...
					requiredAmt = ((ResourceItem) item).count; // get's the amount needed to craft the item
				}
				int has = player.inventory.count(item); // This is the amount of the resource you have in your inventory
				int color = Color.get(-1, 555, 555, 555); // color in the 'cost' window
				if (has < requiredAmt) { // If the player has less than required of the resource
					color = Color.get(-1, 222, 222, 222); // then change the color to gray.
				}
				if (has > 99) has = 99; // if the player has over 99 of the resource, then just display 99 (for space)
				Font.draw("" + requiredAmt + "/" + has, screen, xo + 8, yo, color); // Draw "#required/#has" text next to the icon 
			}
		}
		
	}
}