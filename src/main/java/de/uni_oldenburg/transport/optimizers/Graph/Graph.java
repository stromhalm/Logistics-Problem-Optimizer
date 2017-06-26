package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides static method and functions to get spanning networks (aka spanning graphs).
 */
public class Graph {

	public static HashMap<Location, Integer>[][] computeAdjazenMatrix(Location[] locations) {
		HashMap<Location, Integer>[][] adjazenMatrix = new HashMap[locations.length][locations.length];
		for (int i = 0; i < locations.length; i++) {
			for (int j = 0; j < locations.length; j++) {
				Location from = locations[i];
				Location to = locations[j];
				HashMap<ArrayList<Location>, Integer> entry = new HashMap<ArrayList<Location>, Integer>();
				adjazenMatrix[i][j] = doDijkstra(from, to, new ArrayList(), new ArrayList());
				adjazenMatrix[i][j].put(from, 0); // add start location of the path
				for (Map.Entry<Location, Integer> mapEntry : adjazenMatrix[i][j].entrySet()) {
					System.out.print(mapEntry.getValue() + " ");
				}
			}
			System.out.println();
		}
		return adjazenMatrix;
	}

	/**
	 * Compute Dijkstra recursively;
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	private static HashMap<Location, Integer> doDijkstra(Location from, Location to, ArrayList alreadyVisited, ArrayList settled) {
		if (alreadyVisited(alreadyVisited, from)) return null;
		for (Map.Entry<Location, Integer> neighbouringLocation : from.getNeighbouringLocations().entrySet()) {
			if (!neighbouringLocation.getKey().getName().equals(from.getName())) {
				Location neighbour = neighbouringLocation.getKey();
				int expense = neighbouringLocation.getValue();
				if (neighbour.getName().equals(to.getName())) {
					HashMap<Location, Integer> path = new HashMap<>();
					path.put(to, expense);
					return path;
				} else {
					alreadyVisited.add(neighbour);
					HashMap<Location, Integer> path;
					if ((path = doDijkstra(neighbour, to, new ArrayList<>(alreadyVisited), settled)) != null) {
						path.put(from, expense);
						return path;
					}
				}
			}
		}
		HashMap<Location, Integer> defaultMap = new HashMap<>();
		defaultMap.put(from, 0);
		return defaultMap;
	}


	/**
	 * Get the minimized spanning network. Have not to be the minimum spanning tree as it allows loops if it improves the solution.
	 *
	 * @param locations
	 * @param startLocation
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
	 * @param spanningTree
	 * @param vertices
	 * @param spanningTreesOut
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
					//spanningTreesTmp.add(spanningTree); // TODO imlement cycle detection?!
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
	 * @param spanningTree
	 * @param spanningTrees
	 * @param startLocationName
	 * @return
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
	 * @param locations
	 * @param spanningTrees
	 * @return
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
	 * @param location
	 * @param alreadyVisitedList
	 * @return
	 */
	private static boolean alreadyVisited(Vertex location, ArrayList<Vertex> alreadyVisitedList) {
		for (Vertex vertex : alreadyVisitedList) {
			if (location.getName().equals(vertex.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a location has already been visited.
	 *
	 * @param location
	 * @param alreadyVisitedList
	 * @return
	 */
	private static boolean alreadyVisited(ArrayList<Location> alreadyVisitedList, Location location) {
		for (Location visitedLocation : alreadyVisitedList) {
			if (location.getName().equals(visitedLocation.getName())) {
				return true;
			}
		}
		return false;
	}

}
