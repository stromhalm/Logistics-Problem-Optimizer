package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.optimizers.Graph.Graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * The TransportNetwork represents the map
 */
public class TransportNetwork {

	/**
	 * The map is just a list of locations
	 */
	private final Location[] network;

	/**
	 * {@link HashMap} matrix with shortest path as key and the single expenses in the path as values of the locations.
	 */
	private LinkedHashMap<Location, Integer>[][] shortestPaths;

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

	public int getNumberOfLocations() {
		return network.length;
	}

	/**
	 * Computes the shortest path using Dijkstra.
	 */
	public void computeShortestPaths() {
		shortestPaths = Graph.computeAdjazenMatrix(network);
	}

	/**
	 * Gets the shortest path from a start location to a destination the following list order: i:0 (start), i:1 (startNeighbour), ..., i:n (destination);
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public LinkedHashMap<Location, Integer> getShortestPath(Location from, Location to) {
		for (int i = 0; i < shortestPaths.length; i++) {
			for (int j = 0; j < shortestPaths[i].length; j++) {
				Iterator iterators = shortestPaths[i][j].keySet().iterator();
				Location location = null;
				if ((location = (Location) iterators.next()) != null) {
					if (location.getName().equals(from.getName())) {
						while (iterators.hasNext()) location = (Location) iterators.next();
						if (location.getName().equals(to.getName())) return shortestPaths[i][j];
					}
				}
			}
		}
		return null;
	}
}
