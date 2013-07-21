package com.mojang.ld22.gfx;

public class Sprite {
	public int x, y; // coordinates of the sprite
	public int img; // image of the sprite
	public int col; // color of the sprite
	public int bits; // bits of the sprite

	public Sprite(int x, int y, int img, int col, int bits) {
		this.x = x; // assigns the x coordinate
		this.y = y; // assigns the y coordinate
		this.img = img; // assigns the image value
		this.col = col; // assigns the color value
		this.bits = bits; // assigns the bits value
	}
}
