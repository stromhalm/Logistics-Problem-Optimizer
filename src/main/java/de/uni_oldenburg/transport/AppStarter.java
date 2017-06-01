package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.csv.DeliveryCSVLoader;
import de.uni_oldenburg.transport.csv.TransportNetworkCSVLoader;

import java.util.ArrayList;

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

		if (args.length > 0) {
			System.out.println("Using custom data files.");
			networkFile = args[0];
			deliveryFile = args[1];
		} else {
			System.out.println("No data files passed. Using default ressources.");
			networkFile = "src/main/resources/Logistiknetz.csv";
			deliveryFile = "src/main/resources/Lieferliste.csv";
		}

		try {

			TransportNetworkCSVLoader transportNetworkCSVLoader = new TransportNetworkCSVLoader(networkFile);
			transportNetwork = transportNetworkCSVLoader.getTransportNetwork();

			DeliveryCSVLoader deliveryCSVLoader = new DeliveryCSVLoader(deliveryFile, transportNetwork);
			transportNetwork = deliveryCSVLoader.getTransportNetworkWithDeliveries();

		} catch (Exception e) {
			System.out.println("Error when reading the input files");
		}


	}
}