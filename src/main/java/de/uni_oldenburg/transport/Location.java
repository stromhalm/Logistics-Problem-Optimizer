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
	 * The shortest known distance to the start location
	 */
	private int shortestKnownDistanceToStart = 0;

	/**
	 * The location which will finally lead to the start location
	 */
	private Location shortestKnownWayToStart = null;

	/**
	 * Is a map of neighbouring locations with a expense indicator as kilometers.
	 */
	private HashMap<Location, Integer> neighbouringLocations = new HashMap<>();

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
	 * Notify this location about a new candidate for a shortest way to the start
	 *
	 * @param neighborLocation The neighbor candidate
	 * @param distanceToStart The overall distance for this location
	 */
	public void addShortDistanceToStartCandidate(Location neighborLocation, int distanceToStart) {
		if (distanceToStart < shortestKnownDistanceToStart || shortestKnownWayToStart == null) {

			// Save for later
			shortestKnownDistanceToStart = distanceToStart;
			shortestKnownWayToStart = neighborLocation;

			// Recursively notify neighbors
			for (Map.Entry<Location, Integer> neighbor: neighbouringLocations.entrySet()) {
				neighbor.getKey().addShortDistanceToStartCandidate(this, distanceToStart+neighbor.getValue());
			}
		}
	}

	/**
	 * Get the map of neighbouring locations.
	 *
	 * @return The map of neighbouring locations as {@link HashMap}.
	 */
	public HashMap<Location, Integer> getNeighbouringLocations() {
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

}
