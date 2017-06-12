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

	/**
	 * Get all locations in the network
	 *
	 * @return Array of all the locations
	 */
	public Location[] getLocations() {
		return network;
	}

	/**
	 * Get the start location. It is the first with zero amount in the network
	 *
	 * @return The start location
	 */
	public Location getStartLocation() {

		// The start location is the one with zero amount
		for (Location location : network) {
			if (location.getAmount() == 0) {
				return location;
			}
		}
		return null;
	}
}
