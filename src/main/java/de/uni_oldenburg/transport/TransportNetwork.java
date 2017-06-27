package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.optimizers.Graph.Graph;

import java.util.*;

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

	/**
	 * Default constructor
	 *
	 * @param network Array of locations
	 */
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
	 * If in need of a fresh copy.
	 *
	 * @return
	 */
	public Location[] getLocationsDeepCopy() {
		ArrayList<Location> locations = new ArrayList<>();
		for (int i = 0; i < network.length; i++) {
			Location location = network[i];
			Location locationDeepCopy = null;
			for (Location locationDeepCopyAlreadyFound : locations) {
				if (location.getName().equals(locationDeepCopyAlreadyFound.getName())) {
					locationDeepCopy = locationDeepCopyAlreadyFound;
					break;
				}
			}
			if (locationDeepCopy == null) {
				locationDeepCopy = new Location(location.getName());
				locationDeepCopy.setAmount(location.getAmount());
				locations.add(location);
			}
			for (Map.Entry<Location, Integer> neighbouringLocationEntry : location.getNeighbouringLocations().entrySet()) {
				Location neighbouringLocationDeepCopy = null;
				// find existing references
				for (Location locationDeepCopyAlreadyFound : locations) {
					if (neighbouringLocationEntry.getKey().getName().equals(locationDeepCopyAlreadyFound.getName())) {
						neighbouringLocationDeepCopy = locationDeepCopyAlreadyFound;
						break;
					}
				}
				if (neighbouringLocationDeepCopy == null) {
					neighbouringLocationDeepCopy = new Location(neighbouringLocationEntry.getKey().getName());
					neighbouringLocationDeepCopy.setAmount(neighbouringLocationEntry.getKey().getAmount());
					locations.add(neighbouringLocationDeepCopy);
				}

				int expense = neighbouringLocationEntry.getValue();
				if (!locationDeepCopy.getNeighbouringLocations().containsKey(neighbouringLocationDeepCopy))
					locationDeepCopy.addNeighbouringLocation(neighbouringLocationDeepCopy, expense);
				if (!neighbouringLocationDeepCopy.getNeighbouringLocations().containsKey(locationDeepCopy))
					neighbouringLocationDeepCopy.addNeighbouringLocation(locationDeepCopy, expense);
			}
		}

		// check
		for (Location locationDeepCopy : locations) {
			boolean locationEquivaliantFound = false;
			for (Location location : network) {
				if (location.getName().equals(locationDeepCopy.getName()) && location.getAmount() == location.getAmount()) {
					locationEquivaliantFound = true;
					boolean neighboursAreCorrect = true;
					for (Map.Entry<Location, Integer> neighbouringLocationEntry : location.getNeighbouringLocations().entrySet()) {
						boolean neighbourFound = false;
						for (Map.Entry<Location, Integer> neighbouringLocationDeepCopyEntry : locationDeepCopy.getNeighbouringLocations().entrySet()) {
							if (neighbouringLocationEntry.getKey().getName().equals(neighbouringLocationDeepCopyEntry.getKey().getName())
									&& neighbouringLocationEntry.getValue().equals(neighbouringLocationDeepCopyEntry.getValue())
									&& neighbouringLocationEntry.getKey().getAmount() == neighbouringLocationDeepCopyEntry.getKey().getAmount()) {
								neighbourFound = true;
							}
						}
						if (!neighbourFound) neighboursAreCorrect = false;
					}

					if (!neighboursAreCorrect) {
						System.out.println("Deep copy failed to create correct neighbours.");
						return null;
					}
					break;
				}
			}
			if (!locationEquivaliantFound) {
				System.out.println("Deep copy failed.");
				return null;
			}
		}

		return locations.toArray(new Location[0]);
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
