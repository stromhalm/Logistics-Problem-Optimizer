package de.uni_oldenburg.transport.List;

import de.uni_oldenburg.transport.Location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
		// TODO implement
		return null;
	}

}
