package com.mojang.ld22.crafting;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class ResourceRecipe extends Recipe {
	private Resource resource; //The resource used in this recipe

	/** Adds a recipe to craft a resource */
	public ResourceRecipe(Resource resource) {
		super(new ResourceItem(resource, 1)); //this goes through Recipe.java to be put on a list.
		this.resource = resource; //resource to be added
	}

	/** Adds the resource into your inventory */
	public void craft(Player player) {
		player.inventory.add(0, new ResourceItem(resource, 1)); //adds the resource
		}
}
