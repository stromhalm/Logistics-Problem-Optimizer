package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides static method and functions to get spanning networks (aka spanning graphs) and more.
 */
public class Graph {

	public static LinkedHashMap<Location, Integer>[][] computeAdjazenMatrix(Location[] locations) {
		LinkedHashMap<Location, Integer>[][] adjazenMatrix = new LinkedHashMap[locations.length][locations.length];
		for (int i = 0; i < locations.length; i++) {
			for (int j = 0; j < locations.length; j++) {
				Location from = locations[i];
				Location to = locations[j];
				adjazenMatrix[i][j] = doDijkstra(from, to);
			}
		}
		return adjazenMatrix;
	}

	/**
	 * Compute Dijkstra recursively;
	 *
	 * @param from Location at which to start.
	 * @param to   Destination Location.
	 * @return The Minimal-Spanning-Tree (MST) for each combination of from and to locations.
	 */
	private static LinkedHashMap<Location, Integer> doDijkstra(Location from, Location to) {
		LinkedHashMap<Location, Integer> path = new LinkedHashMap<>();

		if (from.getName().equals(to.getName())) {
			path.put(from, 0);
		} else {
			ArrayList<Vertex> markedVertex = new ArrayList<>();
			ArrayList<Vertex> vertices = new ArrayList<>();
			// init
			Vertex start = new Vertex(from, 0, null);
			vertices.add(start);
			for (Map.Entry<Location, Integer> neighbour : start.getLocationReference().getNeighbouringLocations().entrySet()) {
				Vertex vertex = new Vertex(neighbour.getKey(), neighbour.getValue(), start);
				markedVertex.add(vertex);
				vertices.add(vertex);
			}
			// start iteration
			while (markedVertex.size() != 0) {
				//System.out.print("Marked vertices: ");
				Vertex leastVertex = null;
				for (Vertex vertex : markedVertex) {
					//	System.out.print(vertex.getName() + ", ");
					if (leastVertex == null) {
						leastVertex = vertex;
					} else if (vertex.getExpenseToStart() < leastVertex.getExpenseToStart()) {
						leastVertex = vertex;
					}
				}
				//System.out.println();

				for (Map.Entry<Location, Integer> neighbour : leastVertex.getLocationReference().getNeighbouringLocations().entrySet()) {
					boolean isNew = true;
					for (Vertex vertex : vertices) {
						if (vertex.getName().equals(neighbour.getKey().getName())) {
							// update the vertex if less
							isNew = false;
							if (vertex.getExpenseToStart() > leastVertex.getExpenseToStart() + neighbour.getValue()) {
								vertex.setExpenseToStart(leastVertex.getExpenseToStart() + neighbour.getValue());
								vertex.setPredecessor(leastVertex);
								break;
							}
						}
					}
					if (isNew) {
						Vertex vertex = new Vertex(neighbour.getKey(), neighbour.getValue() + leastVertex.getExpenseToStart(), leastVertex);
						markedVertex.add(vertex);
						vertices.add(vertex);
					}
				}
				markedVertex.remove(leastVertex);
			}

			for (Vertex vertex : vertices) {
				if (vertex.getName().equals(to.getName())) {
					while (vertex.getPredecessor() != null) {
						path.put(vertex.getLocationReference(), vertex.getLocationReference().getNeighbouringLocations().get(vertex.getPredecessor().getLocationReference()));
						vertex = vertex.getPredecessor();
					}
					path.put(vertex.getLocationReference(), 0);
					break;
				}
			}
		}

		return path;
	}


	/**
	 * Get the minimized spanning network. Have not to be the minimum spanning tree as it allows loops if it improves the solution.
	 *
	 * @param locations     Locations array from which the spanning network is to be got.
	 * @param startLocation Location to start at.
	 * @return minimized spanning network.
	 */
	public static ArrayList<ArrayList<Vertex>> getSpanningNetwork(Location[] locations, Location startLocation) {
		ArrayList<ArrayList<Vertex>> spanningTrees = new ArrayList<>();

		/*
		 * Get all possible routes.
		 */
		for (int depth = 0; depth < locations.length; depth++) {
			Vertex root = new Vertex(startLocation, null, 0);

			ArrayList<ArrayList<Vertex>> spanningTreesTmp = new ArrayList<>();
			ArrayList<Vertex> spanningTreeTmp = new ArrayList<>();
			findNewSpanningTree(spanningTreesTmp, spanningTreeTmp, new ArrayList<Vertex>(), root, startLocation.getName(), depth);
			for (ArrayList<Vertex> spanningTree : spanningTreesTmp) {
				if (spanningTreeIsCheaperOrNew(spanningTree, spanningTrees, startLocation.getName())) {
					spanningTrees.add(spanningTree);
				}
			}
		}

		/*
		 * Minimize the found routes by finding overlapping routes, etc.
		 */
		ArrayList<ArrayList<Vertex>> spanningTreesOut = new ArrayList<>(spanningTrees);
		for (int i = 0; i < spanningTrees.size(); i++) {
			ArrayList<Vertex> spanningTree = spanningTrees.get(i);
			for (int y = 0; y < spanningTrees.size(); y++) {
				if (y != i) {
					minimize(spanningTree, spanningTrees.get(y), spanningTreesOut);
				}
			}
		}

		if (allVerticesGot(locations, spanningTrees)) {
			return spanningTreesOut;
		}
		return null;
	}

