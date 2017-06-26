package de.uni_oldenburg.transport.trucks;

/**
 * The basic truck functionality
 */
public abstract class AbstractTruck {

	private int capacity;
	private final int consumption;
	private int unloaded;

	/**
	 * Basic constructor
	 *
	 * @param capacity    The trucks maximum capacity
	 * @param consumption The trucks consumption in litres per 100km
	 */
	public AbstractTruck(int capacity, int consumption) {
		this.capacity = capacity;
		this.consumption = consumption;
		unloaded = 0;
	}

	/**
	 * Get the truck types capacity
	 *
	 * @return capacity of the truck
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Get the trucks consumption per 100km
	 *
	 * @return
	 */
	public int getConsumption() {
		return consumption;
	}

	public void unload(int unload) {
		this.unloaded += unload;
	}

	public int getUnloaded() {
		return unloaded;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
