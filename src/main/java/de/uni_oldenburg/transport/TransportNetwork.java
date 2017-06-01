package de.uni_oldenburg.transport;

import java.util.ArrayList;

/**
 * The TransportNetwork represents the map
 */
public class TransportNetwork {

	/**
	 * The map is just a list of locations
	 */
	private final Location[] network;

	public TransportNetwork(Location[] network) {
		this.network = network;
	}

	/**
	 * Get a specific location by its name
	 *
	 * @param name The locations name
	 * @return The location to be found, null if not present
	 */
	public Location getLocationByName(String name) {
		for (Location location : network) {
			if (location.getName().equals(name)) {
				return location;
			}
		}
		return null;
	}

	public Location[] getLocations() {
		return network;
	}
}
