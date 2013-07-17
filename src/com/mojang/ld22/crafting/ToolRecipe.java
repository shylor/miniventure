package com.mojang.ld22.crafting;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;

public class ToolRecipe extends Recipe {
	
	/* Blue text adds description notes to a variable. Hover your mouse over the blue word 'type' or 'level' */
	
	/** The type of tool, example: (ToolType.sword, ToolType.axe, ToolType.hoe) */
	private ToolType type;
	
	/** The level of the tool (0 = wood, 1 = rock/stone, 2 = iron, 3 = gold, */
	private int level;

	/** Adds a recipe to add a tool, input a ToolType and it's level. 
	 Example: ToolRecipe(ToolType.sword , 2) will create a iron sword.
	 */
	public ToolRecipe(ToolType type, int level) {
		super(new ToolItem(type, level)); //this goes through Recipe.java to be put on a list.
		this.type = type; //adds the type
		this.level = level; //adds the level
	}

	/** Crafts the tool into your inventory */
	public void craft(Player player) {
		player.inventory.add(0, new ToolItem(type, level)); //adds the tool into your inventory
	}
}
