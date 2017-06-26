package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.TransportNetwork;

/**
 * The interface for all our optimizers.
 */
public interface Optimizer {

	/**
	 * @param transportNetwork A transport network for which the transport problem has to be optimized.
	 * @return The best {@link Solution} found by the implementing Optimizer.
	 */
	Solution optimizeTransportNetwork(TransportNetwork transportNetwork);

}
