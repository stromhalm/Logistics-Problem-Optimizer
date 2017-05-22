package de.uni_oldenburg.transport.Map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
	public HashMap toMap() {
		// TODO implement
		return null;
	}

}
