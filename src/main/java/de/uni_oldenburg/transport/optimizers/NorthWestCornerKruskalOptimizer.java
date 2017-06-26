package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Graph;
import de.uni_oldenburg.transport.optimizers.Graph.Kruskal;
import de.uni_oldenburg.transport.optimizers.Graph.Vertice;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;


public class NorthWestCornerKruskalOptimizer extends NorthWestCornerOptimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		Kruskal kruskal = new Kruskal(transportNetwork);
		kruskal.findMST();
		transportNetwork = kruskal.getLocationsMST();

		this.minimalSpanningNetwork = Graph.getMinimalSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		System.out.print(toString());
		Solution solution = new Solution(transportNetwork);

		for (Tour tour : doNorthWestCornerMethod(transportNetwork.getStartLocation())) {
			solution.addTour(tour);
		}

		return solution;
	}
}
