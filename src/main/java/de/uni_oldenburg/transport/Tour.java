package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.trucks.AbstractTruck;

import java.util.ArrayList;

/**
 * A single tour of one truck on our map with multiple destinations
 */
public class Tour {

	private final AbstractTruck truck;
	private ArrayList<TourDestination> destinations;

	/**
	 * Constructor
	 *
	 * @param truck The truck to be used in this tour
	 */
	public Tour(final AbstractTruck truck) {
		this.truck = truck;
	}

	/**
	 * Add a destination to this tour
	 *
	 * @param tourDestination The destination to add
	 */
	public void addDestination(TourDestination tourDestination) {
		destinations.add(tourDestination);
	}

	/**
	 * Remove a destination from this tour
	 *
	 * @param tourDestination The destination to remove
	 * @return False if tour was not found
	 */
	public boolean removeTourDestination(TourDestination tourDestination) {
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

}