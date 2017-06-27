package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Graph;
import de.uni_oldenburg.transport.optimizers.Graph.Kruskal;


public class NorthWestCornerKruskalOptimizer extends NorthWestCornerOptimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		Kruskal kruskal = new Kruskal(transportNetwork);
		kruskal.findMST();
		TransportNetwork transportNetworkKruskal = kruskal.getLocationsMST();

		this.spanningNetwork = Graph.getSpanningNetwork(transportNetworkKruskal.getLocations(), transportNetworkKruskal.getStartLocation());
		System.out.print(toString());
		Solution solution = new Solution(transportNetworkKruskal);

		for (Tour tour : doNorthWestCornerMethod(transportNetwork.getStartLocation(), transportNetwork)) {
			solution.addTour(tour);
		}

		return solution;
	}
}
