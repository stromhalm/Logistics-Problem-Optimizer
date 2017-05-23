package de.uni_oldenburg.transport.List;

import de.uni_oldenburg.transport.Location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Extends and implements {@link CSVLoader}.
 */
public class LogisticsNetworkCSVLoader extends CSVLoader {
	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 * @throws FileNotFoundException The exception is thrown if the file is not found.
	 */
	public LogisticsNetworkCSVLoader(String file) throws FileNotFoundException {
		super(file);
	}

	@Override
	public ArrayList<Location> toList() throws Exception {
		DeliveryCSVLoader deliveryCSVLoader = new DeliveryCSVLoader("Lieferliste.csv");
		ArrayList<Location> deliveryList = deliveryCSVLoader.toList();

		String entry = this.bufferedReader.readLine(); // skips the first line containing just the column information.

		while ((entry = this.bufferedReader.readLine()) != null) {
			// separate the entry into its parts. Index 0 is the start locations name, index 1 is the destination locations name, index 2 is the expense needed to get to the destination location.
			String[] entrySet = entry.split(this.csvEntrySeperator);

			// create the Location and add to the list.
			Location start = null;
			Location destination = null;
			for (Location location : deliveryList) {
				if (location.getName().equals(entrySet[0])) {
					start = location;
				}
				if (location.getName().equals(entrySet[1])) {
					destination = location;
				}
			}
			// now we have found the edge between both locations and set them to both again using the reference.
			start.addNeighbouringLocation(destination, Integer.parseInt(entrySet[2]));
			destination.addNeighbouringLocation(start, Integer.parseInt(entrySet[2]));
		}
		return deliveryList; // the original deliveryList contains all neighbouring location due to the reference.
	}

}
