package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.sound.Sound;

public class ItemEntity extends Entity {
	private int lifeTime; // the life time of this entity in the level
	public double xa, ya, za; // the x, y, and z acceleration
	public double xx, yy, zz; // the x, y, and z coordinates
	public Item item; // the item that this entity is based off of.
	private int time = 0; // time it has lasted in the level

	public ItemEntity(Item item, int x, int y) {
		this.item = item; // assigns the item
		xx = this.x = x; // assigns the x coordinate
		yy = this.y = y; // assigns the y coordinate
		xr = 3; // x radius (size)
		yr = 3; // y radius (size)

		zz = 2; // z coordinate
		xa = random.nextGaussian() * 0.3; // random direction for x acceleration
		ya = random.nextGaussian() * 0.2; // random direction for y acceleration
		za = random.nextFloat() * 0.7 + 1; // random direction for z acceleration

		lifeTime = 60 * 10 + random.nextInt(60); // sets the lifetime of the item. min = 600 ticks, max = 629 ticks.
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick() {
		time++; // increases time by 1
		if (time >= lifeTime) { // if the time is larger or equal to lifeTime then...
			remove(); // remove from the world
			return; // skip the rest of the code
		}
		xx += xa; // moves the xx coordinate by the x acceleration
		yy += ya; // moves the yy coordinate by the y acceleration
		zz += za; // moves the zz coordinate by the z acceleration
		if (zz < 0) { //if zz is smaller than 0
			zz = 0; // zz now will be 0
			za *= -0.5; // multiplies the z acceleration by -0.5
			xa *= 0.6; // multiplies the x acceleration by 0.6
			ya *= 0.6; // multiplies the y acceleration by 0.6
		}
		za -= 0.15; // minuses the z acceleration by 0.15
		int ox = x; // x coordinate
		int oy = y; // y coordinate
		int nx = (int) xx; // integer conversion of xx
		int ny = (int) yy; // integer conversion of yy
		int expectedx = nx - x; // the difference of nx and x
		int expectedy = ny - y; // the difference of ny and y
		move(nx - x, ny - y); // moves the ItemEntity
		int gotx = x - ox; // the difference between the new x and ox
		int goty = y - oy; // the difference between the new y and oy
		xx += gotx - expectedx; // new xx position based on the difference between gotx and expectedx
		yy += goty - expectedy; // new yy position based on the difference between goty and expectedy
	}

	public boolean isBlockableBy(Mob mob) {
		return false; // mobs cannot block this
	}

	public void render(Screen screen) {
		/* this first part is for the blinking effect */
		if (time >= lifeTime - 6 * 20) { // if time is larger or equal to lifeTime - 6 * 20 then...
			if (time / 6 % 2 == 0) return; // if the remainder of (time/6)/2 = 0 then skip the rest of the code.
		}
		screen.render(x - 4, y - 4, item.getSprite(), Color.get(-1, 0, 0, 0), 0); // render the shadow
		screen.render(x - 4, y - 4 - (int) (zz), item.getSprite(), item.getColor(), 0); // render the item based on the item's sprite and color
	}

	protected void touchedBy(Entity entity) {
		if (time > 30) entity.touchItem(this); // if time is above 30, it will call the touchItem() method in an entity (Player.java)
	}

	/** What happens when the player takes the item */
	public void take(Player player) {
		Sound.pickup.play(); // plays a sound
		player.score++; // increase the player's score by 1
		item.onTake(this); // calls the onTake() method in Item.java
		remove(); // removes this from the world
	}
}
