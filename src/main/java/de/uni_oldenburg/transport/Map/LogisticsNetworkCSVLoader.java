package de.uni_oldenburg.transport.Map;

import java.io.BufferedReader;
import java.util.HashMap;

/**
 * Extends and implements {@link CSVLoader}.
 */
public class LogisticsNetworkCSVLoader extends CSVLoader {
	/**
	 * Constructor to start the CSV reading from file and store it in a {@link BufferedReader} instance.
	 *
	 * @param file Is the file with file name and path.
	 */
	public LogisticsNetworkCSVLoader(String file) {
		super(file);
	}

	@Override
	public HashMap toMap() {
		// TODO implement
		return null;
	}

}
