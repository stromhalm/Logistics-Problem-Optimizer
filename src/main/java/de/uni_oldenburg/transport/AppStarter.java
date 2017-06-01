package de.uni_oldenburg.transport;

import de.uni_oldenburg.transport.csv.LogisticsNetworkCSVLoader;

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
			LogisticsNetworkCSVLoader logisticsNetworkCSVLoader = new LogisticsNetworkCSVLoader(args[0] /*Reference to the logistics network file*/);
			logisticsNetworkList = logisticsNetworkCSVLoader.toList();
		} catch (Exception e) {
			//System.exit(1); // cannot proceed
		}
		// TODO use the list
	}
}