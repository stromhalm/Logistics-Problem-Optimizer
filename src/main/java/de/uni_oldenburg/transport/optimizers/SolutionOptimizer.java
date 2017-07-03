package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Optimizer that improves valid solutions from other optimizers
 */
public class SolutionOptimizer implements Optimizer {

	TransportNetwork transportNetwork;
	int maximumTruckCapacity = LargeTruck.CAPACITY;

	/**
	 * Optimizer that improves valid solutions from other optimizers
	 *
	 * @param transportNetwork A transport network for which the transport problem has to be optimized.
	 * @return The solution found by this optimizer
	 */
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		this.transportNetwork = transportNetwork;

		Solution solution = getGoodSolution();
		double consumptionBefore = solution.getConsumption();

		// Try removing every single tour
		ArrayList<Tour> allTours = (ArrayList<Tour>) solution.getTruckTours().clone();
		for (Tour tour1 : allTours) {
			solution.removeTour(tour1);
			for (Tour tour2 : allTours) {

				if (tour1 != tour2) solution.removeTour(tour2);

				Solution filledUpSolution = getFilledUpSolution(solution);

				if (filledUpSolution.isValid(false)) {
					double consumptionAfter = filledUpSolution.getConsumption();
					if (consumptionAfter < consumptionBefore) {
						return filledUpSolution;
					}
				}

				if (tour1 != tour2) solution.addTour(tour2);
			}
			solution.addTour(tour1);
		}
		return solution;
	}

	private Solution getFilledUpSolution(Solution oldSolution) {

		Solution filledUpSolution = new Solution(transportNetwork);
		HashMap<Location, Integer> openDeliveries = oldSolution.getOpenDeliveries();

		for (Tour oldTour : oldSolution.getTruckTours()) {
			Tour filledUpTour = new Tour(oldTour.getStartLocation());
			int addedTourUnload = 0;

			for (TourDestination oldDestination : oldTour.getTourDestinations()) {

				Location location = oldDestination.getDestination();
				int newLocationUnload = oldDestination.getUnload();
				int additionalUnload = 0;

				if (openDeliveries.containsKey(location)) {
					additionalUnload = getRemainingCapacityForDestination(oldTour, location, addedTourUnload);
					newLocationUnload += additionalUnload;
					addedTourUnload += additionalUnload;

					openDeliveries.put(location, openDeliveries.get(location) - additionalUnload);
					if (openDeliveries.get(location) == 0) openDeliveries.remove(location);
				}

				TourDestination filledUpDestination = new TourDestination(location, newLocationUnload);
				filledUpTour.addDestination(filledUpDestination);
			}
			filledUpSolution.addTour(filledUpTour);
		}
		return filledUpSolution;
	}

	private int getRemainingCapacityForDestination(Tour tour, Location location, int addedTourUnload) {
		for (TourDestination destination : tour.getTourDestinations()) {
			if (destination.getDestination() == location) {
				return (maximumTruckCapacity - tour.getTourLoad() - addedTourUnload);
			}
		}
		return 0;
	}

	/**
	 * Get an already good solution from another optimizer to improve it
	 *
	 * @return A valid solution
	 */
	private Solution getGoodSolution() {
		Optimizer pheromoneOptimizer = new PheromoneOptimizer();
		Solution solution = pheromoneOptimizer.optimizeTransportNetwork(transportNetwork);
		return solution;
	}
}
