package de.uni_oldenburg.transport;

import java.util.*;


/**
 * A single location on a map.
 */
public class Location {

	/**
	 * Is the name of the location on the map.
	 */
	private final String name;

	/**
	 * Is the amount of units to be delivered to this location
	 */
	private int amount;

	/**
	 * Is a map of neighbouring locations with a expense indicator as kilometers.
	 */
	private LinkedHashMap<Location, Integer> neighbouringLocations = new LinkedHashMap<>();

	/**
	 * Create a simple location without neighbors and zero amount
	 *
	 * @param name The locations name
	 */
	public Location(final String name) {
		this.name = name;
		this.amount = 0;
	}

	/**
	 * Adds a neighbouring location to the map if and only if the location has not been added yet.
	 *
	 * @param location Is the {@link Location} to be added.
	 * @param expense  Is the expense in kilometers for the neighbouring location.
	 * @return A boolean value indicating whether the neighbouring location has been added (true) or not (false.)
	 */
	public boolean addNeighbouringLocation(Location location, int expense) {
		for (Location locationEntry : neighbouringLocations.keySet()) {
			if (location.getName().equals(locationEntry.getName())) return false;
		}
		neighbouringLocations.put(location, expense);
		return true;
	}

	/**
	 * Returns the name of this location.
	 *
	 * @return The name of this location as {@link String}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the amount needed by this location.
	 *
	 * @return The amount needed by this location as {@link Integer}.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Set the amount needed by this location.
	 *
	 * @param amount The amount needed by this location as {@link Integer}.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Get the map of neighbouring locations.
	 *
	 * @return The map of neighbouring locations as {@link HashMap}.
	 */
	public LinkedHashMap<Location, Integer> getNeighbouringLocations() {
		return neighbouringLocations;
	}

	/**
	 * Decides whether this location has a neighbouring location equally to the passes location.
	 *
	 * @param location The location to check for neighbourhood.
	 * @return Boolean value whether the location is a neighbour or not.
	 */
	public boolean hasNeigbouringLocationLocation(Location location) {
		return getNeighbouringLocations().containsKey(location);
	}

	/**
	 * Return this locations name
	 *
	 * @return this locations name
	 */
	public String toString() {
		return getName();
	}
}
