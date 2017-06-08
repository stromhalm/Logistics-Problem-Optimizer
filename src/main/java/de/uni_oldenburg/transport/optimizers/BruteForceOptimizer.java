package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class implements a simple brute force solution for any transport optimization problem. Beware of the time needed by the this {@link Optimizer}.
 *
 * @see Optimizer
 */
public class BruteForceOptimizer implements Optimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		Solution solution = new Solution(transportNetwork);

		Location start = transportNetwork.getLocationByName("Hamburg");
		// iterate through transport network
		for (Location destination : transportNetwork.getLocations()) {
			if (!destination.equals(start)) { // prevent Hamburg from being added.
				AbstractTruck truck = new LargeTruck();
				Tour tour = new Tour(truck);
				if (!checkForLocationAlreadyServed(solution, destination)) {
					TourDestination tourDestination = new TourDestination(destination, destination.getAmount());
					tour.addDestination(tourDestination, computeExpense(start, destination));
					break;
				}
				solution.addTour(tour);
			}
		}

		// todo improve and make real brute force
		return solution;
	}

	/**
	 * Computes the shortest way to the destination form the start location.
	 *
	 * @param start       Location to startt at.
	 * @param destination Location to stop at.
	 * @return The expense to get to the destinations location.
	 */
	private int computeExpense(Location start, Location destination) {
		int expense = 0;
		Location lastTriedLocation = null;
		for (Map.Entry<Location, Integer> entry : start.getNeighbouringLocations().entrySet()) {
			Location location = entry.getKey();
			if (location.hasNeigbouringLocationLocation(destination)) {
				return entry.getValue();
			}
			lastTriedLocation = location;
		}
		if (expense == 0) {
			expense += computeExpense(lastTriedLocation, destination);
		}
		return expense;
	}

	/**
	 * Check whether a destination location is already served by a truck.
	 *
	 * @param solution    The solution found so far. The solution does not have to be final.
	 * @param destination The destination to check whether it is already served by a tour.
	 * @return A boolean value indicating whether the destination is served already or not.
	 */
	private boolean checkForLocationAlreadyServed(Solution solution, Location destination) {
		for (Tour tour : solution.getTruckTours()) {
			for (TourDestination tourDestination : tour.getTourDestinations()) {
				if (tourDestination.getDestination().equals(destination)) return true;
			}
		}
		return false;
		// TODO modify the method to return the tour that serves the location and whether the tour serves the location in full.
	}
}
