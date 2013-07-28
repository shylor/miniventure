package com.mojang.ld22.entity;

import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.sound.Sound;

public class Mob extends Entity {
	protected int walkDist = 0; // How far we've walked currently, incremented after each movement
	protected int dir = 0; // The direction the mob is facing, used in attacking and rendering. 0 is down, 1 is up, 2 is left, 3 is right
	public int hurtTime = 0; // A delay after being hurt, that temporarily prevents further damage for a short time
	protected int xKnockback, yKnockback; // The amount of vertical/horizontal knockback that needs to be inflicted, if it's not 0, it will be moved one pixel at a time.
	public int maxHealth = 10; // The maximum amount of health the mob can have
	public int health = maxHealth; // The amount of health we currently have, and set it to the maximum we can have
	public int swimTimer = 0; // How much we have moved in water currently, used to halve movement speed
	public int tickTime = 0; // Incremented whenever tick() is called, is effectively the age in ticks

	public Mob() {
		x = y = 8; // By default, set x and y coordinates to 8
		xr = 4; // Sets the x and y radius/size of the mob
		yr = 3;
	}

	public void tick() { //
		tickTime++; // Increment our tick counter
		if (level.getTile(x >> 4, y >> 4) == Tile.lava) { // If we are trying to swim in lava
			hurt(this, 4, dir ^ 1); // Inflict 4 damage to ourselves, sourced from ourselves, with the direction as the opposite of ours
		}

		if (health <= 0) { // Check if health is at a death-causing level (less than or equal to 0)
			die(); // If so, die
		}
		if (hurtTime > 0) hurtTime--; // If a timer preventing damage temporarily is set, decrement it's value
	}

	protected void die() { // Kill the mob, called when health drops to 0
		remove(); // Remove the mob, with the method inherited from Entity
	}

	public boolean move(int xa, int ya) { // Move the mob, overrides from Entity
		if (isSwimming()) { // Check if the mob is swimming, ie. in water/lava
			if (swimTimer++ % 2 == 0) return true; // Increments swimTimer, and continues only every second time (when swimTimer is not divisible by 2 evenly)
		}
		if (xKnockback < 0) { // If we have negative horizontal knockback (to the left)
			move2(-1, 0); // Move to the left 1 pixel
			xKnockback++; // And increase the knockback by 1 so it is gradually closer to 0, and this will stop being called
		}
		if (xKnockback > 0) { // If we have positive horizontal knockback (to the right)
			move2(1, 0); // Move to the right 1 pixel
			xKnockback--; // And decrease the knockback by 1 so it is gradually closer to 0, and this will stop being called
		}
		if (yKnockback < 0) { // If we have negative vertical knockback (upwards)
			move2(0, -1); // Move upwards 1 pixel
			yKnockback++; // And increase the knockback by 1 so it is gradually closer to 0, and this will stop being called
		}
		if (yKnockback > 0) { // If we have positive vertical knockback (downwards)
			move2(0, 1); // Move downwards 1 pixel
			yKnockback--; // And decrease the knockback by 1 so it is gradually closer to 0, and this will stop being called
		}
		if (hurtTime > 0) return true; // If we have been hurt recently and haven't yet cooled down, don't continue with the movement (so only knockback will be performed)
		if (xa != 0 || ya != 0) { // Only if horizontal or vertical movement is actually happening
			walkDist++; // Increment our walking/movement counter
			if (xa < 0) dir = 2; // Set the mob's direction based on movement: left
			if (xa > 0) dir = 3; // right
			if (ya < 0) dir = 1; // up
			if (ya > 0) dir = 0; // down
		}
		return super.move(xa, ya); // Call the move method from Entity
	}

	protected boolean isSwimming() { // Check if the mob is swimming
		Tile tile = level.getTile(x >> 4, y >> 4); // Get the tile the mob is standing on (at x/16, y/16)
		return tile == Tile.water || tile == Tile.lava; // Check if the tile is liquid, and return true if so
	}

