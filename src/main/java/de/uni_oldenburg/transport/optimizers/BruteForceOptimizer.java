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
				Tour tour = new Tour(truck, start);
				if (!checkForLocationAlreadyServed(solution, destination)) {
					TourDestination tourDestination = new TourDestination(destination, destination.getAmount());
					ArrayList<Location> alreadyVisited = new ArrayList<>();
					System.out.println("From Hamburg \n");
					int expense = computeExpense(start, destination, 0, alreadyVisited);
					tour.addDestination(tourDestination, expense);
					System.out.println("With " + expense + " kilometers to drive.\n\n");
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
	 * @param start         Location to startt at.
	 * @param destination   Location to stop at.
	 * @param recursionDeep Is the deep of the recursion
	 * @return The expense to get to the destinations location.
	 */
	private int computeExpense(Location start, Location destination, int recursionDeep, ArrayList<Location> alreadyVisited) {
		int expense = 0;
		for (Location location : alreadyVisited) {
			if (location.equals(start)) return -1; // break
		}
		alreadyVisited.add(start);
		if (start.hasNeigbouringLocationLocation(destination)) {
			return start.getNeighbouringLocations().get(destination); // is the expense
		} else {
			for (Map.Entry<Location, Integer> entry : start.getNeighbouringLocations().entrySet()) {
				Location location = entry.getKey();
				expense = entry.getValue();
				int returnVL = computeExpense(location, destination, recursionDeep + 1, alreadyVisited);
				if (returnVL > 0) {
					System.out.println(" to " + location.getName());
					expense += returnVL;
					break;
				}
			}
		}
		// TODO implement
		if (expense == 0) {
			return -1;
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
