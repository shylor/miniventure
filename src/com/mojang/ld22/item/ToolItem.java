package com.mojang.ld22.item;

import java.util.Random;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class ToolItem extends Item {
	private Random random = new Random();

	public static final int MAX_LEVEL = 5; // How many different levels of tools there are
	public static final String[] LEVEL_NAMES = {
	"Wood", "Rock", "Iron", "Gold", "Gem" // The names of the different levels. Later levels means stronger tool
	};

	public static final int[] LEVEL_COLORS = {// Colors of the tools, same position as LEVEL_NAMES
	Color.get(-1, 100, 321, 431),// Colors for Wood tools
	Color.get(-1, 100, 321, 111),// Colors for Rock/Stone tools
	Color.get(-1, 100, 321, 555),// Colors for Iron tools
	Color.get(-1, 100, 321, 550),// Colors for Gold tools
	Color.get(-1, 100, 321, 055),// Colors for Gem tools
	};

	public ToolType type; // Type of tool (Sword, hoe, axe, pickaxe, shovel)
	public int level = 0; // Level of said tool

	/** Tool Item, requires a tool type (ToolType.sword, ToolType.axe, ToolType.hoe, etc) and a level (0 = wood, 2 = iron, 4 = gem, etc) */
	public ToolItem(ToolType type, int level) {
		this.type = type; //type of tool for this item
		this.level = level; //level of tool for this item
	}
	
	/** Gets the colors for this tool */
	public int getColor() {
		return LEVEL_COLORS[level];
	}

	/** gets the sprite for this tool */
	public int getSprite() {
		return type.sprite + 5 * 32;
	}

	/** Renders the icon for this tool on the screen */
	public void renderIcon(Screen screen, int x, int y) {
		screen.render(x, y, getSprite(), getColor(), 0);
	}
	
	/** Renders the icon & name of this tool for inventory/crafting purposes. */
	public void renderInventory(Screen screen, int x, int y) {
		screen.render(x, y, getSprite(), getColor(), 0);
		Font.draw(getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
	}

	/** Gets the name of this tool (and it's type) */
	public String getName() {
		return LEVEL_NAMES[level] + " " + type.name;
	}
	
	public void onTake(ItemEntity itemEntity) {
	}
	
	/** Can attack mobs with tools. */
	public boolean canAttack() {
		return true; 
	}

	/** Calculates Damage */
	public int getAttackDamageBonus(Entity e) {
		if (type == ToolType.axe) {
			return (level + 1) * 2 + random.nextInt(4); // axes: (level + 1) * 2 + random number beteween 0 and 3, do slightly less damage than swords.
		}
		if (type == ToolType.sword) {
			return (level + 1) * 3 + random.nextInt(2 + level * level * 2); //swords: (level + 1) * 3 + random number between 0 and (2 + level * level * 2)
		}
		return 1;
	}

	/** Sees if this item matches another. */
	public boolean matches(Item item) {
		if (item instanceof ToolItem) {
			ToolItem other = (ToolItem) item;
			if (other.type != type) return false;
			if (other.level != level) return false;
			return true;
		}
		return false;
	}
}
