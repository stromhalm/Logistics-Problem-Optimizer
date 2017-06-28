package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.csv.DeliveryCSVLoader;
import de.uni_oldenburg.transport.csv.TransportNetworkCSVLoader;
import de.uni_oldenburg.transport.optimizers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the entry point to our optimizers
 */
public class AppStarter {

	/**
	 * The app's main method
	 *
	 * @param args CLI arguments
	 */
	public static void main(String[] args) {

		TransportNetwork transportNetwork = null;
		String networkFile;
		String deliveryFile;
		int optimizerId;

		if (args.length > 0) {
			optimizerId = Integer.parseInt(args[0]);
		} else {
			System.out.println("No optimizer specified. Comparing all.");
			optimizerId = -1;
		}

		if (args.length > 1) {
			System.out.println("Using custom data files.");
			networkFile = args[1];
			deliveryFile = args[2];
		} else {
			System.out.println("No data files passed. Using default resources.");
			networkFile = "src/main/resources/Logistiknetz.csv";
			deliveryFile = "src/main/resources/Lieferliste.csv";
		}

		try {

			TransportNetworkCSVLoader transportNetworkCSVLoader = new TransportNetworkCSVLoader(networkFile);
			transportNetwork = transportNetworkCSVLoader.getTransportNetwork();

			DeliveryCSVLoader deliveryCSVLoader = new DeliveryCSVLoader(deliveryFile, transportNetwork);
			transportNetwork = deliveryCSVLoader.getTransportNetworkWithDeliveries();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error when reading the input files");
			System.exit(1);
		}

		HashMap<Optimizer, Double> optimizers = new HashMap<>();

		switch (optimizerId) {
			case 0:
				optimizers.put(new PheromoneOptimizer(), -1.0);
				break;
			case 1:
				optimizers.put(new NorthWestCornerKruskalOptimizer(), -1.0);
				optimizers.put(new NorthWestCornerKruskalOptimizer(), -1.0);
				break;
			case 2:
				optimizers.put(new NorthWestCornerOwnOptimizer(), -1.0);
				break;
			case 3:
				//optimizers.put(new SavingsOptimizer(), -1.0);
				break;
			case 4:
				optimizers.put(new ShortestPathOptimizer(), -1.0);
				break;
			default:
				optimizers.put(new PheromoneOptimizer(), -1.0);
				optimizers.put(new ShortestPathOptimizer(), -1.0);
				optimizers.put(new NorthWestCornerKruskalOptimizer(), -1.0);
				optimizers.put(new NorthWestCornerOwnOptimizer(), -1.0);
				//optimizers.put(new SavingsOptimizer(), -1);
		}


		for (Map.Entry<Optimizer, Double> optimizerEntry : optimizers.entrySet()) {
			System.out.println("Running \"" + optimizerEntry.getKey().getClass().getSimpleName() + "\"");
			Solution solution = optimizerEntry.getKey().optimizeTransportNetwork(new TransportNetwork(transportNetwork.getLocationsDeepCopy()));
			if (solution.isValid()) {
				// Print solution
				System.out.println("Solution found:");
				System.out.println(solution.toString());
				optimizerEntry.setValue(solution.getTotalConsumption()); // update the consumption
			}
		}
		System.out.println("\nRanking:");
		for (Map.Entry<Optimizer, Double> optimizerEntry : optimizers.entrySet()) {
			if (optimizerEntry.getValue() < 0) {
				System.out.println("Optimizer \"" + optimizerEntry.getKey().getClass().getSimpleName() + "\": No valid solution found");
			} else {
				System.out.println("Optimizer \"" + optimizerEntry.getKey().getClass().getSimpleName() + "\": " + optimizerEntry.getValue() + " consumed");
			}
		}
	}
}