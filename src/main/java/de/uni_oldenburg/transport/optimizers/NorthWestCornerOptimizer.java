package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Kruskal;
import de.uni_oldenburg.transport.optimizers.Graph.Vertice;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implements the North-Wester-Corner method to solve the transport problem and optimize it. See VL 6 for further information.
 */
public class NorthWestCornerOptimizer implements Optimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		/*ArrayList<ArrayList<Vertice>> minimalSpanningNetwork = getMinimalSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());
		for (ArrayList<Vertice> spanningNode : minimalSpanningNetwork) {
			for (int i = 0; i < spanningNode.size(); i++) {
				System.out.print(spanningNode.get(i).getName() + ", ");
			}
			// is a quite well optimized shortest path
			System.out.print("\n");
		}*/

		Kruskal kruskal = new Kruskal(transportNetwork);
		kruskal.findMST();
		transportNetwork = kruskal.getLocationsMST();

		ArrayList<ArrayList<Vertice>> minimalSpanningNetwork = getMinimalSpanningNetwork(transportNetwork.getLocations(), transportNetwork.getStartLocation());

		for (ArrayList<Vertice> tours : minimalSpanningNetwork) {
			System.out.print("Possible tour: ");
			for (Vertice vertice : tours) {
				System.out.print(vertice.getName() + " to ");
			}
			System.out.print("\n");
		}

		Solution solution = new Solution(transportNetwork);

		for (Tour tour : doNorthWestCornerMethod(minimalSpanningNetwork, transportNetwork.getStartLocation())) {
			solution.addTour(tour);
		}

		return solution;
	}

	public ArrayList<Tour> doNorthWestCornerMethod(ArrayList<ArrayList<Vertice>> minimalSpanningNetwork, Location startLocation) {
		int[] kmToDriveOnRoute = new int[minimalSpanningNetwork.size()];
		int[] amountToDeliverOnRoute = new int[minimalSpanningNetwork.size()];

		ArrayList<Location> locationsDelivered = new ArrayList<>();

		for (int route = 0; route < minimalSpanningNetwork.size(); route++) {
			Vertice leaf = minimalSpanningNetwork.get(route).get(minimalSpanningNetwork.get(route).size() - 1);

			while (leaf.getParentLocation() != null) {
				amountToDeliverOnRoute[route] += leaf.getLocationReference().getAmount();
				kmToDriveOnRoute[route] += leaf.getExpenseToParentLocation();
				leaf = leaf.getParentLocation();
			}
			//System.out.println("Route: " + route + ": " + amountToDeliverOnRoute[route] + " (amountNeeded) and " + kmToDriveOnRoute[route] + "(kmToDrive)");
		}

		ArrayList<Tour> tours = new ArrayList<>();

		for (int route = 0; route < minimalSpanningNetwork.size(); route++) {
			ArrayList<SmallTruck> smallTrucks = new ArrayList<>();
			ArrayList<MediumTruck> mediumTrucks = new ArrayList<>();
			ArrayList<LargeTruck> largeTrucks = new ArrayList<>();

			ArrayList<Vertice> vertices = minimalSpanningNetwork.get(route);
			boolean trucksConstellationPut = false;
			do { // TODO improve by detecting already delivered locations
				if (amountToDeliverOnRoute[route] <= SmallTruck.CAPACITY) {
					smallTrucks.add(new SmallTruck());
					trucksConstellationPut = true;
					amountToDeliverOnRoute[route] -= SmallTruck.CAPACITY;
				} else if (amountToDeliverOnRoute[route] > SmallTruck.CAPACITY && amountToDeliverOnRoute[route] <= MediumTruck.CAPACITY) {
					mediumTrucks.add(new MediumTruck());
					trucksConstellationPut = true;
					amountToDeliverOnRoute[route] -= MediumTruck.CAPACITY;
				} else if (amountToDeliverOnRoute[route] > MediumTruck.CAPACITY && amountToDeliverOnRoute[route] <= LargeTruck.CAPACITY) {
					largeTrucks.add(new LargeTruck());
					trucksConstellationPut = true;
					amountToDeliverOnRoute[route] -= LargeTruck.CAPACITY;
				} else {
					largeTrucks.add(new LargeTruck());
					amountToDeliverOnRoute[route] -= LargeTruck.CAPACITY;
				}
			} while (!trucksConstellationPut);

			AbstractTruck lastTruck = null;
			Tour lastTour = null;
			for (Vertice vertice : vertices) {
				int locationAmount = (locationDeliveredAlready(locationsDelivered, vertice) ? 0 : vertice.getLocationReference().getAmount());
				while (locationAmount != 0) {
					int min;
					if (lastTruck == null || lastTruck.getUnloaded() - lastTruck.getCapacity() == 0) {
						if (largeTrucks.size() != 0) {
							min = Math.min(LargeTruck.CAPACITY, locationAmount);
							lastTruck = largeTrucks.get(0);
							lastTour = new Tour(lastTruck, startLocation);
							lastTour.addDestination(new TourDestination(vertice.getLocationReference(), min), computeExpense(vertice));
							largeTrucks.remove(lastTruck);
						} else if (mediumTrucks.size() != 0) {
							min = Math.min(MediumTruck.CAPACITY, locationAmount);
							lastTruck = mediumTrucks.get(0);
							lastTour = new Tour(lastTruck, startLocation);
							lastTour.addDestination(new TourDestination(vertice.getLocationReference(), min), computeExpense(vertice));
							mediumTrucks.remove(lastTruck);
						} else {
							min = Math.min(SmallTruck.CAPACITY, locationAmount);
							lastTruck = smallTrucks.get(0);
							lastTour = new Tour(lastTruck, startLocation);
							lastTour.addDestination(new TourDestination(vertice.getLocationReference(), min), computeExpense(vertice));
							smallTrucks.remove(lastTruck);
						}
						tours.add(lastTour);
					} else {
						min = Math.min(lastTruck.getCapacity() - lastTruck.getUnloaded(), locationAmount);
						lastTour.addDestination(new TourDestination(vertice.getLocationReference(), min), computeExpense(vertice) - lastTour.getKilometersToDrive());
					}
					locationAmount -= min;
					lastTruck.unload(min);
				}
				locationsDelivered.add(vertice.getLocationReference());
			}
		}
		return tours;
	}

	private boolean locationDeliveredAlready(ArrayList<Location> locationsDelivered, Vertice vertice) {
		for (Location location : locationsDelivered) {
			if (vertice.getName().equals(location.getName())) return true;
		}
		return false;
	}

	private int computeExpense(Vertice vertice) {
		int expense = 0;
		while (vertice.getParentLocation() != null) {
			expense += vertice.getExpenseToParentLocation();
			vertice = vertice.getParentLocation();// TODO beware that might change the pointer?!
		}
		return expense;
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

		ArrayList<ArrayList<Vertice>> spanningNodesOut = new ArrayList<>(spanningNodes);
		for (int i = 0; i < spanningNodes.size(); i++) {
			ArrayList<Vertice> spanningNode = spanningNodes.get(i);
			for (int y = 0; y < spanningNodes.size(); y++) {
				if (y != i) {
					minimize(spanningNode, spanningNodes.get(y), spanningNodesOut);
				}
			}
		}

		if (allVerticesGot(locations, spanningNodes)) {
			return spanningNodesOut;
		} else {
			for (ArrayList<Vertice> spanningNode : spanningNodesOut) {
				for (int i = 0; i < spanningNode.size(); i++) {
					System.out.print(spanningNode.get(i).getName() + ", ");
				}
				// is a quite well optimized shortest path
				System.out.print("\n");
			}
			System.out.println("Failed to get shortest paths");
			return null;
		}
	}

	private void minimize(ArrayList<Vertice> spanningNode, ArrayList<Vertice> vertices, ArrayList<ArrayList<Vertice>> spanningNodesOut) {
		boolean replacable = true;

		int min = Math.min(spanningNode.size(), vertices.size());
		for (int i = 0; i < min; i++) {
			if (!spanningNode.get(i).getName().equals(vertices.get(i).getName())) {
				replacable = false;
			}
		}

		if (replacable && min == spanningNode.size()) {
			spanningNodesOut.remove(spanningNode);
		} else if (replacable) {
			spanningNodesOut.remove(vertices);
		}
	}

	private void findNewSpanningNode(ArrayList<ArrayList<Vertice>> spanningNodesTmp, ArrayList<Vertice> spanningNode, ArrayList<Vertice> alreadyVisitedList, Vertice startLocation, String hamburg, int deepth) {

		spanningNode.add(startLocation);
		alreadyVisitedList.add(startLocation);
		if (deepth != 0) {
			for (Map.Entry<Location, Integer> neighbouringLocation : startLocation.getLocationReference().getNeighbouringLocations().entrySet()) {
				Vertice location = new Vertice(neighbouringLocation.getKey(), startLocation, neighbouringLocation.getValue());
				if (!alreadyVisited(location, alreadyVisitedList)) {

					if (location.getName().equals(hamburg)) {
						return;
					}
					startLocation.addChild(location);
					int weight = neighbouringLocation.getValue();
					location.setExpenseToParentLocation(weight);

					findNewSpanningNode(spanningNodesTmp, new ArrayList<Vertice>(spanningNode) /*Create a copy*/, new ArrayList<>(alreadyVisitedList), location, hamburg, deepth - 1);
				} else {
					// cycle
					//spanningNodesTmp.add(spanningNode); // TODO imlement cycle detection?!
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

			if (spanningNode.get(spanningNode.size() - 1).getName().equals(spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - 1).getName())) {
				matchFound = true;
				// because all nodes start from the original start location the end specifies the node
				int comparedMinimum = (Math.min(spanningNode.size(), spanningNodeAlreadyGot.size()) - 1);
				for (int i = comparedMinimum; i > 0; i--) { // compare which one is cheaper
					newWeigth += spanningNode.get(spanningNode.size() - i).getExpenseToParentLocation();
					oldWeigth += spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - i).getExpenseToParentLocation();
				}
				if (spanningNode.get(spanningNode.size() - comparedMinimum).getName().equals("Hamburg")
						&& spanningNodeAlreadyGot.get(spanningNodeAlreadyGot.size() - comparedMinimum).getName().equals("Hamburg")) { // only compare paths that are of same length // TODO find a better solution to switch paths and replace parts of them
					if (oldWeigth > newWeigth) {
						spanningNodes.remove(spanningNodeAlreadyGot); // TODO do not delete but adjust the path
						return true;
					}
				}

			}
		}
		return !matchFound;
	}

	private boolean allVerticesGot(Location[] locations, ArrayList<ArrayList<Vertice>> spanningNodes) {
		boolean[] locationsGot = new boolean[locations.length];
		for (ArrayList<Vertice> nodes : spanningNodes) {
			for (Vertice location : nodes) {
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

	private boolean alreadyVisited(Vertice location, ArrayList<Vertice> alreadyVisitedList) {
		for (Vertice vertice : alreadyVisitedList) {
			if (location.getName().equals(vertice.getName())) {
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
