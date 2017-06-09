package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.lang.reflect.Array;
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
				ArrayList<Tour> tours = new ArrayList<>();
				if (!checkForLocationAlreadyServed(solution, destination)) {
					getPathAndExpenseToDestination(solution, tours, start, destination);
				}
				for (Tour tour : tours) {
					solution.addTour(tour);
				}
			}
		}

		// todo improve and make real brute force
		return solution;
	}

	private void getPathAndExpenseToDestination(Solution solution, ArrayList<Tour> tours, Location start, Location destination) {
		ArrayList<Location> visitedLocations = new ArrayList<>();
		computeGeneralTourToDestination(visitedLocations, new ArrayList<Location>(), start, destination);

		int totalAmount = 0;
		for (Location visitedLocation : visitedLocations) {
			if (!checkForLocationAlreadyServed(solution, visitedLocation)) {
				totalAmount += visitedLocation.getAmount();
			}
		}

		if (totalAmount > MediumTruck.CAPACITY && totalAmount <= LargeTruck.CAPACITY) {
			tours.add(new Tour(new LargeTruck(), start));
		} else if (totalAmount > SmallTruck.CAPACITY && totalAmount <= MediumTruck.CAPACITY) {
			tours.add(new Tour(new MediumTruck(), start));
		} else if (totalAmount <= SmallTruck.CAPACITY) {
			tours.add(new Tour(new MediumTruck(), start));
		} else {
			int totalsAmountCopy = totalAmount;
			do {
				tours.add(new Tour(new LargeTruck(), start));
				totalsAmountCopy -= LargeTruck.CAPACITY;
			} while (totalsAmountCopy > 0);
		}

		int x = 0;
		int amountPossible = tours.get(x).getTruck().getCapacity();
		Tour tour = tours.get(x);

		for (int i = 0; i < visitedLocations.size(); i++) {
			Location location = visitedLocations.get(visitedLocations.size() - 1 - i);
			if (!checkForLocationAlreadyServed(solution, location)) {
				System.out.println("LKW " + x + " drives to " + location.getName() + " and unloads " + location.getAmount() + " at tour number " + x);

				TourDestination tourDestination = new TourDestination(location, location.getAmount());
				// todo compute expense again
				tour.addDestination(tourDestination, 10);
				amountPossible -= location.getAmount();
				if (amountPossible <= 0 && i != visitedLocations.size() - 1 || visitedLocations.size() - 2 - i >= 0 && amountPossible < visitedLocations.get(visitedLocations.size() - 2 - i).getAmount()) {
					tour = tours.get(++x);
					amountPossible = tours.get(x).getTruck().getCapacity();
				}
			} else {
				// TODO compute the expense nonetheless
			}
		}
		System.out.println("With " + 10 + " kilometers to drive.\n\n");

	}

	private boolean computeGeneralTourToDestination(ArrayList<Location> visitedLocations, ArrayList<Location> alreadyVisited, Location start, Location destination) {

		for (Location location : alreadyVisited) {
			if (location.equals(start)) return false; // break
		}
		alreadyVisited.add(start);
		if (start.hasNeigbouringLocationLocation(destination)) {
			visitedLocations.add(destination);
			return true;
		} else {
			for (Map.Entry<Location, Integer> entry : start.getNeighbouringLocations().entrySet()) {
				Location location = entry.getKey();
				if (computeGeneralTourToDestination(visitedLocations, alreadyVisited, location, destination)) {
					visitedLocations.add(location);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Computes the shortest way to the destination form the start location.
	 *
	 * @param start            Location to startt at.
	 * @param destination      Location to stop at.
	 * @param alreadyVisited   The list of already visited location in the recursion process.
	 * @param visitedLocations The list of already unloaded locations.
	 * @return The expense to get to the destinations location.
	 */
	private int computeExpense(Location start, Location destination, ArrayList<Location> alreadyVisited, ArrayList<Location> visitedLocations) {
		int expense = 0;
		for (Location location : alreadyVisited) {
			if (location.equals(start)) return -1; // break
		}
		alreadyVisited.add(start);
		if (start.hasNeigbouringLocationLocation(destination)) {
			visitedLocations.add(destination);
			return start.getNeighbouringLocations().get(destination); // is the expense
		} else {
			for (Map.Entry<Location, Integer> entry : start.getNeighbouringLocations().entrySet()) {
				Location location = entry.getKey();
				expense = entry.getValue();
				int returnVL = computeExpense(location, destination, alreadyVisited, visitedLocations);
				if (returnVL > 0) {
					visitedLocations.add(location);
					expense += returnVL;
					break;
				}
			}
		}
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
				if (tourDestination.getDestination().equals(destination)) {
					if (tourDestination.getUnload() == tourDestination.getDestination().getAmount()) {
						return true;
					}

				}
			}
		}
		return false;
		// TODO modify the method to return the tour that serves the location and whether the tour serves the location in full.
	}
}
