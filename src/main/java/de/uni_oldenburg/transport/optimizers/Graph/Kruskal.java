package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Kruskal {

	private ArrayList<Vertice> vertices;
	private ArrayList<Edge> edges;

	private int highestEdgeWeight;

	private ArrayList<Edge> edgesMST;

	public Kruskal(TransportNetwork transportNetwork) {
		Location[] locations = transportNetwork.getLocations();
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		highestEdgeWeight = 0;

		Vertice vertice = new Vertice(transportNetwork.getStartLocation(), null, 0);
		vertices.add(vertice);
		fillEdgesAndVertices(vertice, locations.length - 1, 0);

		for (Edge edge : edges) {
			System.out.println(edge.getVertice1().getName() + " with " + edge.getWeight() + " to " + edge.getVertice2().getName());
		}
	}

	public Location[] getLocationsMST() {
		ArrayList<Location> locations = new ArrayList<>();
		System.out.println();
		for (Edge edge : edgesMST) {
			System.out.println(edge.getVertice1().getName() + " with " + edge.getWeight() + " to " + edge.getVertice2().getName());
		}
		// TODO implement

		return (Location[]) locations.toArray();
	}

	public void findMST() {
		edgesMST = new ArrayList<>();
		while (edges.size() != 0) {
			int lowestEdge = findLowestEdge();
			ArrayList<Edge> edgesByWeight = getAllEdgesByWeight(lowestEdge);
			for (Edge edgeByWeight : edgesByWeight) {
				if (!addingProducesCycle(edgeByWeight, edgesMST)) {
					edgesMST.add(edgeByWeight);
				}
			}
			removeEdges(edgesByWeight);
		}
	}

	private boolean addingProducesCycle(Edge edgeByWeight, ArrayList<Edge> edgesMST) {


		for (int i = 0; i < edgesMST.size(); i++) {
			ArrayList<Edge> edgesMSTCopy = new ArrayList<>(edgesMST);
			Edge start = edgesMSTCopy.get(i);

			edgesMSTCopy.remove(start);
			edgesMSTCopy.add(edgeByWeight);

			Edge next = edgesMSTCopy.get((i + 1) % edgesMST.size());

			if (hasCycle(edgesMSTCopy, start, next, 0, edgesMST.size() - 1, new ArrayList<>())) return true;
			System.out.println();
		}
		return false;
	}

	private boolean hasCycle(ArrayList<Edge> edgeMST, Edge start, Edge next, int edgesChecked, int maxEdgesToCheck, ArrayList<Edge> alreadyCheckedEdges) {
		if (next == null) return false;

		if (start.getVertice1().getName().equals(next.getVertice2().getName()) && alreadyCheckedEdges.size() >= 1
				|| start.getVertice2().getName().equals(next.getVertice1().getName()) && alreadyCheckedEdges.size() >= 1) {
			System.out.println(start.getVertice1().getName() + " to " + start.getVertice2().getName() + " (compared to) " + next.getVertice1().getName() + " to " + next.getVertice2().getName());
			return true;
		}
		if (maxEdgesToCheck != edgesChecked) {
			alreadyCheckedEdges.add(next);
			return hasCycle(edgeMST, start, findNextEdge(edgeMST, next, alreadyCheckedEdges), edgesChecked + 1, maxEdgesToCheck, alreadyCheckedEdges);
		}
		return false;
	}

	private Edge findNextEdge(ArrayList<Edge> edgeMST, Edge next, ArrayList<Edge> alreadyCheckedEdges) {
		for (Edge edge : edgeMST) {
			if (!alreadyCheckedEdges.contains(edge)) {
				if (edge.getVertice1().getName().equals(next.getVertice1().getName())
						|| edge.getVertice2().getName().equals(next.getVertice1().getName())
						|| edge.getVertice1().getName().equals(next.getVertice2().getName())
						|| edge.getVertice2().getName().equals(next.getVertice2().getName())) {
					return edge;
				}
			}
		}
		return null;
	}

	private void removeEdges(ArrayList<Edge> edgesByWeight) {
		edges.removeAll(edgesByWeight);
	}

	private ArrayList<Edge> getAllEdgesByWeight(int lowestEdge) {
		ArrayList<Edge> edgesByWeight = new ArrayList<>();
		for (Edge edge : edges) {
			if (edge.getWeight() == lowestEdge) edgesByWeight.add(edge);
		}
		return edgesByWeight;
	}

	private int findLowestEdge() {
		int lowestEdge = highestEdgeWeight;
		for (Edge edge : edges) {
			if (edge.getWeight() < lowestEdge) lowestEdge = edge.getWeight();
		}
		return lowestEdge;
	}

	private void fillEdgesAndVertices(Vertice vertice1, int maxDeep, int deep) {

		if (deep == maxDeep) return;
		for (Map.Entry<Location, Integer> neighbouringEntry : vertice1.getLocationReference().getNeighbouringLocations().entrySet()) {
			Location location = neighbouringEntry.getKey();
			int weight = neighbouringEntry.getValue();

			Vertice vertice2 = new Vertice(location, null, 0);

			Edge edge = new Edge(vertice1, vertice2, weight);
			if (edgeDoesNotExistYet(edge)) {
				if (weight > highestEdgeWeight) highestEdgeWeight = weight;
				vertices.add(vertice2);
				edges.add(edge);
			}
			fillEdgesAndVertices(vertice2, maxDeep, deep + 1);

		}


	}

	private boolean edgeDoesNotExistYet(Edge edge) {
		for (Edge egeAlreadPut : edges) {
			if (egeAlreadPut.getVertice1().getName().equals(edge.getVertice1().getName())
					&& egeAlreadPut.getVertice2().getName().equals(edge.getVertice2().getName())) {
				return false;
			} else if (egeAlreadPut.getVertice1().getName().equals(edge.getVertice2().getName())
					&& egeAlreadPut.getVertice2().getName().equals(edge.getVertice1().getName())) {
				return false;
			}
		}
		// TODO take weight into acc
		return true;
	}


}
