package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.optimizers.Graph.Vertice;
import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.util.ArrayList;

/**
 * Implements the North-Wester-Corner method to solve the transport problem and optimize it. See VL 6 for further information. The method can be improved if the table organization is optimized, too.
 *
 * @see NorthWestCornerKruskalOptimizer
 * @see NorthWestCornerOwnOptimizer
 */
public abstract class NorthWestCornerOptimizer implements Optimizer {

	protected ArrayList<ArrayList<Vertice>> minimalSpanningNetwork;

	@Override
	public abstract Solution optimizeTransportNetwork(TransportNetwork transportNetwork);

	public ArrayList<Tour> doNorthWestCornerMethod(Location startLocation) {
		int[] kmToDriveOnRoute = new int[minimalSpanningNetwork.size()];
		int[] amountToDeliverOnRoute = new int[minimalSpanningNetwork.size()];

		ArrayList<Location> locationsDelivered = new ArrayList<>();

		for (int route = 0; route < minimalSpanningNetwork.size(); route++) {
			Vertice leaf = minimalSpanningNetwork.get(route).get(minimalSpanningNetwork.get(route).size() - 1);

			while (leaf.getParentLocation() != null) {
				if (!locationDeliveredAlready(locationsDelivered, leaf))
					amountToDeliverOnRoute[route] += leaf.getLocationReference().getAmount();
				kmToDriveOnRoute[route] += leaf.getExpenseToParentLocation();
				locationsDelivered.add(leaf.getLocationReference());
				leaf = leaf.getParentLocation();
			}
			//System.out.println("Route: " + route + ": " + amountToDeliverOnRoute[route] + " (amountNeeded) and " + kmToDriveOnRoute[route] + "(kmToDrive)");
		}

		ArrayList<Tour> tours = new ArrayList<>();
		locationsDelivered = new ArrayList<>();
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

	@Override
	public String toString() {
		String spanningNetwork = "";
		for (ArrayList<Vertice> tours : minimalSpanningNetwork) {
			spanningNetwork += "Possible tour: ";
			for (Vertice vertice : tours) {
				spanningNetwork += vertice.getName() + " to ";
			}
			spanningNetwork += "\n";
		}
		return spanningNetwork;
	}

}
