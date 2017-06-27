package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.trucks.AbstractTruck;

import java.util.ArrayList;

/**
 * A single tour of one truck on our map with multiple destinations
 */
public class Tour {

	private final AbstractTruck truck;
	private ArrayList<TourDestination> tourDestinations;
	private int consumption;
	private int kilometersToDrive;

	private final Location startLocation;

	/**
	 * Constructor
	 *
	 * @param truck The truck to be used in this tour
	 */
	public Tour(final AbstractTruck truck, final Location startLocation) {
		this.truck = truck;
		this.tourDestinations = new ArrayList<>();
		consumption = 0;
		kilometersToDrive = 0;
		this.startLocation = startLocation;
	}

	/**
	 * Get the actual type of truck
	 *
	 * @return
	 */
	public AbstractTruck getTruck() {
		return truck;
	}

	/**
	 * Add a destination to this tour
	 *
	 * @param tourDestination The destination to add
	 * @param expense         The expense to the location.
	 */
	public void addDestination(TourDestination tourDestination, int expense) {
		tourDestinations.add(tourDestination);
		tourDestination.setExpense(expense); // TODO delete if not wanted
		kilometersToDrive += expense;
		addConsumption(expense);
	}

	/**
	 * Remove a destination from this tour
	 *
	 * @param tourDestination The destination to remove
	 * @param expense         The expense to the location.
	 * @return False if tour was not found
	 */
	public boolean removeTourDestination(TourDestination tourDestination, int expense) {
		subtractConsumption(expense);
		kilometersToDrive -= expense;
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

	/**
	 * Checks if the tour is valid for this type of truck
	 *
	 * @return True if valid, else false
	 */
	public boolean isValid() {

		// Verify truck load
		int load = 0;
		for (TourDestination tourDestination : tourDestinations) {
			load += tourDestination.getUnload();
		}
		if (load > truck.getCapacity()) {
			// Error Service
			System.out.println("Truck was overloaded with a load of " + load + " (maximum capacity " + truck.getCapacity() + ")");
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

	public void addConsumption(int expense) {
		this.consumption += (truck.getConsumption() * expense) / 100;
	}

	public void subtractConsumption(int expense) {
		this.consumption -= truck.getConsumption() * expense;
	}

	public int getConsumption() {
		return consumption;
	}

	public Location getStartLocation() {
		return startLocation;
	}

	public int getKilometersToDrive() {
		return kilometersToDrive;
	}

	@Override
	public String toString() {
		String string = "";
		string += "Drive " + kilometersToDrive + "km with a " + truck.toString() + " consuming " + consumption + " liters of gas from " + startLocation.getName() + " over: \n";

		for (TourDestination tourDestination : tourDestinations) {
			string += tourDestination.toString() + " while consuming " + (tourDestination.getExpense() * truck.getConsumption() / 100) + " liters of gas\n";
		}
		return string;
	}
}
