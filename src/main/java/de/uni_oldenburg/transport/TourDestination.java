package de.uni_oldenburg.transport;

/**
 * A single destination that is part of a truck tour
 */
public class TourDestination {

	private Location destination;
	private int unload;

	public TourDestination(Location destination, int unload) {
		this.destination = destination;
		this.unload = unload;
	}

	/**
	 * Get the discharge amount for this destination
	 *
	 * @return Discharge amount
	 */
	public int getUnload() {
		return unload;
	}

	/**
	 * Set the discharge amount fpr this destination
	 *
	 * @param unload Discharge amount
	 */
	public void setUnload(int unload) {
		this.unload = unload;
	}

	/**
	 * Get the destination
	 *
	 * @return A single destination in a tour
	 */
	public Location getDestination() {
		return destination;
	}

	/**
	 * Set the destination
	 *
	 * @param destination A single destination in a tour
	 */
	public void setDestination(Location destination) {
		this.destination = destination;
	}

}