	public boolean blocks(Entity e) { // Check if another entity would be prevented from moving through this one
		return e.isBlockableBy(this); // Call the method on the other entity to determine this, and return it
	}

	public void hurt(Tile tile, int x, int y, int damage) { // Hurt the mob, when the source of damage is a tile
		int attackDir = dir ^ 1; // Set attackDir to our own direction, inverted. XORing it with 1 flips the rightmost bit in the variable, this effectively adds one when even, and subtracts one when odd
		doHurt(damage, attackDir); // Call the method that actually performs damage, and provide it with our new attackDir value
	}

	public void hurt(Mob mob, int damage, int attackDir) { // Hurt the mob, when the source is another mob
		doHurt(damage, attackDir); // Call the method that actually performs damage, and use our provided attackDir
	}

	public void heal(int heal) { // Restore health on the mob
		if (hurtTime > 0) return; // If the mob has been hurt recently and hasn't cooled down, don't continue

		level.add(new TextParticle("" + heal, x, y, Color.get(-1, 50, 50, 50))); // Add a text particle in our level at our position, that is green and displays the amount healed
		health += heal; // Actually add the amount to heal to our current health
		if (health > maxHealth) health = maxHealth; // If our health has exceeded our maximum, lower it back down to said maximum
	}

	protected void doHurt(int damage, int attackDir) { // Actually hurt the mob, based on only damage and a direction
		if (hurtTime > 0) return; // If the mob has been hurt recently and hasn't cooled down, don't continue

		if (level.player != null) { // If there is a player in the level
			int xd = level.player.x - x; // Set xd to the difference between our's and the player's x-coordinate.
			int yd = level.player.y - y; // Set yd to the difference between our's and the player's y-coordinate.
			if (xd * xd + yd * yd < 80 * 80) { // Some math to figure out if the distance is less than 80 pixels. Pythagoras' theorem is used on a hypothetical right-angled triangle between our two points, where the hypotenuse is the diagonal distance
				Sound.monsterHurt.play(); // If we are close enough to the player, play the monsterHurt sound
			}
		}
		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 500, 500, 500))); // Make a text particle at our position in our level, bright red and displaying the damage inflicted
		health -= damage; // Actually change our health
		if (attackDir == 0) yKnockback = +6; // If the direction is downwards, add positive vertical knockback
		if (attackDir == 1) yKnockback = -6; // If the direction is upwards, add negative vertical knockback
		if (attackDir == 2) xKnockback = -6; // If the direction is to the left, add negative horizontal knockback
		if (attackDir == 3) xKnockback = +6; // If the direction is to the right, add positive horizontal knockback
		hurtTime = 10; // Set a delay before we can be hurt again
	}

	public boolean findStartPos(Level level) { // Find a place to spawn the mob
		int x = random.nextInt(level.w); // Pick a random x coordinate, inside the level's bounds
		int y = random.nextInt(level.h); // Pick a random y coordinate, inside the level's bounds
		int xx = x * 16 + 8; // Get actual pixel coordinates from this tile coord
		int yy = y * 16 + 8;

		if (level.player != null) { // If there is a player in our level
			int xd = level.player.x - xx; // Get the difference between our attempted spawn x, and the player's x
			int yd = level.player.y - yy; // Get the difference between our attempted spawn y, and the player's y
			if (xd * xd + yd * yd < 80 * 80) return false; // Use pythagoras' theorem to determine the distance between us and the player, and if it is less than 80 (too close) then return false
		}

		int r = level.monsterDensity * 16; // Get the allowed density of mobs in the level, convert it from a tile to a real coordinate
		if (level.getEntities(xx - r, yy - r, xx + r, yy + r).size() > 0) return false; // Get a list of mobs in the level, within a box centered on our attempted coordinates, with dimensions of r times 2, and if there are any close to us, return false;

		if (level.getTile(x, y).mayPass(level, x, y, this)) { // Check if the tile we're trying to spawn on is not solid to us
			this.x = xx; // Set our coordinates to the attempted ones
			this.y = yy;
			return true;
		}

		return false; 
	}
}
