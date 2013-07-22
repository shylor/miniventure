package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.sound.Sound;

public class AirWizard extends Mob {
	private int xa, ya; // x & y acceleration
	private int randomWalkTime = 0; // time it takes for him to complete walking
	private int attackDelay = 0; // attack delay variable
	private int attackTime = 0; // attack time variable
	private int attackType = 0; // attack type variable

	public AirWizard() {
		x = random.nextInt(64 * 16); // x position is anywhere between (0 to 1023) [Tile position (0 to 64)]
		y = random.nextInt(64 * 16); // y position is anywhere between (0 to 1023) [Tile position (0 to 64)]
		health = maxHealth = 2000; // health and maxHealth set to 2000.
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick() {
		super.tick(); // ticks the Entity.java part of this class

		if (attackDelay > 0) { // if the attackDelay is larger than 0...
			dir = (attackDelay - 45) / 4 % 4; // the direction of attack.
			dir = (dir * 2 % 4) + (dir / 2); // direction attack changes
			if (attackDelay < 45) { // If the attack delay is lower than 45
				dir = 0; // direction is reset
			}
			attackDelay--; // minus attack delay by 1
			if (attackDelay == 0) { // if attack delay is equal to 0
				attackType = 0; // attack type is set to 0.
				if (health < 1000) attackType = 1; // if the air wizard is at 1000 (50%) health then attackType = 1
				if (health < 200) attackType = 2; // if the air wizard is at 200 (10%) health then attackType = 2
				attackTime = 60 * 2; // attackTime is set to 120 (2 seconds)
			}
			return; // skips the rest of the code
		}

		if (attackTime > 0) { // if the attackTime is larger than 0
			attackTime--; // attackTime will minus by 1.
			double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1); //assigns a local direction variable from the attack time.
			double speed = (0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
			level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed));// adds a spark entity with the cosine and sine of dir times speed.
			return; // skips the rest of the code
		}

		if (level.player != null && randomWalkTime == 0) { // if there is a player around, and the randomWalkTime is equal to 0
			int xd = level.player.x - x; // the horizontal distance between the player and the air wizard.
			int yd = level.player.y - y; // the vertical distance between the player and the air wizard.
			if (xd * xd + yd * yd < 32 * 32) { // if the x-distance² + y-distance² is smaller than 32² then...
				/* Move away from the player */
				xa = 0; // x acceleration
				ya = 0; // y acceleration
				if (xd < 0) xa = +1; // if the xd is less than 0, then increase x acceleration by 1
				if (xd > 0) xa = -1; // if the xd is more than 0, then increase x acceleration by 1 (negative direction)
				if (yd < 0) ya = +1; // if the yd is less than 0, then increase y acceleration by 1
				if (yd > 0) ya = -1; // if the yd is more than 0, then increase y acceleration by 1 (negative direction)
			} else if (xd * xd + yd * yd > 80 * 80) { // if the x-distance² + y-distance² is smaller than 80² then...
				/* Move towards from the player */
				xa = 0; // x acceleration
				ya = 0; // y acceleration
				if (xd < 0) xa = -1; // if the xd is less than 0, then increase x acceleration by 1 (negative direction)
				if (xd > 0) xa = +1; // if the xd is more than 0, then increase x acceleration by 1
				if (yd < 0) ya = -1; // if the yd is less than 0, then increase y acceleration by 1 (negative direction)
				if (yd > 0) ya = +1; // if the yd is more than 0, then increase y acceleration by 1
			}
		}

