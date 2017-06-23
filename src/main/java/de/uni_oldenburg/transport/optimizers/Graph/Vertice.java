package de.uni_oldenburg.transport.optimizers.Graph;

import de.uni_oldenburg.transport.Location;

import java.util.ArrayList;

public class Vertice {

	private ArrayList<Vertice> childNodes;

	private Vertice parentLocation;
	private int expenseToParentLocation;

	private String name;

	Location locationReference;

	public Vertice(Location location, Vertice parentLocation, int expenseToParentLocation) {
		this.locationReference = location;
		this.parentLocation = parentLocation;
		this.expenseToParentLocation = expenseToParentLocation;
		this.childNodes = new ArrayList<>();
		this.name = location.getName();
	}

	public boolean addChild(Vertice child) {
		return this.childNodes.add(child);
	}

	public ArrayList<Vertice> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(ArrayList<Vertice> childNodes) {
		this.childNodes = childNodes;
	}

	public Vertice getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Vertice parentLocation) {
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
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
