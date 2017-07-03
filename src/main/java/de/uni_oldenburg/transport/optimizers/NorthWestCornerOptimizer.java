package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Vertex;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements the North-Wester-Corner method to solve the transport problem and optimize it. See VL 6 for further information. The method can be improved if the table organization is optimized, too.
 *
 * @see NorthWestCornerKruskalOptimizer
 * @see NorthWestCornerOwnOptimizer
 */
public abstract class NorthWestCornerOptimizer implements Optimizer {

	protected ArrayList<ArrayList<Vertex>> spanningNetwork;

	@Override
	public abstract Solution optimizeTransportNetwork(TransportNetwork transportNetwork);

	public ArrayList<Tour> doNorthWestCornerMethod(Location startLocation, TransportNetwork transportNetwork) {
		int[] kmToDriveOnRoute = new int[spanningNetwork.size()];
		int[] amountToDeliverOnRoute = new int[spanningNetwork.size()];

		ArrayList<Location> locationsDelivered = new ArrayList<>();

		for (int route = 0; route < spanningNetwork.size(); route++) {
			Vertex leaf = spanningNetwork.get(route).get(spanningNetwork.get(route).size() - 1);

			while (leaf.getParentLocation() != null) {
				if (!locationDeliveredAlready(locationsDelivered, leaf))
					amountToDeliverOnRoute[route] += leaf.getLocationReference().getAmount();
				kmToDriveOnRoute[route] += leaf.getExpenseToParentLocation();
				locationsDelivered.add(leaf.getLocationReference());
				leaf = leaf.getParentLocation();
			}
			////System.out.println("Route: " + route + ": " + amountToDeliverOnRoute[route] + " (amountNeeded) and " + kmToDriveOnRoute[route] + "(kmToDrive)");
		}

		ArrayList<Tour> tours = new ArrayList<>();
		locationsDelivered = new ArrayList<>();
		for (int route = 0; route < spanningNetwork.size(); route++) {
			ArrayList<SmallTruck> smallTrucks = new ArrayList<>();
			ArrayList<MediumTruck> mediumTrucks = new ArrayList<>();
			ArrayList<LargeTruck> largeTrucks = new ArrayList<>();

			ArrayList<Vertex> vertices = spanningNetwork.get(route);
			boolean trucksConstellationPut = false;
			do {
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
			for (Vertex vertex : vertices) {
				int locationAmount = (locationDeliveredAlready(locationsDelivered, vertex) ? 0 : vertex.getLocationReference().getAmount());
				while (locationAmount != 0) {
					int unload;
					if (lastTruck == null || lastTruck.getUnloaded() - lastTruck.getCapacity() == 0) {
						if (largeTrucks.size() != 0) {
							unload = Math.min(LargeTruck.CAPACITY, locationAmount);
							lastTruck = largeTrucks.get(0);
							lastTour = addPaths(lastTruck, startLocation, vertex, unload, transportNetwork);
							largeTrucks.remove(lastTruck);
						} else if (mediumTrucks.size() != 0) {
							unload = Math.min(MediumTruck.CAPACITY, locationAmount);
							lastTruck = mediumTrucks.get(0);
							lastTour = addPaths(lastTruck, startLocation, vertex, unload, transportNetwork);
							mediumTrucks.remove(lastTruck);
						} else {
							unload = Math.min(SmallTruck.CAPACITY, locationAmount);
							lastTruck = smallTrucks.get(0);
							lastTour = addPaths(lastTruck, startLocation, vertex, unload, transportNetwork);
							smallTrucks.remove(lastTruck);
						}
						//System.out.print(startLocation.getName() + " with " + computeExpense(vertex) + " to " + lastTour.getTourDestinations()[lastTour.getTourDestinations().length - 1].getDestination().getName());
						tours.add(lastTour);
					} else {
						unload = Math.min(lastTruck.getCapacity() - lastTruck.getUnloaded(), locationAmount);
						//System.out.print(" with " + vertex.getExpenseToParentLocation() + " to " + lastTour.getTourDestinations()[lastTour.getTourDestinations().length - 1].getDestination().getName());
						lastTour.addDestination(new TourDestination(vertex.getLocationReference(), unload));
					}
					locationAmount -= unload;
					lastTruck.unload(unload);
					if (lastTruck.getCapacity() - lastTruck.getUnloaded() == 0 || vertices.indexOf(vertex) == vertices.size() - 1) { // send the last truck home if he is empty or the route is forefilled
						Location from = lastTour.getTourDestinations()[lastTour.getTourDestinations().length - 1].getDestination();
						Location to = lastTour.getStartLocation();
						LinkedHashMap<Location, Integer> pathBack = transportNetwork.getShortestPath(from, to);
						Map.Entry<Location, Integer> lastSubPath = null;
						for (Map.Entry<Location, Integer> subPath : pathBack.entrySet()) {
							if (lastSubPath == null) {
								lastSubPath = subPath;
							} else {
								lastTour.addDestination(new TourDestination(subPath.getKey(), 0));
								//System.out.print(" with " + lastSubPath.getValue() + " to " + subPath.getKey().getName());
								lastSubPath = subPath;
							}

						}
						//System.out.println();
						//System.out.println(lastTruck.getCapacity() - lastTruck.getUnloaded() + " capacity left in the truck.");
					}
				}
				locationsDelivered.add(vertex.getLocationReference());
			}
		}
		return tours;
	}

	private Tour addPaths(AbstractTruck lastTruck, Location startLocation, Vertex vertex, int unload, TransportNetwork transportNetwork) {
		Tour tour = new Tour(lastTruck, startLocation);
		LinkedHashMap<Location, Integer> pathBack = transportNetwork.getShortestPath(startLocation, vertex.getLocationReference());
		Map.Entry<Location, Integer> lastSubPath = null;
		for (Map.Entry<Location, Integer> subPath : pathBack.entrySet()) {
			if (lastSubPath == null) {
				lastSubPath = subPath;
			} else {
				tour.addDestination(new TourDestination(subPath.getKey(), (subPath.getKey().getName().equals(vertex.getName()) ? unload : 0)));
				lastSubPath = subPath;
			}

		}
		return tour;
	}

	private boolean locationDeliveredAlready(ArrayList<Location> locationsDelivered, Vertex vertex) {
		for (Location location : locationsDelivered) {
			if (vertex.getName().equals(location.getName())) return true;
		}
		return false;
	}
}
