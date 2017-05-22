package de.uni_oldenburg.transport.Map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 */
	public CSVLoader(String file) {
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Maps the read CSV entries into a map representation of the entries.
	 *
	 * @return A specific mapping of the read CSV entries into a {@link HashMap}.
	 */
	public abstract HashMap toMap();

}
