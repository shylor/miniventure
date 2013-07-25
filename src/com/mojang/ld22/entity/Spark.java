package com.mojang.ld22.entity;

import java.util.List;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

public class Spark extends Entity {
	private int lifeTime; // how much time until the spark disappears
	public double xa, ya; // the x and y acceleration
	public double xx, yy; // the x and y positions
	private int time; // the amount of time that has occurred
	private AirWizard owner; // the AirWizard that created this spark

	public Spark(AirWizard owner, double xa, double ya) {
		this.owner = owner; // assigns the owner
		xx = this.x = owner.x; // assigns the x position
		yy = this.y = owner.y; // assigns the y position
		xr = 0; // x radius size
		yr = 0; // y radius size

		this.xa = xa; // assigns the x acceleration
		this.ya = ya; // assigns the x acceleration

		// Max time = 629 ticks. Min time = 600 ticks.
		lifeTime = 60 * 10 + random.nextInt(30); // the lifetime of this spark is (60 * 10 + (random value between 0 to 29)).
	}

	/** Update method, updates (ticks) 60 times a second */
	public void tick() {
		time++; // increases time by 1
		if (time >= lifeTime) { // if time is larger or equal to lifeTime then...
			remove(); // remove this from the world
			return; // skip the rest of the code
		}
		xx += xa; // move the xx position in the x acceleration direction
		yy += ya; // move the yy position in the x acceleration direction
		x = (int) xx; // the x position equals the integer converted xx position.
		y = (int) yy; // the y position equals the integer converted yy position.
		List<Entity> toHit = level.getEntities(x, y, x, y); // gets the entities in the current position to hit.
		for (int i = 0; i < toHit.size(); i++) { // cycles through the list
			Entity e = toHit.get(i); // gets the current entity
			if (e instanceof Mob && !(e instanceof AirWizard)) { // if the entity is a mob, but not a Air Wizard then...
				e.hurt(owner, 1, ((Mob) e).dir ^ 1); // hurt the mob with 1 damage
			}
		}
	}

	/** Can this entity block you? Nope. */
	public boolean isBlockableBy(Mob mob) {
		return false;
	}

	/** Renders the spark on the screen */
	public void render(Screen screen) {
		/* this first part is for the blinking effect */
		if (time >= lifeTime - 6 * 20) {// if time is larger or equal to lifeTime - 6 * 20 then...
			if (time / 6 % 2 == 0) return; // if the remainder of (time/6)/2 = 0 then skip the rest of the code.
		}

		int xt = 8; // the x coordinate on the sprite-sheet
		int yt = 13; // the y coordinate on the sprite-sheet

		screen.render(x - 4, y - 4 - 2, xt + yt * 32, Color.get(-1, 555, 555, 555), random.nextInt(4)); // renders the spark
		screen.render(x - 4, y - 4 + 2, xt + yt * 32, Color.get(-1, 000, 000, 000), random.nextInt(4)); // renders the shadow on the ground
	}
}
