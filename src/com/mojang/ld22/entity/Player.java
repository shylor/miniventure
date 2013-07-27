package com.mojang.ld22.entity;

import java.util.List;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.FurnitureItem;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.PowerGloveItem;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.InventoryMenu;
import com.mojang.ld22.sound.Sound;

public class Player extends Mob {
	private InputHandler input; // keyboard input by the player
	private int attackTime, attackDir; // the time and direction of an attack.

	public Game game; // the game the player is in
	public Inventory inventory = new Inventory(); // the inventory of the player
	public Item attackItem; // the player's attack item
	public Item activeItem; // the player's active item
	public int stamina; // the player's stamina
	public int staminaRecharge; // the recharge rate of the player's stamina
	public int staminaRechargeDelay; // the recharge delay when the player uses up their stamina.
	public int score; // the player's score
	public int maxStamina = 10; // the maximum stamina that the player can have
	private int onStairDelay; // the delay before changing levels.
	public int invulnerableTime = 0; // the invulnerability time the player has when he is hit
	// Note: the player's health & max health are inherited from Mob.java
	
	public Player(Game game, InputHandler input) {
		this.game = game; // assigns the game that the player is in
		this.input = input; // assigns the input
		x = 24; // players x position
		y = 24; // players y position
		stamina = maxStamina; // assigns the stamina to be the max stamina (10)

		inventory.add(new FurnitureItem(new Workbench())); // adds a workbench to the player's inventory
		inventory.add(new PowerGloveItem()); // adds a power glove to the player's inventory
	}

	public void tick() {
		super.tick(); // ticks the parent (Mob.java)

		if (invulnerableTime > 0) invulnerableTime--; // if invulnerableTime is above 0, then minus it by 1.
		Tile onTile = level.getTile(x >> 4, y >> 4); // gets the current tile the player is on.
		if (onTile == Tile.stairsDown || onTile == Tile.stairsUp) { // if the tile is a stairs up or stairs down...
			if (onStairDelay == 0) { // if the stair delay is 0 then...
				changeLevel((onTile == Tile.stairsUp) ? 1 : -1); // change level depending on if the Tile is an stairsUp or not.
				onStairDelay = 10; // creates a stair delay of 10.
				return; // skips the rest of the code
			}
			onStairDelay = 10; // stair delay is set to 10.
		} else {
			if (onStairDelay > 0) onStairDelay--; // if stair delay is above 0, then minus it by 1.
		}

		/* if stamina is smaller or equal to 0, and if the recharge & recharge delay are both 0 then...  */
		if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
			staminaRechargeDelay = 40; // the recharge delay will equal 40 
		}

		if (staminaRechargeDelay > 0) { // if the recharge delay is above 0 then...
			staminaRechargeDelay--; // minus the recharge delay by 1.
		}

		if (staminaRechargeDelay == 0) { // if the stamina recharge delay is 0 then...
			staminaRecharge++; // the stamina recharge adds up.
			if (isSwimming()) { // if the player is swimming then...
				staminaRecharge = 0; // the recharge is 0.
			}
			while (staminaRecharge > 10) { // while the stamina recharge is above 10 then...
				staminaRecharge -= 10; // minus the recharge by 10
				if (stamina < maxStamina) stamina++; // if the player's stamina is less than their max stamina then add 1 stamina.
			}
		}

		int xa = 0; // x acceleration
		int ya = 0; // y acceleration
		if (input.up.down) ya--; // if the player presses up then his y acceleration will be -1
		if (input.down.down) ya++; // if the player presses down then his y acceleration will be 1
		if (input.left.down) xa--; // if the player presses left then his x acceleration will be -1
		if (input.right.down) xa++; // if the player presses up right his x acceleration will be 1
		
		if (isSwimming() && tickTime % 60 == 0) { // if the player is swimming and the remainder of (tickTime/60) equals 0 then...
			if (stamina > 0) { // if stamina is above 0 then...
				stamina--; // minus 0 by 1.
			} else { // else
				hurt(this, 1, dir ^ 1); // do 1 damage to the player
			}
		}

		if (staminaRechargeDelay % 2 == 0) { // if the remainder of (staminaRechargeDelay/2) equals 0 then...
			move(xa, ya); // move the player in the x & y acceleration
		}

		if (input.attack.clicked) { // if the player presses the attack button...
			if (stamina == 0) { // if the player's stamina is 0...
				// nothing
			} else { // if the player's stamina is larger than 0 then...
				stamina--; // minus the stamina by 1
				staminaRecharge = 0; // the recharge is set to 0
				attack(); // calls the attack() method
			}
		}
		
