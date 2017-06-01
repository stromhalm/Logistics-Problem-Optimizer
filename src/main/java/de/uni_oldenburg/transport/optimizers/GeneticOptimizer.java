package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.TransportNetwork;

/**
 * Example optimizer
 */
public class GeneticOptimizer implements Optimizer {

	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		return new Solution(transportNetwork);
	}
}