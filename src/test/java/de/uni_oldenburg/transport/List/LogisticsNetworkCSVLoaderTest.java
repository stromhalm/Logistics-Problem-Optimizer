package de.uni_oldenburg.transport.List;

import de.uni_oldenburg.transport.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests {@link LogisticsNetworkCSVLoader}.
 */
public class LogisticsNetworkCSVLoaderTest {

	private CSVLoader instance;

	@Before
	public void setup() throws Exception {
		CSVLoader.setResources("src/test/resources/");
		instance = new LogisticsNetworkCSVLoader("shortenedLogistiknetz.csv");
	}

	@After
	public void cleanup() {
		instance = null;
		CSVLoader.setResources("src/main/resources/");
	}

	// toList():
	@Test
	public void toList_passCorruptedFile_returnsNull() throws Exception {
		instance = new LogisticsNetworkCSVLoader("test.csv");
		ArrayList<Location> logisticsNetworkList = instance.toList();
	}

	@Test
	public void toList_readCorrectFile_confirms() throws Exception {
		// TODO implement
	}
}
