package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.Tour;
import de.uni_oldenburg.transport.TransportNetwork;
import de.uni_oldenburg.transport.optimizers.Graph.Graph;
import de.uni_oldenburg.transport.optimizers.Graph.Vertice;

import java.util.ArrayList;


public class NorthWestCornerOwnOptimizer extends NorthWestCornerOptimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		this.minimalSpanningNetwork = Graph.getMinimalSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		System.out.print(toString());
		Solution solution = new Solution(transportNetwork);

		for (Tour tour : doNorthWestCornerMethod(transportNetwork.getStartLocation())) {
			solution.addTour(tour);
		}

		return solution;
	}
}
