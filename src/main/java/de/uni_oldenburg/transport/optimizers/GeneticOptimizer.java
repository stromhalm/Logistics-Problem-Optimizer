package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Example optimizer
 */
public class GeneticOptimizer implements Optimizer {

	TransportNetwork transportNetwork;

	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		this.transportNetwork = transportNetwork;

		transportNetwork.buildShortestPaths();
		Location startLocation = transportNetwork.getStartLocation();
		Solution solution = new Solution(transportNetwork);

		// While work to do
		Location location = getLocationWithPositiveAmount();
		while (location != null) {

			AbstractTruck truck = new LargeTruck();
			int tourload = 0;
			Tour tour = new Tour(truck, startLocation);
			Location truckLocation = startLocation;

			// While truck not overloaded
			while (tourload < truck.getCapacity()) {

				// Choose direction
				int direction = ThreadLocalRandom.current().nextInt(0, 5 + 1);
				direction = direction % (truckLocation.getNeighbouringLocations().size());

				Location nextLocation = (Location) truckLocation.getNeighbouringLocations().keySet().toArray()[direction];
				int nextLocationDistance = truckLocation.getNeighbouringLocations().get(nextLocation);
				int nextLocationCost = nextLocationDistance*(truck.getConsumption()/100);
				int nextLocationUnload = Math.min(nextLocation.getAmount(), truck.getCapacity()-tourload);
				nextLocation.setAmount(nextLocation.getAmount()-nextLocationUnload);
				tourload += nextLocationUnload;

				TourDestination tourDestination = new TourDestination(nextLocation, nextLocationUnload);
				tour.addDestination(tourDestination, nextLocationCost);
				truckLocation = nextLocation;

				// TODO: Back to start
			}

			System.out.println(tour.toString());

			solution.addTour(tour);
		}


		return solution;
	}

	private Location getLocationWithPositiveAmount() {
		for (Location location : transportNetwork.getLocations()) {
			if (location.getAmount() > 0) return location;
		}
		return null;
	}
}