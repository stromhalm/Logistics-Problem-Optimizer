package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Extends and implements {@link CSVLoader}.
 */
public class TransportNetworkCSVLoader extends CSVLoader {

	private HashMap<String, Location> locationHashMap = new HashMap<>();

	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 * @throws FileNotFoundException The exception is thrown if the file is not found.
	 */
	public TransportNetworkCSVLoader(String file) throws FileNotFoundException {
		super(file);
	}

	public TransportNetwork getTransportNetwork() throws IOException {

		String entry;

		this.bufferedReader.readLine(); // skips the first line containing just the column information.

		while ((entry = this.bufferedReader.readLine()) != null) {
			// separate the entry into its parts. Index 0 is the start locations name,
			// index 1 is the destination locations name, index 2 is the expense needed to get to the destination location.
			String[] entrySet = entry.split(this.csvEntrySeperator);
			String locationName1 = entrySet[0];
			String locationName2 = entrySet[1];
			String distanceValue = entrySet[2];

			// create the Location and add to the list.
			int distance = Integer.parseInt(distanceValue);

			if (locationHashMap.containsKey(locationName1) && !locationHashMap.containsKey(locationName2)) {
				// create new destination
				locationHashMap.put(locationName2, new Location(locationName2));
			} else if (locationHashMap.containsKey(locationName2) && !locationHashMap.containsKey(locationName1)) {
				// create new start
				locationHashMap.put(locationName1, new Location(locationName1));
			} else if ( !locationHashMap.containsKey(locationName1) && !locationHashMap.containsKey(locationName2)){
				// create new start
				locationHashMap.put(locationName1, new Location(locationName1));
				// create new destination
				locationHashMap.put(locationName2, new Location(locationName2));
			}

			locationHashMap.get(locationName1).addNeighbouringLocation(locationHashMap.get(locationName2), distance);
			locationHashMap.get(locationName2).addNeighbouringLocation(locationHashMap.get(locationName1), distance);
		}

		TransportNetwork transportNetwork = new TransportNetwork(locationHashMap.values().toArray(new Location[0]));

		return transportNetwork;
	}

}
