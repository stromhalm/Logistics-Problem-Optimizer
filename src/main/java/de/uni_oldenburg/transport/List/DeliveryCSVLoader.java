package de.uni_oldenburg.transport.List;

import de.uni_oldenburg.transport.Location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Extends and implements {@link CSVLoader}.
 */
public class DeliveryCSVLoader extends CSVLoader {
	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 * @throws FileNotFoundException The exception is thrown if the file is not found.
	 */
	public DeliveryCSVLoader(String file) throws FileNotFoundException {
		super(file);
	}

	@Override
	public ArrayList<Location> toList() throws Exception {
		ArrayList<Location> deliveryList = new ArrayList<>();

		String entry = this.bufferedReader.readLine(); // skips the first line containing just the column information.

		while ((entry = this.bufferedReader.readLine()) != null) {
			// separate the entry into its parts. Index 0 is the locations name, index 1 is the amount needed by the location.
			String[] entrySet = entry.split(this.csvEntrySeperator);

			// create the Location and add to the list.
			Location location = new Location(entrySet[0], Integer.parseInt(entrySet[1]), new HashMap());
			deliveryList.add(location);
		}
		return deliveryList;
	}
}
