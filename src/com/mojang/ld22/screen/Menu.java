package com.mojang.ld22.screen;

import java.util.List;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class Menu {
	protected Game game; // game object used in the menu classes.
	protected InputHandler input; // input object used in the menu classes.

	/** Initialization step, adds the game & input objects. */
	public void init(Game game, InputHandler input) {
		this.input = input;
		this.game = game;
	}

	/** update method used in menus */
	public void tick() {
	}

	/** render method used in menus */
	public void render(Screen screen) {
	}

	/** renders items in a list, used for the inventory, crafting, and chest menus */
	public void renderItemList(Screen screen, int xo, int yo, int x1, int y1, List<? extends ListItem> listItems, int selected) {
		boolean renderCursor = true;// Renders the ">" "<" arrows between a name.
		if (selected < 0) {
			selected = -selected - 1; // If the selected is smaller than 0, then it will add one.
			renderCursor = false; // doesn't render the arrows between the name
		}
		int w = x1 - xo; // Width variable determined by given values
		int h = y1 - yo - 1; // Height variable determined by given values
 		int i0 = 0; // Beginning variable of the list loop
		int i1 = listItems.size(); // End variable of the list loop
		if (i1 > h) i1 = h; // If the end variable is larger than the height variable, then end variable will equal height variable.
		int io = selected - h / 2; // Middle of the list, (selected item). For scrolling effect
		if (io > listItems.size() - h) io = listItems.size() - h; //if the middle is coming near the bottom, then the selected will change.
		if (io < 0) io = 0; // if the middle is coming near the top, then the selected will change

		for (int i = i0; i < i1; i++) {
			listItems.get(i + io).renderInventory(screen, (1 + xo) * 8, (i + 1 + yo) * 8); // this will render all the items in the inventory
		}

		if (renderCursor) {
			int yy = selected + 1 - io + yo; // the y position of the currently selected item
			Font.draw(">", screen, (xo + 0) * 8, yy * 8, Color.get(5, 555, 555, 555)); // draws the left cursor next to the name
			Font.draw("<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555)); // draws the right cursor next to the name
		}
	}
}
