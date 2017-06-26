package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;

/**
 * A simple implementation for a graph vertex. The vertex provides a reference to {@link Location}s. It is easier to do graph operations with the standard graph layout containing vertices.
 */
public class Vertex {

	private Vertex parentLocation;
	private int expenseToParentLocation;

	private Location locationReference;

	public Vertex(Location location, Vertex parentLocation, int expenseToParentLocation) {
		this.locationReference = location;
		this.parentLocation = parentLocation;
		this.expenseToParentLocation = expenseToParentLocation;
	}

	public Vertex getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Vertex parentLocation) {
		this.parentLocation = parentLocation;
	}

	public int getExpenseToParentLocation() {
		return expenseToParentLocation;
	}

	public void setExpenseToParentLocation(int expenseToParentLocation) {
		this.expenseToParentLocation = expenseToParentLocation;
	}

	public Location getLocationReference() {
		return locationReference;
	}

	public void setLocationReference(Location locationReference) {
		this.locationReference = locationReference;
	}

	public String getName() {
		return locationReference.getName();
	}
}
