package de.uni_oldenburg.transport.trucks;

/**
 * A truck of medium size (type 2)
 */
public class MediumTruck extends AbstractTruck {

	public static final int CAPACITY = 50;

	/**
	 * Basic constructor
	 */
	public MediumTruck() {
		super(CAPACITY, 25);
	}

	@Override
	public String toString() {
		return "MediumTruck";
	}
}
