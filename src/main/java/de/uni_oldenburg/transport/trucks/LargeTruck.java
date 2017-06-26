package de.uni_oldenburg.transport.trucks;

/**
 * A truck of large size (type 3)
 */
public class LargeTruck extends AbstractTruck {

	public static final int CAPACITY = 70;

	/**
	 * Basic constructor
	 */
	public LargeTruck() {
		super(CAPACITY, 35);
	}
}
