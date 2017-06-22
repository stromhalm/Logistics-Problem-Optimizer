package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.TransportNetwork;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implements the North-Wester-Corner method to solve the transport problem and optimize it. See VL 6 for further information.
 */
public class NorthWestCornerOptimizer implements Optimizer {
	@Override
	public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {

		ArrayList<ArrayList<Location>> networkCycles = getNetworkCycles(transportNetwork.getLocations(), transportNetwork.getStartLocation());

		return null;
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
			} else {
				counter++;
			}
			cycles.add(cycle);
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
			if (startLocation.getName().equals("Hamburg") && location.getName().equals("Kiel")){
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

	private boolean alreadyVisited(Location key, ArrayList<Location> alreadyVisitiedList) {
		for (Location location : alreadyVisitiedList) {
			if (key.getName().equals(location.getName())) {
				return true;
			}
		}
		return false;
	}

}
