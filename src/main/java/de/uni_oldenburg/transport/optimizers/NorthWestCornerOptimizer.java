package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.TransportNetwork;
import de.uni_oldenburg.transport.optimizers.Graph.Vertice;

import java.util.ArrayList;
import java.util.Map;

/**
 * Implements the North-Wester-Corner method to solve the transport problem and optimize it. See VL 6 for further information.
 */
public class NorthWestCornerOptimizer implements Optimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		//ArrayList<ArrayList<Location>> networkCycles = getNetworkCycles(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		ArrayList<ArrayList<Vertice>> minimalSpanningNetwork = getMinimalSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		for (ArrayList<Vertice> spanningNode : minimalSpanningNetwork) {
			for (int i = 0; i < spanningNode.size(); i++) {
				System.out.print(spanningNode.get(i).getLocationReference().getName() + ", ");
			}
			System.out.print("\n");
		}
		return null;
	}

	private ArrayList<ArrayList<Vertice>> getMinimalSpanningNetwork(Location[] locations, Location startLocation) {
		ArrayList<ArrayList<Vertice>> spanningNodes = new ArrayList<>();


		for (int deepth = 0; deepth < locations.length; deepth++) {
			Vertice root = new Vertice(startLocation, null, 0);

			ArrayList<ArrayList<Vertice>> spanningNodesTmp = new ArrayList<>();
			ArrayList<Vertice> spanningNodeTmp = new ArrayList<>();
			findNewSpanningNode(spanningNodesTmp, spanningNodeTmp, new ArrayList<Vertice>(), root, "Hamburg", deepth);
			for (ArrayList<Vertice> spanningNode : spanningNodesTmp) {
				if (spanningNodeIsCheaperOrNew(spanningNode, spanningNodes)) {
					spanningNodes.add(spanningNode);
				}
			}
		}

		System.out.println(allVerticesGot(locations, spanningNodes));

		return spanningNodes;
	}

	private void findNewSpanningNode(ArrayList<ArrayList<Vertice>> spanningNodesTmp, ArrayList<Vertice> spanningNode, ArrayList<Vertice> alreadyVisitedList, Vertice startLocation, String hamburg, int deepth) {

		spanningNode.add(startLocation);
		alreadyVisitedList.add(startLocation);
		if (deepth != 0) {
			for (Map.Entry<Location, Integer> neighbouringLocation : startLocation.getLocationReference().getNeighbouringLocations().entrySet()) {
				Vertice location = new Vertice(neighbouringLocation.getKey(), startLocation, neighbouringLocation.getValue());
				if (!alreadyVisited(location, alreadyVisitedList)) {

					if (location.getLocationReference().getName().equals(hamburg)) {
						return;
					}
					startLocation.addChild(location);
					int weight = neighbouringLocation.getValue();
					location.setExpenseToParentLocation(weight);

					findNewSpanningNode(spanningNodesTmp, new ArrayList<Vertice>(spanningNode) /*Create a copy*/, new ArrayList<>(alreadyVisitedList), location, hamburg, deepth - 1);
				} else {
					// cycle
					spanningNodesTmp.add(spanningNode); // TODO remove
				}
			}
		} else {
			// end is reached
			spanningNodesTmp.add(spanningNode);

		}
	}

	private boolean spanningNodeIsCheaperOrNew(ArrayList<Vertice> spanningNode, ArrayList<ArrayList<Vertice>> spanningNodes) {

		boolean matchFound = false;
		for (ArrayList<Vertice> spanningNodeAlreadyGot : spanningNodes) {
			int oldWeigth = 0;
			int newWeigth = 0;

			if (spanningNode.get(spanningNode.size() - 1).getLocationReference().getName().equals("Dresden") && spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - 1).getLocationReference().getName().equals("Dresden")) {
				System.currentTimeMillis();
			}
			if (spanningNode.get(spanningNode.size() - 1).getLocationReference().getName().equals(spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - 1).getLocationReference().getName())) {
				matchFound = true;
				// because all nodes start from the original start location the end specifies the node
				int comparedMinimum = (Math.min(spanningNode.size(), spanningNodeAlreadyGot.size()) - 1);
				for (int i = comparedMinimum; i > 0; i--) { // compare which one is cheaper
					newWeigth += spanningNode.get(spanningNode.size() - i).getExpenseToParentLocation();
					oldWeigth += spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - i).getExpenseToParentLocation();
				}
				if (spanningNode.get(spanningNode.size() - comparedMinimum).getLocationReference().getName().equals("Hamburg")
						&& spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - comparedMinimum).getLocationReference().getName().equals("Hamburg")) { // only compare paths that are of same length // TODO find a better solution to switch paths and replace parts of them
					if (oldWeigth > newWeigth) {
						spanningNodes.remove(spanningNodeAlreadyGot); // TODO do not delete but adjust the path
						return true;
					}
				}
			}
		}
		return !matchFound;
	}

	private ArrayList<ArrayList<Location>> getNetworkCycles(Location[] locations, Location startLocation) {
		ArrayList<ArrayList<Location>> cycles = new ArrayList<>();

		int counter = 0;
		do {
			ArrayList<Location> cycle = new ArrayList<>();
			findNewCycle(cycle, new ArrayList<>(), startLocation, "Hamburg", counter);
			if (!cycleAlreadyExists(cycle, cycles)) {
				for (Location location : cycle) {
					System.out.print(location.getName() + ", ");
				}
				System.out.print("\n");
				cycles.add(cycle);
			} else {
				counter++;
			}
		} while (!allLocationsGot(locations, cycles) && counter < locations.length);
		return cycles;
	}

	private boolean findNewCycle(ArrayList<Location> locations, ArrayList<Location> alreadyVisitedList, Location startLocation, String originalLocationName, int counter) {
		alreadyVisitedList.add(startLocation);
		locations.add(startLocation);

		if (startLocation.getNeighbouringLocations().size() == 1) {
			alreadyVisitedList.remove(locations.get(locations.size() - 2)); // remove the original start that led into the one way
		}

		int i = 0;
		for (Map.Entry<Location, Integer> neighbouringLocation : startLocation.getNeighbouringLocations().entrySet()) {
			Location location = neighbouringLocation.getKey();
			if (startLocation.getName().equals("Hamburg") && location.getName().equals("Kiel")) {
				System.currentTimeMillis();
			}
			boolean alreadyVisited = alreadyVisited(location, alreadyVisitedList);

			if (!alreadyVisited && i >= counter) {
				if (findNewCycle(locations, alreadyVisitedList, location, originalLocationName, counter)) {
					return true;
				}
			} else if (location.getName().equals(originalLocationName) && locations.size() != 2) {
				return true;
			} else {
				i++;
			}
		}
		return false;
	}

	private boolean cycleAlreadyExists(ArrayList<Location> cycle, ArrayList<ArrayList<Location>> cycles) {
		boolean wasAlwaysFalse = false;
		for (ArrayList<Location> alreadyGotCycle : cycles) {
			boolean isEqual = true;
			for (int i = 0; i < Math.min(cycle.size(), alreadyGotCycle.size()); i++) {
				if (!cycle.get(i).getName().equals(alreadyGotCycle.get(i).getName())) isEqual = false;
			}
			if (isEqual) wasAlwaysFalse = true;
		}
		// TODO check inverse
		for (ArrayList<Location> alreadyGotCycle : cycles) {
			boolean isEqual = true;
			for (int i = 0; i < Math.min(cycle.size(), alreadyGotCycle.size()); i++) {
				if (!cycle.get(i).getName().equals(alreadyGotCycle.get(Math.min(cycle.size(), alreadyGotCycle.size()) - i - 1).getName()))
					isEqual = false;
			}
			if (isEqual) wasAlwaysFalse = true;
		}
		return wasAlwaysFalse;
	}

	private boolean allLocationsGot(Location[] locations, ArrayList<ArrayList<Location>> cycles) {
		boolean[] locationsGot = new boolean[locations.length];
		for (ArrayList<Location> cycle : cycles) {
			for (Location location : cycle) {
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
				return allLocationsGot;
			}
		}
		return false;
	}

	private boolean allVerticesGot(Location[] locations, ArrayList<ArrayList<Vertice>> spanningNodes) {
		boolean[] locationsGot = new boolean[locations.length];
		for (ArrayList<Vertice> nodes : spanningNodes) {
			for (Vertice location : nodes) {
				for (int i = 0; i < locations.length; i++) {
					if (locations[i].getName().equals(location.getLocationReference().getName())) {
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
				return allLocationsGot;
			}
		}
		return false;
	}

	private boolean alreadyVisited(Vertice location, ArrayList<Vertice> alreadyVisitedList) {
		for (Vertice vertice : alreadyVisitedList) {
			if (location.getLocationReference().getName().equals(vertice.getLocationReference().getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean alreadyVisited(Location key, ArrayList<Location> alreadyVisitiedList) {
		for (Location location : alreadyVisitiedList) {
			if (key.getName().equals(location.getName())) {
				return true;
			}
		}
		return false;
	}

}