		if (input.menu.clicked) { // if the player presses the menu button...
			if (!use()) { // if the use() method returns false then (aka: no furniture in-front of the player)
				game.setMenu(new InventoryMenu(this)); // set the current menu to the inventory menu
			}
		}
		
		if (attackTime > 0) attackTime--; // if the attack time is larger than 0 then minus it by 1

	}

	private boolean use() {
		if (dir == 0 && use(x - 8, y + 4 - 2, x + 8, y + 12 - 2)) return true; // if the entity below has a use() method then return true
		if (dir == 1 && use(x - 8, y - 12 - 2, x + 8, y - 4 - 2)) return true; // if the entity above has a use() method then return true
		if (dir == 3 && use(x + 4, y - 8 - 2, x + 12, y + 8 - 2)) return true; // if the entity to the right has a use() method then return true
		if (dir == 2 && use(x - 12, y - 8 - 2, x - 4, y + 8 - 2)) return true; // if the entity to the left has a use() method then return true
		return false;
	}

	
	private void attack() {
		walkDist += 8; // increase the walkDist (changes the sprite)
		attackDir = dir; // the attack direction equals the current direction
		attackItem = activeItem; // the attackItem is the active item
		boolean done = false; // not done.

		if (activeItem != null) { // if the player has a active Item
			attackTime = 10; // attack time will be set to 10.
			int yo = -2; // y offset
			int range = 12; // range from an object
			/* if the interaction between you and an entity is successful then done = true */
			if (dir == 0 && interact(x - 8, y + 4 + yo, x + 8, y + range + yo)) done = true;
			if (dir == 1 && interact(x - 8, y - range + yo, x + 8, y - 4 + yo)) done = true;
			if (dir == 3 && interact(x + 4, y - 8 + yo, x + range, y + 8 + yo)) done = true;
			if (dir == 2 && interact(x - range, y - 8 + yo, x - 4, y + 8 + yo)) done = true;
			if (done) return; // if done = true, then skip the rest of the code.

			int xt = x >> 4; // current x-tile coordinate you are on.
			int yt = (y + yo) >> 4; // current y-tile coordinate you are on.
			int r = 12; // radius 
			if (attackDir == 0) yt = (y + r + yo) >> 4; // gets the tile below that you are attacking.
			if (attackDir == 1) yt = (y - r + yo) >> 4; // gets the tile above that you are attacking.
			if (attackDir == 2) xt = (x - r) >> 4; // gets the tile to the left that you are attacking.
			if (attackDir == 3) xt = (x + r) >> 4; // gets the tile to the right that you are attacking.

			if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) { // if (xt & yt) are larger or equal to 0 and less than the level's width and height...
				if (activeItem.interactOn(level.getTile(xt, yt), level, xt, yt, this, attackDir)) { // if the interactOn() method in an item returns true...
					done = true; // done equals true
				} else {
					if (level.getTile(xt, yt).interact(level, xt, yt, this, activeItem, attackDir)) { // if the interact() method in an item returns true...
						done = true; // done equals true
					}
				}
				if (activeItem.isDepleted()) { // if the activeItem has 0 resources left then...
					activeItem = null; // removes the active item.
				}
			}
		}

		if (done) return; // if done is true, then skip the rest of the code

		if (activeItem == null || activeItem.canAttack()) { // if there is no active item, OR if the item can be used to attack...
			attackTime = 5; // attack time = 5
			int yo = -2; // y offset
			int range = 20; // range of attack
			if (dir == 0) hurt(x - 8, y + 4 + yo, x + 8, y + range + yo); // attacks the entity below you.
			if (dir == 1) hurt(x - 8, y - range + yo, x + 8, y - 4 + yo); // attacks the entity above you.
			if (dir == 3) hurt(x + 4, y - 8 + yo, x + range, y + 8 + yo); // attacks the entity to the right of you.
			if (dir == 2) hurt(x - range, y - 8 + yo, x - 4, y + 8 + yo); // attacks the entity to the left of you.

			int xt = x >> 4; // current x-tile coordinate you are on.
			int yt = (y + yo) >> 4; // current y-tile coordinate you are on.
			int r = 12; // radius
			if (attackDir == 0) yt = (y + r + yo) >> 4; // gets the tile below that you are attacking.
			if (attackDir == 1) yt = (y - r + yo) >> 4; // gets the tile above that you are attacking.
			if (attackDir == 2) xt = (x - r) >> 4; // gets the tile to the left that you are attacking.
			if (attackDir == 3) xt = (x + r) >> 4; // gets the tile to the right that you are attacking.

			if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) { // if (xt & yt) are larger or equal to 0 and less than the level's width and height...
				level.getTile(xt, yt).hurt(level, xt, yt, this, random.nextInt(3) + 1, attackDir); // calls the hurt() method in that tile's class
			}
		}

	}

	/** if the entity in-front of the player has a use() method, it will call it. */
	private boolean use(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1); // gets the entities within the 4 points
		for (int i = 0; i < entities.size(); i++) { // cycles through the entities
			Entity e = entities.get(i); // gets the current entity
			if (e != this) if (e.use(this, attackDir)) return true; // if the entity is not the player, and has a use() method then return true.
		}
		return false;
	}

	/** if the entity in-front of the player has a interact() method, it will call it */
	private boolean interact(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1); // gets the entities within the 4 points
		for (int i = 0; i < entities.size(); i++) { // cycles through the entities
			Entity e = entities.get(i); // gets the current entity
			if (e != this) if (e.interact(this, activeItem, attackDir)) return true; // if the entity is not the player, and has a interact() method then return true.
		}
		return false;
	}

	/** if the entity in-front of the player has a hurt() method, it will call it */
	private void hurt(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1); // gets the entities within the 4 points
		for (int i = 0; i < entities.size(); i++) { // cycles through the entities
			Entity e = entities.get(i); // gets the current entity
			if (e != this) e.hurt(this, getAttackDamage(e), attackDir); // if the entity is not the player, and has a hurt() method then return true.
		}
	}

	/** Gets the attack damage the player has */
	private int getAttackDamage(Entity e) {
		int dmg = random.nextInt(3) + 1; // damage is equal to a random number between 1 and 3
		if (attackItem != null) { // if the current attack item isn't null
			dmg += attackItem.getAttackDamageBonus(e); // adds the attack damage bonus (from a sword/axe)
		}
		return dmg;
	}

	/** Draws the player on the screen */
	public void render(Screen screen) {
		int xt = 0; // X tile coordinate in the sprite-sheet
		int yt = 14; // Y tile coordinate in the sprite-sheet

		int flip1 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
		int flip2 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)

		if (dir == 1) { // if the direction is 1 (Up)...
			xt += 2; // then move the sprite over 2 tiles
		}
		if (dir > 1) { // if the direction is larger than 1 (left or right)...
			flip1 = 0; // flip1 will equal 0.
			flip2 = ((walkDist >> 4) & 1); // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
			if (dir == 2) { // if the direction is 2 (left)
				flip1 = 1; // mirror the sprite
			}
			xt += 4 + ((walkDist >> 3) & 1) * 2; // animation based on walk distance
		}

		/* where to draw the sprite relative to our position */
		int xo = x - 8; // the horizontal offset location to start drawing the sprite
		int yo = y - 11; // the vertical offset location to start drawing the sprite
		if (isSwimming()) { // if the player is swimming...
			yo += 4; // y offset is moved up by 4
			int waterColor = Color.get(-1, -1, 115, 335) ; // color of water circle
			if (tickTime / 8 % 2 == 0) { // if the remainder of (tickTime/8)/2 is equal to 0...
				waterColor = Color.get(-1, 335, 5, 115); // change the color of water circle
			}
			screen.render(xo + 0, yo + 3, 5 + 13 * 32, waterColor, 0); // render the water graphic
			screen.render(xo + 8, yo + 3, 5 + 13 * 32, waterColor, 1); // render the mirrored water graphic to the right.
		}

		if (attackTime > 0 && attackDir == 1) { // if the attack time is larger than 0 and the attack Direction is 1 (Up)
			screen.render(xo + 0, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 0); // render a half-slash
			screen.render(xo + 8, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 1); // render a mirrored half-slash to the right of it.
			if (attackItem != null) { // if the player has an item
				attackItem.renderIcon(screen, xo + 4, yo - 4); // then render the icon of the item.
			}
		}
		int col = Color.get(-1, 100, 220, 532); // color of the player
		if (hurtTime > 0) { // if the player is hurt...
			col = Color.get(-1, 555, 555, 555); // then the color is white
		}

		if (activeItem instanceof FurnitureItem) { // if the active item is a furniture item
			yt += 2; // moves the y tile 2 over. (for the player holding his hands up)
		}
		screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1); // render the top-left part of the sprite
		screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1); // render the top-right part of the sprite
		if (!isSwimming()) { // if the player is NOT swimming
			screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2); // render the bottom-left part of the sprite
			screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2); // render the bottom-right part of the sprite
		}

		if (attackTime > 0 && attackDir == 2) { // if the attack time is larger than 0 and the attack Direction is 2 (Left)
			screen.render(xo - 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 1); // render a half-slash
			screen.render(xo - 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 3); // render a mirrored half-slash below it.
			if (attackItem != null) { // if the player has an item
				attackItem.renderIcon(screen, xo - 4, yo + 4); // then render the icon of the item.
			}
		}
		if (attackTime > 0 && attackDir == 3) { // if the attack time is larger than 0 and the attack Direction is 3 (Right)
			screen.render(xo + 8 + 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 0); // render a half-slash
			screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 2); // render a mirrored half-slash below it.
			if (attackItem != null) { // if the player has an item
				attackItem.renderIcon(screen, xo + 8 + 4, yo + 4); // then render the icon of the item.
			}
		}
		if (attackTime > 0 && attackDir == 0) {  // if the attack time is larger than 0 and the attack Direction is 0 (Down)
			screen.render(xo + 0, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 2); // render a half-slash
			screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 3); // render a mirrored half-slash to the right of it.
			if (attackItem != null) { // if the player has an item
				attackItem.renderIcon(screen, xo + 4, yo + 8 + 4); // then render the icon of the item.
			}
		}

		if (activeItem instanceof FurnitureItem) { // if the active Item is a furniture item.
			Furniture furniture = ((FurnitureItem) activeItem).furniture; // gets the furniture of that item
			furniture.x = x; // the x position is that of the player's
			furniture.y = yo; // the y position is that of yo
			furniture.render(screen); // renders the furniture on the screen (above his hands)

		}
	}

	/** What happens when the player interacts with a itemEntity */
	public void touchItem(ItemEntity itemEntity) {
		itemEntity.take(this); // calls the take() method in ItemEntity
		inventory.add(itemEntity.item); // adds the item into your inventory
	}

	/** Returns if the entity can swim */
	public boolean canSwim() {
		return true; // yes the player can swim
	}

	/** Finds a start position for the player to start in. */
	public boolean findStartPos(Level level) {
		while (true) { // will loop until it returns
			int x = random.nextInt(level.w); // gets a random value between 0 and the world's width - 1
			int y = random.nextInt(level.h); // gets a random value between 0 and the world's height - 1
			if (level.getTile(x, y) == Tile.grass) { // if the tile at the x & y coordinates is a grass tile then...
				this.x = x * 16 + 8; // the player's x coordinate will be in the middle of the tile
				this.y = y * 16 + 8; // the player's y coordinate will be in the middle of the tile
				return true; // returns and stop's the loop
			}
		}
	}

	/** Pays the stamina used for an action */
	public boolean payStamina(int cost) {
		if (cost > stamina) return false; // if the player doesn't have enough stamina, then return false
		stamina -= cost; // minus the current stamina by the cost
		return true; // return true
	}

	/** Changes the level */
	public void changeLevel(int dir) {
		game.scheduleLevelChange(dir); // schedules a level change.
	}

	/** Gets the player's light radius underground */
	public int getLightRadius() {
		int r = 2; // the radius of the light.
		if (activeItem != null) { // if the player has an item
			if (activeItem instanceof FurnitureItem) { // if item is a furniture item
				int rr = ((FurnitureItem) activeItem).furniture.getLightRadius(); // gets the furniture's light radius
				if (rr > r) r = rr; // if the furniture's light radius is larger than the player's, then the players light radius will equal that of the furniture's.
			} 
		}
		return r; // return the radius
	}

	/** What happens when the player dies */
	protected void die() {
		super.die(); // calls the die() method in Mob.java
		Sound.playerDeath.play(); // plays a sound
	}

	/** What happens when the player touches an entity */
	protected void touchedBy(Entity entity) {
		if (!(entity instanceof Player)) { // if the entity is not a player.
			entity.touchedBy(this); // calls the touchedBy() method in the entity's class
		}
	}

	/** What happens when the player is hurt */
	protected void doHurt(int damage, int attackDir) {
		if (hurtTime > 0 || invulnerableTime > 0) return; // if hurt time OR invulnerableTime is above 0, then skip the rest of the code.

		Sound.playerHurt.play(); // plays a sound
		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504, 504, 504))); // adds a text particle telling how much damage was done.
		health -= damage; // health is decreased by the damage amount
		if (attackDir == 0) yKnockback = +6; // if the direction was from below, then get knocked above.
		if (attackDir == 1) yKnockback = -6; // if the direction was from above, then get knocked below.
		if (attackDir == 2) xKnockback = -6; // if the direction was from the right, then get knocked to the left.
		if (attackDir == 3) xKnockback = +6; // if the direction was from the left, then get knocked to the right.
		hurtTime = 10; // hurt time set to 10
		invulnerableTime = 30; // invulnerable time is set to 30
	}

	/** What happens when the player wins */
	public void gameWon() {
		level.player.invulnerableTime = 60 * 5; // sets the invulnerable time to 300
		game.won(); // win the game
	}
}