		int speed = (tickTime % 4) == 0 ? 0 : 1; // if the remainder of tickTime/4 is equal to 0, then speed is equal to 0, else it is equal to 1.
		if (!move(xa * speed, ya * speed) || random.nextInt(100) == 0) { // If the air wizard is not moving & a random value (0 to 99) equals 0...
			randomWalkTime = 30; // randomWalkTime is equal to 30
			xa = (random.nextInt(3) - 1); // x-accelerations equals (random number between 0 to 2) minus 1.
			ya = (random.nextInt(3) - 1); // y-accelerations equals (random number between 0 to 2) minus 1.
		}
		if (randomWalkTime > 0) { // if randomWalkTime is larger than 0...
			randomWalkTime--; // decrease randomWalkTime by 1
			if (level.player != null && randomWalkTime == 0) { // if there is a player around, and the randomWalkTime is equal to 0
				int xd = level.player.x - x; // the horizontal distance between the player and the air wizard.
				int yd = level.player.y - y; // the vertical distance between the player and the air wizard.
				
				/* if a random number (0 to 3) equals 0 and the x-distance² + y-distance² is smaller than 50² then...*/
				if (random.nextInt(4) == 0 && xd * xd + yd * yd < 50 * 50) {
					if (attackDelay == 0 && attackTime == 0) { // if attackDelay & attackTime equal 0, then...
						attackDelay = 60 * 2; // attackDelay equals 120 (about 2 seconds)
					}
				}
			}
		}
	}

	/** Renders the air wizard on the screen */
	public void render(Screen screen) {
		int xt = 8; // x coordinate on the sprite sheet
		int yt = 14; // y coordinate on the sprite sheet

		int flip1 = (walkDist >> 3) & 1; // animation flip value (used to mirror the sprite).
		int flip2 = (walkDist >> 3) & 1; // animation flip value (used to mirror the sprite).

		if (dir == 1) { // if the direction is 1 then...
			xt += 2; // moves the sprite coordinate 2 tiles to the right (16 pixels over)
		}
		if (dir > 1) { // if the direction is larger than 1...

			flip1 = 0; // flip1 becomes 0 (don't mirror)
			flip2 = ((walkDist >> 4) & 1); // changes bottom half of sprite (mirror)
			if (dir == 2) { // if the direction is 2...
				flip1 = 1; // flip1 becomes 1 (mirrored)
			}
			xt += 4 + ((walkDist >> 3) & 1) * 2; // changes bottom half of sprite (in the sprite sheet)
		}

		int xo = x - 8; // the horizontal location to start drawing the sprite
		int yo = y - 11;  // the vertical location to start drawing the sprite

		int col1 = Color.get(-1, 100, 500, 555); // Color used in the top half of the sprite
		int col2 = Color.get(-1, 100, 500, 532); // Color used in the bottom half of the sprite
		if (health < 200) { // if health is above 200
			if (tickTime / 3 % 2 == 0) { // if the remainder of ((tickTime/3)/2) is equal to 0...
				col1 = Color.get(-1, 500, 100, 555); // change colors
				col2 = Color.get(-1, 500, 100, 532); // change colors
			}
		} else if (health < 1000) { // if health is above 200
			if (tickTime / 5 % 4 == 0) { // if the remainder of ((tickTime/5)/4) is equal to 0...
				col1 = Color.get(-1, 500, 100, 555); // change colors
				col2 = Color.get(-1, 500, 100, 532); // change colors
			}
		}
		if (hurtTime > 0) {//if the air wizards hurt time is above 0... (hurtTime value in Mob.java)
			col1 = Color.get(-1, 555, 555, 555); // turn the sprite to white
			col2 = Color.get(-1, 555, 555, 555); // turn the sprite to white
		}

		
		/* screen.render(int x position, int y-position, int sprite-location, int colors, int bits (0 = not mirrored, 1 = mirrored)) */
		screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col1, flip1); //renders the top-right of the sprite
		screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col1, flip1); //renders the top-left of the sprite
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col2, flip2); //renders the bottom-right of the sprite
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col2, flip2); //renders the bottom-left of the sprite
	}

	/** What happens when the player (or any entity) touches the air wizard */
	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) { // if the entity happens to be the player...
			entity.hurt(this, 3, dir); // hurt the player for 3 damage.
		}
	}

	/** What happens when the air wizard dies */
	protected void die() {
		super.die(); // calls the die() method in Mob.java
		if (level.player != null) { // if the player is not null
			level.player.score += 1000; // gives the player 1000 points of score
			level.player.gameWon(); // player wins the game
		}
		Sound.bossdeath.play(); // plays a sound.
	}

}