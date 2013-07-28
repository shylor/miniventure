package com.mojang.ld22.entity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class Inventory {
	public List<Item> items = new ArrayList<Item>(); // the list of items that is in the inventory.

	/** Adds an item to the inventory */
	public void add(Item item) {
		add(items.size(), item); // adds a item into latest spot of the inventory
	}

	/** Adds an item to a specific spot in the inventory */
	public void add(int slot, Item item) {
		if (item instanceof ResourceItem) {// if the item happens to be a resource
			ResourceItem toTake = (ResourceItem) item; // converts the item object into a ResourceItem object
			ResourceItem has = findResource(toTake.resource); // finds if you have the resource in their inventory
			if (has == null) { // if the owner of this inventory doesn't have the resource
				items.add(slot, toTake); // add the resource in the items list
			} else {
				has.count += toTake.count; // else add to the count of the item resource
			}
		} else {
			items.add(slot, item); // add the item to the items list
		}
	}

	/** Finds a resource in your inventory */
	private ResourceItem findResource(Resource resource) {
		for (int i = 0; i < items.size(); i++) { // loops through the items list
			if (items.get(i) instanceof ResourceItem) { // if the current item is a ResourceItem
				ResourceItem has = (ResourceItem) items.get(i); // converts the item object into a ResourceItem object
				if (has.resource == resource) return has; // returns if the loop has found a matching resource in your inventory
			}
		}
		return null; // else it will return null
	}

	/** Returns true if the player has the resource, and has equal or more than the amount given */
	public boolean hasResources(Resource r, int count) {
		ResourceItem ri = findResource(r); // finds the resource
		if (ri == null) return false; // if the player doesn't have the resource, then return false
		return ri.count >= count; // if the player has more or equal amount of resource than the count, then return true.
	}

	/** Removes resources from your inventory */
	public boolean removeResource(Resource r, int count) {
		ResourceItem ri = findResource(r); // finds the resource
		if (ri == null) return false; // if the resource cannot be found, then skip the rest of the code
		if (ri.count < count) return false; // if the resource amount is smaller than the count, then skip the rest of the code.
		ri.count -= count; // minus the count.
		if (ri.count <= 0) items.remove(ri); // if the count is smaller than or equal to 0 then remove the resource from the list
		return true;
	}

	/** Returns the amount of an item you have in the inventory */
	public int count(Item item) {
		if (item instanceof ResourceItem) { // if the item is a resource...
			ResourceItem ri = findResource(((ResourceItem)item).resource); // finds the resource
			if (ri!=null) return ri.count; // returns the amount of that resource
		} else { // if the item is NOT a resource...
			int count = 0; // the count of the item
			for (int i=0; i<items.size(); i++) { // loops through the player's inventory
				if (items.get(i).matches(item)) count++; // if the current item matches the one given, then the count increases.
			}
			return count; // returns the count.
		}
		return 0;
	}
}
