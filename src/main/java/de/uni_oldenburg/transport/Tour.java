package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.trucks.AbstractTruck;

import java.util.ArrayList;

/**
 * A single tour of one truck on our map with multiple destinations
 */
public class Tour {

	private final AbstractTruck truck;
	private ArrayList<TourDestination> destinations;
	private int consumption;

	private final Location startLocation;

	/**
	 * Constructor
	 *
	 * @param truck The truck to be used in this tour
	 */
	public Tour(final AbstractTruck truck, final Location startLocation) {
		this.truck = truck;
		this.destinations = new ArrayList<>();
		consumption = 0;
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
		destinations.add(tourDestination);
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
		return destinations.remove(tourDestination);
	}

	/**
	 * Get all of this tours destinations
	 *
	 * @return This tours destinations
	 */
	public TourDestination[] getTourDestinations() {
		return destinations.toArray(new TourDestination[0]);
	}

	/**
	 * Checks if the tour is valid for this type of truck
	 *
	 * @return True if valid, else false
	 */
	public boolean isValid() {

		// Verify truck load
		int load = 0;
		for (TourDestination tourDestination : destinations) {
			load += tourDestination.getUnload();
		}
		return (load <= truck.getCapacity());
	}

	private void addConsumption(int expense) {
		this.consumption += truck.getConsumption() * expense;
	}

	private void subtractConsumption(int expense) {
		this.consumption -= truck.getConsumption() * expense;
	}

	public int getConsumption() {
		return consumption;
	}

	public Location getStartLocation() {
		return startLocation;
	}
}
