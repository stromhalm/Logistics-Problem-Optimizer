package de.uni_oldenburg.transport.trucks;

/**
 * A truck of small size (type 1)
 */
public class SmallTruck extends AbstractTruck {

	public static final int CAPACITY = 20;

	/**
	 * Basic constructor
	 */
	public SmallTruck() {
		super(CAPACITY, 15);
	}
}