	/**
	 * Minimizes a given set of routes.
	 *
	 * @param spanningTree     The spanning tree which is to be minimized by a new set of vertices.
	 * @param vertices         The vertices that are to be checked checked.
	 * @param spanningTreesOut The output new spanning tree.
	 */
	private static void minimize(ArrayList<Vertex> spanningTree, ArrayList<Vertex> vertices, ArrayList<ArrayList<Vertex>> spanningTreesOut) {
		boolean replacable = true;

		int min = Math.min(spanningTree.size(), vertices.size());
		for (int i = 0; i < min; i++) {
			if (!spanningTree.get(i).getName().equals(vertices.get(i).getName())) {
				replacable = false;
			}
		}

		if (replacable && min == spanningTree.size()) {
			spanningTreesOut.remove(spanningTree);
		} else if (replacable) {
			spanningTreesOut.remove(vertices);
		}
	}

	/**
	 * Finds new spanning trees recursively.
	 *
	 * @param spanningTreesTmp
	 * @param spanningTree
	 * @param alreadyVisitedList
	 * @param startLocation
	 * @param startLocationName
	 * @param depth
	 */
	private static void findNewSpanningTree(ArrayList<ArrayList<Vertex>> spanningTreesTmp, ArrayList<Vertex> spanningTree, ArrayList<Vertex> alreadyVisitedList, Vertex startLocation, String startLocationName, int depth) {

		spanningTree.add(startLocation);
		alreadyVisitedList.add(startLocation);
		if (depth != 0) {
			for (Map.Entry<Location, Integer> neighbouringLocation : startLocation.getLocationReference().getNeighbouringLocations().entrySet()) {
				Vertex location = new Vertex(neighbouringLocation.getKey(), startLocation, neighbouringLocation.getValue());
				if (!alreadyVisited(location, alreadyVisitedList)) {

					if (location.getName().equals(startLocationName)) {
						return;
					}

					location.setExpenseToParentLocation(neighbouringLocation.getValue());

					findNewSpanningTree(spanningTreesTmp, new ArrayList<Vertex>(spanningTree) /*Create a copy*/, new ArrayList<>(alreadyVisitedList), location, startLocationName, depth - 1);
				} else {
					// cycle
					//spanningTreesTmp.add(spanningTree); // TODO implement cycle detection?!
				}
			}
		} else {
			// end is reached
			spanningTreesTmp.add(spanningTree);

		}
	}

	/**
	 * Checks whether a new tree is new or cheaper than one already found.
	 *
	 * @param spanningTree      The spanning tree which is possibly to be added.
	 * @param spanningTrees     The currently found spanning trees.
	 * @param startLocationName The start locations name to ident
	 * @return A boolean value whether the new spanning tree must be added to the currently set of spanning trees.
	 */
	private static boolean spanningTreeIsCheaperOrNew(ArrayList<Vertex> spanningTree, ArrayList<ArrayList<Vertex>> spanningTrees, String startLocationName) {

		boolean matchFound = false;
		for (ArrayList<Vertex> spanningTreeAlreadyGot : spanningTrees) {
			int oldWeigth = 0;
			int newWeigth = 0;

			if (spanningTree.get(spanningTree.size() - 1).getName().equals(spanningTreeAlreadyGot.get(spanningTreeAlreadyGot.size() - 1).getName())) {
				matchFound = true;
				// because all Trees start from the original start location the end specifies the Tree
				int comparedMinimum = (Math.min(spanningTree.size(), spanningTreeAlreadyGot.size()) - 1);
				for (int i = comparedMinimum; i > 0; i--) { // compare which one is cheaper
					newWeigth += spanningTree.get(spanningTree.size() - i).getExpenseToParentLocation();
					oldWeigth += spanningTreeAlreadyGot.get(spanningTreeAlreadyGot.size() - i).getExpenseToParentLocation();
				}
				if (spanningTree.get(spanningTree.size() - comparedMinimum).getName().equals(startLocationName)
						&& spanningTreeAlreadyGot.get(spanningTreeAlreadyGot.size() - comparedMinimum).getName().equals(startLocationName)) { // only compare paths that are of same length // TODO find a better solution to switch paths and replace parts of them
					if (oldWeigth > newWeigth) {
						spanningTrees.remove(spanningTreeAlreadyGot); // TODO do not delete but adjust the path
						return true;
					}
				}

			}

		}
		return !matchFound;
	}


	/**
	 * Checks whether all vertices are delivered or reached by the routes found.
	 *
	 * @param locations     Location array to be checked.
	 * @param spanningTrees The spanning trees which reorganized the locations array with graph elements.
	 * @return A boolean value whether all locations have a vertex equivalent.
	 */
	private static boolean allVerticesGot(Location[] locations, ArrayList<ArrayList<Vertex>> spanningTrees) {
		boolean[] locationsGot = new boolean[locations.length];
		for (ArrayList<Vertex> Trees : spanningTrees) {
			for (Vertex location : Trees) {
				for (int i = 0; i < locations.length; i++) {
					if (locations[i].getName().equals(location.getName())) {
						locationsGot[i] = true;
						break;
					}
				}
			}
			boolean allLocationsGot = true;
			for (boolean aLocationsGot : locationsGot) {
				if (!aLocationsGot) {
					allLocationsGot = false;
					break;
				}
			}

			if (allLocationsGot) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a location has already been visited and checked by a route of vertices.
	 *
	 * @param location           The location to be checked as a {@link Vertex}.
	 * @param alreadyVisitedList The already vistied vertices.
	 * @return boolean value whether the location is already added.
	 */
	private static boolean alreadyVisited(Vertex location, ArrayList<Vertex> alreadyVisitedList) {
		for (Vertex vertex : alreadyVisitedList) {
			if (location.getName().equals(vertex.getName())) {
				return true;
			}
		}
		return false;
	}
}
