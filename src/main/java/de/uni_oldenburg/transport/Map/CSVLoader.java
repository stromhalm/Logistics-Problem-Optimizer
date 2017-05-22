package de.uni_oldenburg.transport.Map;

import de.uni_oldenburg.transport.AppStarter;
import sun.tools.jar.Main;

import java.io.*;
import java.util.HashMap;

/**
 * The CSVLoader reads data from a CSV file and put them into an appropriate {@link HashMap}. A CSVLoader can either be a {@link DeliveryCSVLoader} or a {@link LogisticsNetworkCSVLoader}.
 */
public abstract class CSVLoader {

	/**
	 * Is a standard {@link BufferedReader} to read the CSV and preserve them.
	 */
	BufferedReader bufferedReader;
	/**
	 * Defines which separator is used in the string entries.
	 */
	String csvEntrySeperator = ";";
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
	 * Maps the read CSV entries into a map representation of the entries.
	 *
	 * @return A specific mapping of the read CSV entries into a {@link HashMap} or null if any error occurred.
	 */
	public abstract HashMap toMap();

	/**
	 * Is used to reset the resources location.
	 *
	 * @param resources Is the path to the resource folder.
	 */
	public static void setResources(String resources) {
		CSVLoader.resources = resources;
	}


}
