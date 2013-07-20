package com.mojang.ld22.entity.particle;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class SmashParticle extends Entity {
	private int time = 0; // the time that the particle is on screen

	public SmashParticle(int x, int y) {
		this.x = x; // assigns the x position of the particle
		this.y = y; // assigns the u position of the particle
		Sound.monsterHurt.play(); // plays a sound
	}

	/* Update method, 60 updates (ticks) a second */
	public void tick() {
		time++; //increases the time variable by 1
		if (time > 10) {//if time is over 10 (1/6th of a second old)
			remove(); //remove the particle
		}
	}

	public void render(Screen screen) {
		int col = Color.get(-1, 555, 555, 555);// color of the particle (white)
		screen.render(x - 8, y - 8, 5 + 12 * 32, col, 2); // renders the top-left part
		screen.render(x - 0, y - 8, 5 + 12 * 32, col, 3); // renders the top-right part
		screen.render(x - 8, y - 0, 5 + 12 * 32, col, 0); // renders the bottom-left part
		screen.render(x - 0, y - 0, 5 + 12 * 32, col, 1); // renders the bottom-right part
	}
}
