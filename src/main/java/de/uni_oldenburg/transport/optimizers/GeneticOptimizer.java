package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Example optimizer
 */
public class GeneticOptimizer implements Optimizer {

	TransportNetwork transportNetwork;

	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		this.transportNetwork = transportNetwork;
		Solution solution = new Solution(transportNetwork);

		Location startLocation = transportNetwork.getStartLocation();
		Location currentLocation = startLocation;
		Tour tour = new Tour(new LargeTruck(), startLocation);

		/*do {
			HashMap<Location, Double> scentMap = getSummedScentMap(0.0);

			Location bestNeighbor;
			int bestScent;

			for (HashMap.Entry neighbor : currentLocation.getNeighbouringLocations().entrySet()) {
				if (scentMap.get((Location) neighbor.getValue()) > bestScent) {
					bestNeighbor =
				}
			}

			TourDestination nextDestination =
		} while (startLocation != currentLocation)

		for (HashMap.Entry source : scentMap.entrySet()) {
			Location location = (Location) source.getKey();
			System.out.println("Scent for " + location.getName() + " is " + (Double) source.getValue());
		}*/

		return solution;
	}

	private HashMap<Location, Double> getSummedScentMap(double driftToStart) {

		HashMap<Location, Double> summedScentMap = new HashMap();

		// Sum scents for all the locations
		for (Location scentSource : transportNetwork.getLocations()) {

			// Build scentMap for every location
			int startScent = 20;
			int scent;
			if (scentSource == transportNetwork.getStartLocation()) {
				scent = startScent;
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
				if (scentSource != transportNetwork.getStartLocation()) {
					additionalScent = (1-driftToStart)*additionalScent;
				}

				summedScentMap.put((Location) singleScentSource.getKey(), currentScent + additionalScent);
			}
		}
		return summedScentMap;
	}

	private HashMap<Location, Double> markRecursively(HashMap<Location, Double> scentMap, Location location, double scent) {

		// Mark the current location
		if (scentMap.get(location) == null || scentMap.get(location) < scent) {
			scentMap.put(location, scent);

			for (HashMap.Entry neighbor : location.getNeighbouringLocations().entrySet()) {
				scentMap = markRecursively(scentMap, (Location) neighbor.getKey(), scent*Math.pow(0.995, (int) neighbor.getValue()));
			}
		}
		return scentMap;
	}
}