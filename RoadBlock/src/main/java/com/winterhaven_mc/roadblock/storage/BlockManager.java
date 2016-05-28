package com.winterhaven_mc.roadblock.storage;

import com.winterhaven_mc.roadblock.PluginMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;


public final class BlockManager {

	// reference to main class
	private final PluginMain plugin;

	// set of road block materials
	private Set<Material> roadBlockMaterials;
		
	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	public BlockManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;
	
		// get road block materials from config file
		updateMaterials();
	}

	
	/**
	 * Create HashSet of all blocks of valid road block material attached to location
	 * @param startLocation location to begin searching for attached road blocks
	 * @return Set of Locations of attached road blocks
	 */
	public final Set<Location> getFill(final Location startLocation) {
		
		if (startLocation == null) {
			return Collections.emptySet();
		}
		
		// create HashSet for return values
		final Set<Location> returnSet = new HashSet<>();
		
		// create queue using linked list implementation
		final Queue<Location> queue = new LinkedList<>();
		
		// put start location in queue
		queue.add(startLocation);
		
		// loop until queue is empty
		while (!queue.isEmpty()) {

			// remove location at head of queue
			Location loc = queue.poll();
		
			// if location is not in return set and is a road block material and is not too far from start...
			if (!returnSet.contains(loc) && roadBlockMaterials.contains(loc.getBlock().getType())
					&& loc.distanceSquared(startLocation) < Math.pow(plugin.getConfig().getInt("spread-distance"),2)) {
				
				// add location to return set
				returnSet.add(loc);

				// add adjacent locations to queue
				queue.add(loc.clone().add(0,0,1));
				queue.add(loc.clone().add(0,0,-1));
				queue.add(loc.clone().add(1,0,0));
				queue.add(loc.clone().add(-1,0,0));
			}
		}			
		return returnSet;
	}
	

	/**
	 * Check if block below player is a protected road block
	 * @param player the player to is above a road block
	 * @return {@code true} if player is within three blocks above a road block, else {@code false}
	 */
	public final boolean isRoadBelowPlayer(final Player player) {
		
		if (player == null) {
			return false;
		}
		
		int depth = 0;
		final int maxDepth = 3;
	
		// convert player location to block location (with integer coordinates)
		Location testLocation = player.getLocation().getBlock().getLocation().clone();
		
		// iterate until maxDepth reached
		while (depth < maxDepth) {
			
			// don't check datastore unless block at location is road block material
			if (isRoadBlockMaterial(testLocation)) {
				if (plugin.dataStore.isProtected(testLocation)) {
					return true;
				}
			}
			// decrement test location height by one block
			testLocation.add(0,-1,0);
			depth++;
		}		
		return false;
	}


	/**
	 * Check if block below location is a protected road block, searching down to maxDepth
	 * @param location the location to test if above a road block
	 * @param maxDepth the distance in blocks to test below location
	 * @return {@code true} if location is above a road block, else {@code false}
	 */
	public final boolean isRoadBelow(final Location location, final int maxDepth) {
		
		if (location == null) {
			return false;
		}

		int localMaxDepth = maxDepth;
		int depth = 0;

		// if maxDepth passed is less than or equal to zero, default to 5
		if (maxDepth <= 0) {
			localMaxDepth = 5;
		}
		
		// don't let maxDepth go below bottom of world
		localMaxDepth = Math.min(localMaxDepth, location.getBlockY());
	
		// get copy of location as block location (with integer coordinates)
		final Location testLocation = location.getBlock().getLocation().clone();
		
		// iterate until maxDepth reached
		while (depth < localMaxDepth) {
			
			// don't check datastore unless block at location is road block material
			if (isRoadBlockMaterial(testLocation)) {
				if (plugin.dataStore.isProtected(testLocation)) {
					return true;
				}
			}
			// decrement test location height by one block
			testLocation.add(0,-1,0);
			depth++;
		}
		return false;
	}


	/**
	 * Check if block is a protected road block
	 * @param block the block to test
	 * @return {@code true} if the block is a protected road block, else {@code false}
	 */
	public final boolean isRoadBlock(final Block block) {
		
		if (block == null) {
			return false;
		}
		
		// check if block is road block material
		if (!isRoadBlockMaterial(block)) {
			return false;
		}
		
		// check if block is in cache or datastore
		return plugin.dataStore.isProtected(block.getLocation());
	}
	

	/**
	 * Check if block is a valid road block material
	 * @param block the block to test for valid configured road block material
	 * @return {@code true} if the block material is a configured road block material, {@code false} if it is not
	 */
	private boolean isRoadBlockMaterial(final Block block) {
		return block != null && roadBlockMaterials.contains(block.getType());
	}


	/**
	 * Check if block at location is a valid road block material
	 * @param location the location of a block to test for valid road block material
	 * @return {@code true} if the block at location is a configured road block material, {@code false} if it is not
	 */
	private boolean isRoadBlockMaterial(final Location location) {
		return location != null && roadBlockMaterials.contains(location.getBlock().getType());
	}

	
	/**
	 * Check if a material is a valid road block material
	 * @param material the material type to test for valid configured road block material
	 * @return {@code true} if the material is a valid configured road block material, {@code false} if it is not
	 */
	public final boolean isRoadBlockMaterial(final Material material) {
		return material != null && roadBlockMaterials.contains(material);
	}
	
	
	/**
	 * Insert block locations into datastore
	 * @param locations a Collection of Locations to be inserted into the datastore
	 */
	public final void storeLocations(final Collection<Location> locations) {
		plugin.dataStore.insertRecords(locations);
	}
	

	/**
	 * Remove block locations from datastore
	 * @param locations a Collection of Locations to be deleted from the datastore
	 */
	public final void removeLocations(final Collection<Location> locations) {
		plugin.dataStore.deleteRecords(locations);
	}


	/**
	 * Remove a block location from datastore
	 * @param location the location to be removed from the datastore
     */
	public final void removeLocation(final Location location) {
		plugin.dataStore.deleteRecord(location);
	}
	
	
	/**
	 * Parse valid road block materials from config file
	 */
	public final void updateMaterials() {
		
		final ArrayList<String> materialStringList = 
				new ArrayList<>(plugin.getConfig().getStringList("materials"));
		
		final HashSet<Material> returnSet = new HashSet<>();
		
		Material matchMaterial = null;
		
		for (String string : materialStringList) {
			
			// try to split on colon
			if (!string.isEmpty()) {
				String materialElements[] = string.split("\\s*:\\s*");
	
				// try to match material
				if (materialElements.length > 0) {
					matchMaterial = Material.matchMaterial(materialElements[0]);
				}
			}
			
			// if matching material found, add to returnSet
			if (matchMaterial != null) {
				returnSet.add(matchMaterial);
			}
		}
		this.roadBlockMaterials = returnSet;
	}

	public final Set<Material> getRoadBlockMaterials() {
		return roadBlockMaterials;
	}

}
