package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

/**
 * Extends and implements {@link CSVLoader}.
 */
public class DeliveryCSVLoader extends CSVLoader {

	TransportNetwork transportNetwork;

	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 * @throws FileNotFoundException The exception is thrown if the file is not found.
	 */
	public DeliveryCSVLoader(String file, TransportNetwork transportNetwork) throws FileNotFoundException {
		super(file);
		this.transportNetwork = transportNetwork;
	}

	/**
	 * Gets the network and sets the amount to deliver for each {@link Location}.
	 *
	 * @return the {@link TransportNetwork} with the locations amount.
	 * @throws Exception If any error occurs.
	 */
	public TransportNetwork getTransportNetworkWithDeliveries() throws Exception {

		String entry = this.bufferedReader.readLine(); // skips the first line containing just the column information.

		while ((entry = this.bufferedReader.readLine()) != null) {
			// separate the entry into its parts. Index 0 is the locations name, index 1 is the amount needed by the location.
			String[] entrySet = entry.split(this.csvEntrySeperator);
			String name = entrySet[0];
			String amount = entrySet[1];

			transportNetwork.getLocationByName(name).setAmount(Integer.parseInt(amount));
		}
		return transportNetwork;
	}
}
