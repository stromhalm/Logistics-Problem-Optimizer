package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.Tour;
import de.uni_oldenburg.transport.TransportNetwork;
import de.uni_oldenburg.transport.optimizers.Graph.Graph;


public class NorthWestCornerOwnOptimizer extends NorthWestCornerOptimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		this.spanningNetwork = Graph.getSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		//System.out.print(toString());
		Solution solution = new Solution(transportNetwork);
		transportNetwork.computeShortestPaths();


		for (Tour tour : doNorthWestCornerMethod(transportNetwork.getStartLocation(), transportNetwork)) {
			solution.addTour(tour);
		}

		return solution;
	}
}
