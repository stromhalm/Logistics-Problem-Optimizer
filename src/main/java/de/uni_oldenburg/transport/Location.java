package de.uni_oldenburg.transport;

import java.util.*;


/**
 * A single location on a map.
 */
public class Location {

	/**
	 * Is the name of the location on the map.
	 */
	private String name;
	/**
	 * Is a map of neighbouring locations with a expense indicator as kilometers.
	 */
	private HashMap<Location, Long> neighbouringLocations;

	/**
	 * @param name                  Is the name of the location on the map.
	 * @param neighbouringLocations Is a map of neighbouring locations with a expense indicator as kilometers.
	 * @throws Exception Is thrown if name is either empty or null or if neighbouringLocations is null.
	 */
	public Location(String name, HashMap neighbouringLocations) throws Exception {
		if (neighbouringLocations == null) throw new Exception("The neighbouringLocations map must not be null.");
		if (name == null || name.isEmpty()) throw new Exception("The name of the location must not be null or empty.");
		this.name = name;
		this.neighbouringLocations = neighbouringLocations;
	}

	/**
	 * Adds a neighbouring location to the map if and only if the location has not been added yet.
	 *
	 * @param location Is the {@link Location} to be added.
	 * @param expense  Is the expense in kilometers for the neighbouring location.
	 * @return A boolean value indicating whether the neighbouring location has been added (true) or not (false.)
	 */
	public boolean addNeighbouringLocation(Location location, long expense) {
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
	 * Get the map of neighbouring locations.
	 *
	 * @return The map of neighbouring locations as {@link HashMap}.
	 */
	public HashMap<Location, Long> getNeighbouringLocations() {
		return neighbouringLocations;
	}

}
