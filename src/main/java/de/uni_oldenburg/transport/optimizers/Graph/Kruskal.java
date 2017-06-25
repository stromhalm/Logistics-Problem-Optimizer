package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Kruskal {

	private int highestEdgeWeight;

	private ArrayList<Vertice> vertices;
	private ArrayList<Edge> edges;

	private ArrayList<Edge> edgesMST;

	private ArrayList<ArrayList<Edge>> subGraphEdges;

	public Kruskal(TransportNetwork transportNetwork) {
		Location[] locations = transportNetwork.getLocations();
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		highestEdgeWeight = 0;
		edgesMST = new ArrayList<>();
		subGraphEdges = new ArrayList<>();

		Vertice vertice = new Vertice(transportNetwork.getStartLocation(), null, 0);
		vertices.add(vertice);
		fillEdgesAndVertices(vertice, locations.length - 1, 0, locations.length);


	}

	public Location[] getLocationsMST() {
		ArrayList<Location> locations = new ArrayList<>();
		//System.out.println();
		for (Edge edge : edgesMST) {
			//System.out.println(edge.getVertice1().getName() + " with " + edge.getWeight() + " to " + edge.getVertice2().getName());
		}
		// TODO implement

		return (Location[]) locations.toArray();
	}

	public void findMST() {

		while (edges.size() != 0) {
			int lowestEdge = findLowestEdge();
			ArrayList<Edge> edgesByWeight = getAllEdgesByWeight(lowestEdge);
			for (Edge edgeByWeight : edgesByWeight) {
				if (!addingProducesCycle(edgeByWeight)) {
					edgesMST.add(edgeByWeight);
					System.out.println(edgeByWeight.getVertice1().getName() + " with " + edgeByWeight.getWeight() + " to " + edgeByWeight.getVertice2().getName());
				}
			}
			removeEdges(edgesByWeight);
		}
	}

	private boolean addingProducesCycle(Edge edgeByWeight) {

		isConnected(edgeByWeight);

		for (ArrayList<Edge> subGraph : subGraphEdges) {
			ArrayList<Vertice> vertices = new ArrayList<>();

			for (Edge edge : subGraph) {
				Vertice vertice = edge.getVertice1();
				if (!verticeAlreadyGot(vertices, vertice)) vertices.add(vertice);
				vertice = edge.getVertice2();
				if (!verticeAlreadyGot(vertices, vertice)) vertices.add(vertice);
			}
			if (subGraph.size() + 1 > vertices.size()) {
				return subGraph.remove(edgeByWeight); // remove it again
			}
		}
		return false;
	}

	private boolean isConnected(Edge edgeByWeight) {

		ArrayList<ArrayList<Edge>> subGraphsMathed = new ArrayList<>();

		for (ArrayList<Edge> subGraph : subGraphEdges) {
			for (Edge edge : subGraph) {
				if (edge.getVertice1().getName().equals(edgeByWeight.getVertice1().getName())
						|| edge.getVertice2().getName().equals(edgeByWeight.getVertice1().getName())
						|| edge.getVertice1().getName().equals(edgeByWeight.getVertice2().getName())
						|| edge.getVertice2().getName().equals(edgeByWeight.getVertice2().getName())) {
					subGraphsMathed.add(subGraph);
					break;
				}
			}
		}

		if (subGraphsMathed.size() == 2) { // compare
			subGraphsMathed.get(0).add(edgeByWeight);
			subGraphsMathed.get(0).addAll(subGraphsMathed.get(1));
			subGraphEdges.remove(subGraphsMathed.get(1));
			return true;
		} else if (subGraphsMathed.size() == 1) {
			return subGraphsMathed.get(0).add(edgeByWeight);
		} else {
			// else not connected
			ArrayList<Edge> subGraph = new ArrayList<>();
			subGraph.add(edgeByWeight);
			return subGraphEdges.add(subGraph);
		}
	}

	private boolean verticeAlreadyGot(ArrayList<Vertice> vertices, Vertice vertice) {
		for (Vertice verticeAlreadyGot : vertices) {
			if (verticeAlreadyGot.getName().equals(vertice.getName())) return true;
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

	private void fillEdgesAndVertices(Vertice vertice1, int maxDeep, int deep, int locationsCount) {

		if (deep == maxDeep || vertices.size() == locationsCount) return;
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
			fillEdgesAndVertices(vertice2, maxDeep, deep + 1, locationsCount);

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
