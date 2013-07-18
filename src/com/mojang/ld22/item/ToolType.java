package com.mojang.ld22.item;

public class ToolType {
	public static ToolType shovel = new ToolType("Shvl", 0); //creates the shovel tool type, the number next to the name deals with the sprite location.
	public static ToolType hoe = new ToolType("Hoe", 1); //creates the hoe tool type, the number next to the name deals with the sprite location.
	public static ToolType sword = new ToolType("Swrd", 2); //creates the sword tool type, the number next to the name deals with the sprite location.
	public static ToolType pickaxe = new ToolType("Pick", 3); //creates the pick tool type, the number next to the name deals with the sprite location.
	public static ToolType axe = new ToolType("Axe", 4); //creates the axe tool type, the number next to the name deals with the sprite location.

	public final String name; // name of the type
	public final int sprite; // sprite location on the spritesheet

	private ToolType(String name, int sprite) {
		this.name = name; // adds the name
		this.sprite = sprite; //adds the sprite location number
	}
}
