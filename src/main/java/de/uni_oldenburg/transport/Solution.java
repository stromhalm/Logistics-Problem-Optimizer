package de.uni_oldenburg.transport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Solution represents a solution to our optimization problem.
 * It consists of multiple {@link Tour}s.
 */
public class Solution {

	private TransportNetwork transportNetwork;
	private ArrayList<Tour> truckTours = new ArrayList<>();

	/**
	 * Simple Constructor
	 *
	 * @param transportNetwork The transport network this is a solution for
	 */
	public Solution(TransportNetwork transportNetwork) {
		this.transportNetwork = transportNetwork;
	}

	/**
	 * Add a tour to this solution
	 *
	 * @param tour The tour to add
	 */
	public void addTour(Tour tour) {
		truckTours.add(tour);
	}

	/**
	 * Remove a tour from this solution
	 *
	 * @param tour The tour to remove
	 * @return true if this solution contained the tour
	 */
	public boolean removeTour(Tour tour) {
		return truckTours.remove(tour);
	}

	/**
	 * Checks if all delivery targets have been fulfilled and is omitted.
	 *
	 * @param print print error messages
	 * @return true iff valid
	 */
	public boolean isValid(boolean print) {

		HashMap<Location, Integer> deliveries = new HashMap<>();

		// Check if all delivery targets have been fulfilled
		for (Tour tour : truckTours) {

			// ckeck if truck was overloaded and returned to start
			if (!tour.isValid(print)) return false;

			for (TourDestination tourDestination : tour.getTourDestinations()) {
				if (deliveries.containsKey(tourDestination.getDestination())) {
					deliveries.put(tourDestination.getDestination(), deliveries.get(tourDestination.getDestination()) + tourDestination.getUnload());
				} else {
					deliveries.put(tourDestination.getDestination(), tourDestination.getUnload());
				}
			}
		}
		for (Location location : transportNetwork.getLocations()) {

			System.out.println(location + ": " + location.getAmount());

			if (location.getAmount() != 0) {
				if (!deliveries.containsKey(location) || location.getAmount() != deliveries.get(location)) {
					// error message servicing
					if (print) {
						int amountDelivered = 0;
						if (deliveries.containsKey(location)) amountDelivered = deliveries.get(location);
						System.out.println(location.getName() + " needs " + location.getAmount() + " but gets " + amountDelivered + " delivered.");
					}
					return false;
				}
			}
		}
		System.out.println();
		return true;
	}

	/**
	 * Get the total consumption
	 *
	 * @return Total consumption
	 */
	public double getConsumption() {
		double consumption = 0;
		for (Tour tour : truckTours) {
			consumption += tour.getConsumption();
		}
		return consumption;
	}

	/**
	 * Output this solution
	 *
	 * @return The output as a String
	 */
	@Override
	public String toString() {
		String output = "";
		for (Tour tour : truckTours) {
			output += tour.toString();
		}
		return output;
	}

	public ArrayList<Tour> getTruckTours() {
		return truckTours;
	}

	public double getTotalConsumption() {
		double consumption = 0;
		for (Tour tour : truckTours) {
			consumption += tour.getConsumption();
		}
		return consumption;
	}
}
