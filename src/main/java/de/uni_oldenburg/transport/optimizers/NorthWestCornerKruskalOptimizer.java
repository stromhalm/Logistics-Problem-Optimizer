package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Graph;
import de.uni_oldenburg.transport.optimizers.Graph.Kruskal;
import de.uni_oldenburg.transport.optimizers.Graph.Vertex;

import java.util.ArrayList;


public class NorthWestCornerKruskalOptimizer extends NorthWestCornerOptimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
		transportNetwork.computeShortestPaths();

		Kruskal kruskal = new Kruskal(new TransportNetwork(transportNetwork.getLocationsDeepCopy()));
		kruskal.findMST();
		TransportNetwork transportNetworkKruskal = kruskal.getLocationsMST();

		this.spanningNetwork = Graph.getSpanningNetwork(transportNetworkKruskal.getLocations(), transportNetworkKruskal.getStartLocation());
		//System.out.print(toString());

		for (ArrayList<Vertex> vertices : spanningNetwork) {
			for (Vertex vertex : vertices) {
				for (Location location : transportNetwork.getLocations()) {
					if (location.getName().equals(vertex.getName())) {
						vertex.setLocationReference(location); // reset the location
					}
				}
			}
		}


		Solution solution = new Solution(transportNetwork);


		for (Tour tour : doNorthWestCornerMethod(transportNetwork.getStartLocation(), transportNetwork)) {
			solution.addTour(tour);
		}


		return solution;
	}
}
