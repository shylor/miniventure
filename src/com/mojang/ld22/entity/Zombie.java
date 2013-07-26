package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class Zombie extends Mob {
	private int xa, ya; // x & y acceleration
	private int lvl; // how tough the zombie is
	private int randomWalkTime = 0; //time till next walk

	public Zombie(int lvl) {
		this.lvl = lvl; // level is passed through constructor
		x = random.nextInt(64 * 16); // gives it a random x position anywhere between (0 to 1023) [Tile position (0 to 64)]
		y = random.nextInt(64 * 16); // gives it a random y position anywhere between (0 to 1023) [Tile position (0 to 64)]
		health = maxHealth = lvl * lvl * 10; // Health based on level

	}

	public void tick() {
		super.tick(); // ticks the Entity.java part of this class

		if (level.player != null && randomWalkTime == 0) { // checks if player is on zombies level and if there is no time left on timer
			int xd = level.player.x - x; // gets the horizontal distance between the zombie and the player
			int yd = level.player.y - y; // gets the vertical distance between the zombie and the player 
			if (xd * xd + yd * yd < 50 * 50) { // more evil distance checker code
				xa = 0; // sets direction to nothing
				ya = 0;
				if (xd < 0) xa = -1; // if the horizontal difference is smaller than 0, then the x acceleration will be 1 (negative direction)
				if (xd > 0) xa = +1; // if the horizontal difference is larger than 0, then the x acceleration will be 1
				if (yd < 0) ya = -1; // if the vertical difference is smaller than 0, then the y acceleration will be 1 (negative direction)
				if (yd > 0) ya = +1; // if the vertical difference is larger than 0, then the y acceleration will be 1
			}
		}

		//halp david! I have no idea what the & sign does in maths! Unless it's a bit opereator, in which case I'm rusty
		// Calm down, go google "java bitwise AND operator" for information about this. -David
		int speed = tickTime & 1; // Speed is either 0 or 1 depending on the tickTime
		if (!move(xa * speed, ya * speed) || random.nextInt(200) == 0) { //moves the zombie, doubles as a check to see if it's still moving -OR- random chance out of 200
			randomWalkTime = 60; // sets the not-so-random walk time to 60
			xa = (random.nextInt(3) - 1) * random.nextInt(2); //sets the acceleration to random i.e. idling code
			ya = (random.nextInt(3) - 1) * random.nextInt(2); //sets the acceleration to random i.e. idling code
		}
		if (randomWalkTime > 0) randomWalkTime--;//if walk time is larger than 0, decrement!
	}

	public void render(Screen screen) {
		/* our texture in the png file */
		int xt = 0; // X tile coordinate in the sprite-sheet
		int yt = 14; // Y tile coordinate in the sprite-sheet

		// change the 3 in (walkDist >> 3) to change the time it will take to switch sprites. (bigger number = longer time).
		int flip1 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
		int flip2 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
		
		if (dir == 1) { //if facing up
			xt += 2; //change sprite to up
		}
		if (dir > 1) { // if facing left or down

			flip1 = 0; // controls flipping left and right
			flip2 = ((walkDist >> 4) & 1); // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
			if (dir == 2) { // if facing left
				flip1 = 1; // flip the sprite so it looks like we are facing left
			}
			xt += 4 + ((walkDist >> 3) & 1) * 2; // animation based on walk distance
		}

		/* where to draw the sprite relative to our position */
		int xo = x - 8; // the horizontal location to start drawing the sprite
		int yo = y - 11; // the vertical location to start drawing the sprite

		int col = Color.get(-1, 10, 252, 050); // lvl 1 colour green
		if (lvl == 2) col = Color.get(-1, 100, 522, 050); // lvl 2 colour pink
		if (lvl == 3) col = Color.get(-1, 111, 444, 050); // lvl 3 light gray
		if (lvl == 4) col = Color.get(-1, 000, 111, 020); // lvl 4 dark grey
		if (hurtTime > 0) { // if hurt
			col = Color.get(-1, 555, 555, 555); //make our colour white
		}

		/* Draws the sprite as 4 different 8*8 images instead of one 16*16 image */
		screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1); // draws the top-left tile
		screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1); // draws the top-right tile
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2); // draws the bottom-left tile
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2); // draws the bottom-right tile
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) { // if the entity touches the player
			entity.hurt(this, lvl + 1, dir); // hurts the player, damage is based on lvl.
		}
	}

	protected void die() {
		super.die(); // Parent death call

		int count = random.nextInt(2) + 1; // Random amount of cloth to drop from 1 to 2
		for (int i = 0; i < count; i++) { // loops through the count
			level.add(new ItemEntity(new ResourceItem(Resource.cloth), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5)); // creates cloth
		}

		if (level.player != null) { // if player is on zombie level
			level.player.score += 50 * lvl; // add score for zombie death
		}

	}

}