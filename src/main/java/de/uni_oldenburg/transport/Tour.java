package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.trucks.AbstractTruck;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.util.ArrayList;

/**
 * A single tour of one truck on our map with multiple destinations
 */
public class Tour {

	private ArrayList<TourDestination> tourDestinations;
	private final Location startLocation;
	private AbstractTruck truck;

	/**
	 * Create Tour with specific Truck
	 *
	 * @param startLocation The start location
	 */
	public Tour(AbstractTruck truck, final Location startLocation) {
		this.truck = truck;
		this.tourDestinations = new ArrayList<>();
		this.startLocation = startLocation;
	}

	/**
	 * Create Tour without specific Truck (chosen automatically)
	 *
	 * @param startLocation The start location
	 */
	public Tour(final Location startLocation) {
		this.truck = truck;
		this.tourDestinations = new ArrayList<>();
		this.startLocation = startLocation;
	}

	/**
	 * Get the specified type of truck or an automatic truck type if not specified
	 *
	 * @return
	 */
	public AbstractTruck getTruck() {

		// Truck already specified?
		if (truck != null) return truck;

		// Choose truck automatically
		int tourLoad = getTourLoad();
		if (tourLoad <= SmallTruck.CAPACITY) {
			return new SmallTruck();
		} else if (tourLoad <= MediumTruck.CAPACITY) {
			return new MediumTruck();
		} else {
			return new LargeTruck();
		}
	}

	/**
	 * Add a destination to this tour
	 *
	 * @param tourDestination The destination to add
	 */
	public void addDestination(TourDestination tourDestination) {
		tourDestinations.add(tourDestination);
	}

	/**
	 * Remove a destination from this tour
	 *
	 * @param tourDestination The destination to remove
	 * @param expense         The expense to the location.
	 * @return False if tour was not found
	 */
	public boolean removeTourDestination(TourDestination tourDestination, int expense) {
		return tourDestinations.remove(tourDestination);
	}

	/**
	 * Get all of this tours destinations
	 *
	 * @return This tours destinations
	 */
	public TourDestination[] getTourDestinations() {
		return tourDestinations.toArray(new TourDestination[0]);
	}

	public int getTourLoad() {
		int load = 0;
		for (TourDestination tourDestination : tourDestinations) {
			load += tourDestination.getUnload();
		}
		return load;
	}


	/**
	 * Checks if the tour is valid for this type of truck
	 *
	 * @return True if valid, else false
	 */
	public boolean isValid() {

		// Verify truck load
		if (getTourLoad() > getTruck().getCapacity()) {
			// Error Service
			System.out.println("Truck was overloaded with a load of " + getTourLoad() + " (maximum capacity " + getTruck().getCapacity() + ")");
			return false;
		}

		// Verify truck returned to start
		Location lastDestination = tourDestinations.get(tourDestinations.size() - 1).getDestination();
		if (lastDestination != startLocation) {
			// Error Service
			System.out.println("Truck did not return to " + startLocation.getName() + " but stayed in " + lastDestination.getName());
			return false;
		}
		return true;
	}

	public double getConsumption() {
		return getKilometersToDrive() * (getTruck().getConsumption())/100;
	}

	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * Sum of the kilometers in this tour
	 *
	 * @return Total way length in km
	 */
	public int getKilometersToDrive() {

		int kilometersToDrive = 0;
		Location currentLocation = startLocation;
		for (TourDestination tourDestination : tourDestinations) {
			kilometersToDrive += currentLocation.getNeighbouringLocations().get(tourDestination.getDestination());
			currentLocation = tourDestination.getDestination();
		}
		return kilometersToDrive;
	}

	@Override
	public String toString() {
		String string = "";
		string += "Drive " + getKilometersToDrive() + "km with a " + getTruck().toString() + " consuming " + getConsumption() + " from " + startLocation + " over: \n";

		Location currentLocation = startLocation;
		for (TourDestination tourDestination : tourDestinations) {
			int destinationKilometers = currentLocation.getNeighbouringLocations().get(tourDestination.getDestination());
			double destinationConsumption = destinationKilometers * getTruck().getConsumption() / 100;
			currentLocation = tourDestination.getDestination();
			string += tourDestination + " (" + destinationKilometers + "km) while consuming " + destinationConsumption + " litres of gas\n";
		}
		return string;
	}
}
