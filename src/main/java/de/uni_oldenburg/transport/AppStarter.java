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

		ArrayList<Location> logisticsNetworkList = null;
		try {

			TransportNetworkCSVLoader transportNetworkCSVLoader = new TransportNetworkCSVLoader(args[0] /*Reference to the logistics network file*/);
			TransportNetwork transportNetwork = transportNetworkCSVLoader.getTransportNetwork();

			DeliveryCSVLoader deliveryCSVLoader = new DeliveryCSVLoader(args[1], transportNetwork);
			transportNetwork = deliveryCSVLoader.getTransportNetworkWithDeliveries();

		} catch (Exception e) {
			//System.exit(1); // cannot proceed
		}
		// TODO use the list
	}
}