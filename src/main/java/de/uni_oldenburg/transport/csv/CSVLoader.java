package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;

import java.io.*;
import java.util.ArrayList;

/**
 * The CSVLoader reads data from a CSV file and puts them into an appropriate {@link ArrayList<Location>}. A CSVLoader can either be a {@link DeliveryCSVLoader} or a {@link TransportNetworkCSVLoader}.
 */
public abstract class CSVLoader {

	/**
	 * Is a standard {@link BufferedReader} to read the CSV and preserve them.
	 */
	protected BufferedReader bufferedReader;

	/**
	 * Defines which separator is used in the string entries.
	 */
	protected String csvEntrySeperator = ";";

	/**
	 * Is the path to the resources folder. This is a prefix added to the {@link FileReader}.
	 */
	private static String resources = "src/main/resources/";

	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 * @throws FileNotFoundException The exception is thrown if the file is not found.
	 */
	public CSVLoader(String file) throws FileNotFoundException {
		File fileName = new File(resources + file);
		InputStream inputStream = new FileInputStream(fileName);
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	}

	/**
	 * Is used to reset the resources location.
	 *
	 * @param resources Is the path to the resource folder.
	 */
	public static void setResourcesFolder(String resources) {
		CSVLoader.resources = resources;
	}

}
