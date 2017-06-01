package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.TransportNetwork;

/**
 * The interface for all our optimizers
 */
public interface Optimizer {

	Solution optimizeTransportNetwork(TransportNetwork transportNetwork);

}
