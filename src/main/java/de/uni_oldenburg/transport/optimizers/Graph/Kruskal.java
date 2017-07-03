package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple implementation of the Kruskal algorithm to get a minimal spanning tree (MST) of an undirected weighted graph.
 */
public class Kruskal {

	private int highestEdgeWeight;

	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;

	private ArrayList<Edge> notEdgesMST;

	private ArrayList<ArrayList<Edge>> subGraphEdges;

	public Kruskal(TransportNetwork transportNetwork) {
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		highestEdgeWeight = 0;
		notEdgesMST = new ArrayList<>();
		subGraphEdges = new ArrayList<>();

		Vertex vertex = new Vertex(transportNetwork.getStartLocation(), null, 0);
		vertices.add(vertex);
		fillEdgesAndVertices(vertex, transportNetwork.getLocations().length - 1, 0, transportNetwork.getLocations().length);
	}

	public TransportNetwork getLocationsMST() {
		if (subGraphEdges.size() != 1) return null;
		for (Edge edge : notEdgesMST) {
			//System.out.println("Removing Edge: " + edge.getVertex1().getName() + " to " + edge.getVertex2().getName() + " with " + edge.getWeight());
			edge.getVertex1().getLocationReference().getNeighbouringLocations().remove(edge.getVertex2().getLocationReference(), edge.getWeight());
			edge.getVertex2().getLocationReference().getNeighbouringLocations().remove(edge.getVertex1().getLocationReference(), edge.getWeight());
		}

		Location[] locations = new Location[vertices.size()];

		for (int i = 0; i < vertices.size(); i++) {
			locations[i] = vertices.get(i).getLocationReference();
		}
		return new TransportNetwork(locations);
	}

	public void findMST() {

		while (edges.size() != 0) {
			int lowestEdge = findLowestEdge();
			ArrayList<Edge> edgesByWeight = getAllEdgesByWeight(lowestEdge);
			for (Edge edgeByWeight : edgesByWeight) {
				if (addingProducesCycle(edgeByWeight)) {
					notEdgesMST.add(edgeByWeight);
				}
			}
			removeEdges(edgesByWeight);
		}
	}

	private boolean addingProducesCycle(Edge edgeByWeight) {

		isConnected(edgeByWeight);

		for (ArrayList<Edge> subGraph : subGraphEdges) {
			ArrayList<Vertex> vertices = new ArrayList<>();
			for (Edge edge : subGraph) {
				Vertex vertex = edge.getVertex1();
				if (!vertexAlreadyGot(vertices, vertex)) vertices.add(vertex);
				vertex = edge.getVertex2();
				if (!vertexAlreadyGot(vertices, vertex)) vertices.add(vertex);
			}
			if (subGraph.size() + 1 > vertices.size()) {
				subGraph.remove(edgeByWeight); // remove it again
				return true;
			}
		}
		return false;
	}

	private void isConnected(Edge edgeByWeight) {

		ArrayList<ArrayList<Edge>> subGraphsMathed = new ArrayList<>();

		for (ArrayList<Edge> subGraph : subGraphEdges) {
			for (Edge edge : subGraph) {
				if (edge.getVertex1().getName().equals(edgeByWeight.getVertex1().getName())
						|| edge.getVertex2().getName().equals(edgeByWeight.getVertex1().getName())
						|| edge.getVertex1().getName().equals(edgeByWeight.getVertex2().getName())
						|| edge.getVertex2().getName().equals(edgeByWeight.getVertex2().getName())) {
					subGraphsMathed.add(subGraph);
					break;
				}
			}
		}

		if (subGraphsMathed.size() == 2) { // compare
			subGraphsMathed.get(0).add(edgeByWeight);
			subGraphsMathed.get(0).addAll(subGraphsMathed.get(1));
			subGraphEdges.remove(subGraphsMathed.get(1));
		} else if (subGraphsMathed.size() == 1) {
			subGraphsMathed.get(0).add(edgeByWeight);
		} else {
			// else not connected
			ArrayList<Edge> subGraph = new ArrayList<>();
			subGraph.add(edgeByWeight);
			subGraphEdges.add(subGraph);
		}
	}

	private boolean vertexAlreadyGot(ArrayList<Vertex> vertices, Vertex vertex) {
		for (Vertex vertexAlreadyGot : vertices) {
			if (vertexAlreadyGot.getName().equals(vertex.getName())) return true;
		}
		return false;
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

	private void fillEdgesAndVertices(Vertex vertex1, int maxDeep, int deep, int locationsCount) {

		if (deep == maxDeep || vertices.size() == locationsCount) return;
		for (Map.Entry<Location, Integer> neighbouringEntry : vertex1.getLocationReference().getNeighbouringLocations().entrySet()) {
			Location location = neighbouringEntry.getKey();
			int weight = neighbouringEntry.getValue();

			Vertex vertex2 = new Vertex(location, null, 0);

			Edge edge = new Edge(vertex1, vertex2, weight);
			if (edgeDoesNotExistYet(edge)) {
				if (weight > highestEdgeWeight) highestEdgeWeight = weight;
				boolean alreadyGot = false;
				for (Vertex vertex : vertices) {
					if (vertex.getName().equals(vertex2.getName())) alreadyGot = true;
				}
				if (!alreadyGot) vertices.add(vertex2);
				edges.add(edge);
			}
			fillEdgesAndVertices(vertex2, maxDeep, deep + 1, locationsCount);

		}


	}

	private boolean edgeDoesNotExistYet(Edge edge) {
		for (Edge egeAlreadPut : edges) {
			if (egeAlreadPut.getVertex1().getName().equals(edge.getVertex1().getName())
					&& egeAlreadPut.getVertex2().getName().equals(edge.getVertex2().getName())) {
				return false;
			} else if (egeAlreadPut.getVertex1().getName().equals(edge.getVertex2().getName())
					&& egeAlreadPut.getVertex2().getName().equals(edge.getVertex1().getName())) {
				return false;
			}
		}
		return true;
	}


}
