package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class implements a simple brute force solution for any transport optimization problem. Beware of the time needed by the this {@link Optimizer}.
 *
 * @see Optimizer
 */
public class ShortestPathOptimizer implements Optimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		transportNetwork.computeShortestPaths();
		Solution solution = new Solution(transportNetwork);

		Location start = transportNetwork.getStartLocation();
		ArrayList<Location> alreadyServed = new ArrayList<>();
		alreadyServed.add(start);
		ArrayList<Tour> tours = new ArrayList<>();
		shortestPathDelivering(start, alreadyServed, transportNetwork, tours, 0);

		for (Tour tour : tours) {
			solution.addTour(tour);
		}

		// todo improve and make real brute force
		return solution;
	}

	private void shortestPathDelivering(Location start, ArrayList<Location> alreadyServed, TransportNetwork transportNetwork, ArrayList<Tour> tours, int counter) {
		if (alreadyServed.size() == transportNetwork.getLocations().length) return;

		for (Map.Entry<Location, Integer> neighbouringLocationEntry : start.getNeighbouringLocations().entrySet()) {
			if (!alreadyServed(alreadyServed, neighbouringLocationEntry.getKey())) {
				LinkedHashMap<Location, Integer> path = transportNetwork.getShortestPath(transportNetwork.getStartLocation(), neighbouringLocationEntry.getKey());
				tours.addAll(deliverOnPath(path, transportNetwork.getStartLocation(), alreadyServed, transportNetwork));
			}
		}

		shortestPathDelivering(alreadyServed.get(counter + 1), alreadyServed, transportNetwork, tours, ++counter);
	}

	private ArrayList<Tour> deliverOnPath(LinkedHashMap<Location, Integer> path, Location start, ArrayList<Location> alreadyServed, TransportNetwork transportNetwork) {
		ArrayList<Tour> tours = new ArrayList<>();

		int expense = -1;
		Tour tour = new Tour(start);
		for (Map.Entry<Location, Integer> subPath : path.entrySet()) {
			if (expense < 0) {
				expense = subPath.getValue();
			} else {
				if (alreadyServed(alreadyServed, subPath.getKey())) {
					tour.addDestination(new TourDestination(subPath.getKey(), 0));
				} else {

					// prepare the new tour
					Tour newTour = new Tour(start);
					for (int i = 0; i < tour.getTourDestinations().length; i++) {
						newTour.addDestination(tour.getTourDestinations()[i]);
					}
					newTour.addDestination(new TourDestination(subPath.getKey(), 0));

					// finish the current tour
					tour.addDestination(new TourDestination(subPath.getKey(), subPath.getKey().getAmount()));
					alreadyServed.add(subPath.getKey());
					driveHome(tour, transportNetwork.getShortestPath(subPath.getKey(), start));
					tours.add(tour);
					tour = newTour;
				}


			}
		}
		//System.out.print("\n");
		return tours;
	}

	private void driveHome(Tour tour, LinkedHashMap<Location, Integer> path) {
		int expense = -1;
		for (Map.Entry<Location, Integer> subPath : path.entrySet()) {
			if (expense < 0) {
				expense = subPath.getValue();
			} else {
				tour.addDestination(new TourDestination(subPath.getKey(), 0));
			}
		}

	}

	private boolean alreadyServed(ArrayList<Location> alreadyServed, Location start) {
		for (Location location : alreadyServed) {
			if (location.getName().equals(start.getName())) return true;
		}
		return false;
	}
}
