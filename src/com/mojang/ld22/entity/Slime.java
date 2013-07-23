package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class Slime extends Mob {
	private int xa, ya; // x & y acceleration
	private int jumpTime = 0; // jumpTimer, also acts as a rest timer before the next jump
	private int lvl; // how tough the slime is

	public Slime(int lvl) {
		this.lvl = lvl; // level is passed through constructor
		x = random.nextInt(64 * 16); // give it a random position
		y = random.nextInt(64 * 16);
		health = maxHealth = lvl * lvl * 5; // Health based on level
	}

	public void tick() {
		super.tick(); // ticks the Entity.java part of this class

		int speed = 1; // the speed of the slime/ length of jump
		if (!move(xa * speed, ya * speed) || random.nextInt(40) == 0) { //moves the slime... doubles as a check to see if it's still moving -OR- random chance out of 40
			if (jumpTime <= -10) { // if jump is equal or less than ten
				xa = (random.nextInt(3) - 1); // Sets direction randomly from -1 to 1
				ya = (random.nextInt(3) - 1);

				if (level.player != null) { // if player exists on my level
					int xd = level.player.x - x; // get the distance between slime and the player
					int yd = level.player.y - y;
					if (xd * xd + yd * yd < 50 * 50) { // Notch is an evil man who should be punished for this line of code, it seems to test the distance between slime and the player
						if (xd < 0) xa = -1; // set which direction the slime should jump
						if (xd > 0) xa = +1;
						if (yd < 0) ya = -1;
						if (yd > 0) ya = +1;
					}

				}

				if (xa != 0 || ya != 0) jumpTime = 10; // if slime has it's direction, jump!
			}
		}

		jumpTime--; //lower jump time
		if (jumpTime == 0) { // when our jump has ended
			xa = ya = 0; // reset direction to 0
		}
	}

	protected void die() {
		super.die(); // Parent death call

		int count = random.nextInt(2) + 1; // Random amount of slime(item) to drop
		for (int i = 0; i < count; i++) {
			level.add(new ItemEntity(new ResourceItem(Resource.slime), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5)); //creates slime items
		}

		if (level.player != null) { // if player exists on my level
			level.player.score += 25*lvl; // add score for slime death
		}
		
	}

	public void render(Screen screen) {
		int xt = 0; //our texture in the png file
		int yt = 18;

		int xo = x - 8; // where to draw the sprite reletive to our position
		int yo = y - 11;

		if (jumpTime > 0) { // if jumping
			xt += 2; // change sprite
			yo -= 4; // draw sprite a little higher
		}

		int col = Color.get(-1, 10, 252, 555); // lvl 1 colour
		if (lvl == 2) col = Color.get(-1, 100, 522, 555); // lvl 2 colour
		if (lvl == 3) col = Color.get(-1, 111, 444, 555); // lvl 3 colour
		if (lvl == 4) col = Color.get(-1, 000, 111, 224); // lvl 4 colour

		if (hurtTime > 0) { // if hurt
			col = Color.get(-1, 555, 555, 555); // make our colour white
		}

		screen.render(xo + 0, yo + 0, xt + yt * 32, col, 0); // Draws the sprite as 4 different 8*8 images instead of one 16*16 image, really weird, probably an artifact from the zombies and the players render code
		screen.render(xo + 8, yo + 0, xt + 1 + yt * 32, col, 0);
		screen.render(xo + 0, yo + 8, xt + (yt + 1) * 32, col, 0);
		screen.render(xo + 8, yo + 8, xt + 1 + (yt + 1) * 32, col, 0);
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) { // if we touch the player
			entity.hurt(this, lvl, dir); // attack
		}
	}
}