package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  Optimizer that works with scent traces (pheromones)
 */
public class PheromoneOptimizer implements Optimizer {

	// Parameters for the optimizer (found by brute force)
	final double START_SCENT = 200;  // The amount of scent that the startLocation sends at most
	final double SCENT_FADE = 0.993; // The fade of scent per km

	TransportNetwork transportNetwork;
	Location startLocation;
	HashMap<Location, Integer> deliveries = new HashMap<>();

	/**
	 * Optimize the given network with a pheromone approach.
	 * Try minimizing nearby destinations if possible.
	 *
	 * @param transportNetwork  A transport network for which the transport problem has to be optimized.
	 * @return                  The solution found by this optimizer
	 */
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		return optimizeTransportNetwork(transportNetwork, true);
	}

	/**
	 * Optimize the given network with a pheromone approach.
	 *
	 * @param transportNetwork  A transport network for which the transport problem has to be optimized.
	 * @param minimizeNearby    Should try to minimize nearby destinations
	 * @return                  The solution found by this optimizer
	 */
	private Solution optimizeTransportNetwork(TransportNetwork transportNetwork, boolean minimizeNearby) {

		this.transportNetwork = transportNetwork;
		this.startLocation = transportNetwork.getStartLocation();
		Solution solution = new Solution(transportNetwork);
		Location currentLocation = startLocation;

		// While work to do
		while (solution.getOpenDeliveries().size() > 0) {

			Tour tour = new Tour(startLocation);
			HashMap <Location, Integer> tourLocationVisits = new HashMap<>();
			int tourLoad = 0;

			// Go to highest scent
			do {

				// Get scentMap
				HashMap<Location, Double> scentMap = getSummedScentMap((double) tourLoad/LargeTruck.CAPACITY);

				// Find neighbor with highest scent
				Location bestNeighbor = null;
				double bestScent = 0;
				for (HashMap.Entry<Location, Integer> neighbor : currentLocation.getNeighbouringLocations().entrySet()) {

					Location neighborLocation = neighbor.getKey();
					double neighborScent = scentMap.get(neighborLocation);
					if (minimizeNearby) {
						neighborScent = scentMap.get(neighborLocation)*Math.pow(SCENT_FADE, neighbor.getValue());
					}

					if (neighborScent > bestScent) {
						bestNeighbor = neighborLocation;
						bestScent = neighborScent;
					}
				}

				// Calculate amounts
				int nextLocationUnload = Math.min(getAmountLeft(bestNeighbor), LargeTruck.CAPACITY - tourLoad);
				deliverAmount(bestNeighbor, nextLocationUnload);
				tourLoad += nextLocationUnload;

				// Add destination
				TourDestination nextDestination = new TourDestination(bestNeighbor, nextLocationUnload);
				tour.addDestination(nextDestination);
				currentLocation = bestNeighbor;

				// When the tour gets too long, try again without nearby minimization
				if (tour.getTourDestinations().length > transportNetwork.getLocations().length) {
					System.out.println("Trying again without nearby minimization");
					PheromoneOptimizer pheromoneOptimizer = new PheromoneOptimizer();
					return pheromoneOptimizer.optimizeTransportNetwork(transportNetwork, false);
				}
			} while (startLocation != currentLocation);

			solution.addTour(tour);
		}

		return solution;
	}

	/**
	 * Calculate a scent map with a driven drift to the start location
	 *
	 * @param driftToStart  Drift to start location [0..1]. The higher the higher is the drift
	 * @return              A scent map
	 */
	private HashMap<Location, Double> getSummedScentMap(double driftToStart) {

		HashMap<Location, Double> summedScentMap = new HashMap<>();

		// Sum scents for all the locations
		for (Location scentSource : transportNetwork.getLocations()) {

			// Build scentMap for every location
			double scent;
			if (scentSource == startLocation) {
				scent = START_SCENT*driftToStart;
			} else {
				scent = getAmountLeft(scentSource);
			}

			HashMap<Location, Double> locationScentMap = markRecursively(new HashMap<>(), scentSource, scent);

			// Add to summedScentMap
			for (HashMap.Entry<Location, Double> singleScentSource : locationScentMap.entrySet()) {
				double currentScent = 0;
				if (summedScentMap.get(singleScentSource.getKey()) != null) {
					currentScent = summedScentMap.get(singleScentSource.getKey());
				}

				double additionalScent = singleScentSource.getValue();

				// Drift to start
				if (scentSource != startLocation) {
					additionalScent = (1-driftToStart)*additionalScent;
				}

				summedScentMap.put(singleScentSource.getKey(), currentScent + additionalScent);
			}
		}
		return summedScentMap;
	}

	/**
	 * Recursive function to calculate the scents on the map
	 *
	 * @param scentMap  The initial scent map
	 * @param location  The location with a new scent
	 * @param scent     The new scents value
	 * @return          The refreshed scent map
	 */
	private HashMap<Location, Double> markRecursively(HashMap<Location, Double> scentMap, Location location, double scent) {

		// Mark the current location and its neighbors
		if (scentMap.get(location) == null || scentMap.get(location) < scent) {
			scentMap.put(location, scent);

			for (HashMap.Entry<Location, Integer> neighbor : location.getNeighbouringLocations().entrySet()) {
				scentMap = markRecursively(scentMap, neighbor.getKey(), scent*Math.pow(SCENT_FADE, neighbor.getValue()));
			}
		}
		return scentMap;
	}

	/**
	 * Get the amount of open deliveries for a given location
	 *
	 * @param location The location in this problems transportNetwork
	 * @return The amount of open deliveries for the location
	 */
	private int getAmountLeft(Location location) {
		if (deliveries.containsKey(location)) {
			return (location.getAmount() - deliveries.get(location));
		} else {
			return location.getAmount();
		}
	}

	/**
	 * Substracts the given amount from the open deliveries in this problems network
	 * without changing the transportNetwork object itself. Use getAmountLeft() to get
	 * the open amount.
	 *
	 * @param location  The location in this problems transportNetwork
	 * @param amount    The amount of deliveries to substract from the location
	 */
	private void deliverAmount(Location location, int amount) {
		if (deliveries.containsKey(location)) {
			deliveries.put(location, deliveries.get(location) + amount);
		} else {
			deliveries.put(location, amount);
		}
	}
}
