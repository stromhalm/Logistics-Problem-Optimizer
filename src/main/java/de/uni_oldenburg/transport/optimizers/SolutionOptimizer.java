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
		for (Tour tour : allTours) {
			solution.removeTour(tour);

			if (solution.isValid() && solution.getConsumption() < consumptionBefore) {
				break;
			}

			solution.addTour(tour);
		}

		return solution;
	}

	/**
	 * Get an already good solution from another optimizer to improve it
	 *
	 * @return A valid solution
	 */
	private Solution getGoodSolution() {
		// Save original amounts
		int[] originalAmounts = new int[transportNetwork.getLocations().length];
		for (int i = 0; i < transportNetwork.getLocations().length; i++) {
			originalAmounts[i] = transportNetwork.getLocations()[i].getAmount();
		}

		Optimizer pheromoneOptimizer = new PheromoneOptimizer();
		Solution solution = pheromoneOptimizer.optimizeTransportNetwork(transportNetwork);

		// Rebuild original amounts
		for (int i = 0; i < transportNetwork.getLocations().length; i++) {
			transportNetwork.getLocations()[i].setAmount(originalAmounts[i]);
		}
		return solution;
	}
}
