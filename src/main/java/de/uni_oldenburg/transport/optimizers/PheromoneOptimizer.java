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

	TransportNetwork transportNetwork;
	Location startLocation;

	/**
	 *
	 * @param transportNetwork A transport network for which the transport problem has to be optimized.
	 * @return The solution found by this optimizer
	 */
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		this.transportNetwork = transportNetwork;
		this.startLocation = transportNetwork.getStartLocation();
		Solution solution = new Solution(transportNetwork);

		Location currentLocation = startLocation;
		int maximumTruckCapacity = LargeTruck.CAPACITY;

		// While work to do
		while (getLocationWithPositiveAmount() != null) {

			Tour tour = new Tour(startLocation);
			int tourload = 0;

			// Go to highest scent
			do {

				// Get scentMap
				HashMap<Location, Double> scentMap = getSummedScentMap((double) tourload/maximumTruckCapacity);

				// Find neighbor with highest scent
				Location bestNeighbor = null;
				double bestScent = 0;
				for (HashMap.Entry neighbor : currentLocation.getNeighbouringLocations().entrySet()) {
					if (scentMap.get(neighbor.getKey()) > bestScent) {
						bestNeighbor = (Location) neighbor.getKey();
						bestScent = scentMap.get(neighbor.getKey());
					}
				}

				// Calculate amounts
				int nextLocationUnload = Math.min(bestNeighbor.getAmount(), maximumTruckCapacity - tourload);
				bestNeighbor.setAmount(bestNeighbor.getAmount() - nextLocationUnload);
				tourload += nextLocationUnload;

				// Add destination
				TourDestination nextDestination = new TourDestination(bestNeighbor, nextLocationUnload);
				tour.addDestination(nextDestination);

				currentLocation = bestNeighbor;

			} while (startLocation != currentLocation);

			solution.addTour(tour);
		}

		return solution;
	}

	/**
	 * Calculate a scent map with a driven drift to the start location
	 * @param driftToStart Drift to start location [0..1]. The higher the higher is the drift
	 * @return A scent map
	 */
	private HashMap<Location, Double> getSummedScentMap(double driftToStart) {

		HashMap<Location, Double> summedScentMap = new HashMap();

		// Sum scents for all the locations
		for (Location scentSource : transportNetwork.getLocations()) {

			// Build scentMap for every location
			double startScent = 20;
			double scent;
			if (scentSource == startLocation) {
				scent = startScent*driftToStart;
			} else {
				scent = scentSource.getAmount();
			}

			HashMap<Location, Double> locationScentMap = markRecursively(new HashMap<>(), scentSource, scent);

			// Add to summedScentMap
			for (HashMap.Entry singleScentSource : locationScentMap.entrySet()) {
				double currentScent = 0;
				if (summedScentMap.get(singleScentSource.getKey()) != null) {
					currentScent = summedScentMap.get(singleScentSource.getKey());
				}

				double additionalScent = (Double) singleScentSource.getValue();

				// Drift to start
				if (scentSource != startLocation) {
					additionalScent = (1-driftToStart)*additionalScent;
				}

				summedScentMap.put((Location) singleScentSource.getKey(), currentScent + additionalScent);
			}
		}
		return summedScentMap;
	}

	/**
	 * Recursive function to calculate the recursive scents on the map
	 * @param scentMap The initial scent map
	 * @param location The location with a new scent
	 * @param scent The new scents value
	 * @return The refreshed scent map
	 */
	private HashMap<Location, Double> markRecursively(HashMap<Location, Double> scentMap, Location location, double scent) {

		// Mark the current location and its neighbors
		if (scentMap.get(location) == null || scentMap.get(location) < scent) {
			scentMap.put(location, scent);

			for (HashMap.Entry neighbor : location.getNeighbouringLocations().entrySet()) {
				scentMap = markRecursively(scentMap, (Location) neighbor.getKey(), scent*Math.pow(0.995, (int) neighbor.getValue()));
			}
		}
		return scentMap;
	}

	/**
	 * Finds a location that has an amount > 0
	 * @return Any location with amount > 0 else null
	 */
	private Location getLocationWithPositiveAmount() {
		for (Location location : transportNetwork.getLocations()) {
			if (location.getAmount() > 0) return location;
		}
		return null;
	}
}