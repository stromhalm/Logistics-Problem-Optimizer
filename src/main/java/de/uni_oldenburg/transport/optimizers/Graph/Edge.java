package de.uni_oldenburg.transport.optimizers.Graph;

public class Edge {

	private Vertice vertice1;
	private Vertice vertice2;

	private int weight;

	public Edge(Vertice vertice1, Vertice vertice2, int weight) {
		this.vertice1 = vertice1;
		this.vertice2 = vertice2;
		this.weight = weight;
	}

	public Vertice getVertice1() {
		return vertice1;
	}

	public void setVertice1(Vertice vertice1) {
		this.vertice1 = vertice1;
	}

	public Vertice getVertice2() {
		return vertice2;
	}

	public void setVertice2(Vertice vertice2) {
		this.vertice2 = vertice2;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
