package de.uni_oldenburg.transport.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link LogisticsNetworkCSVLoader}.
 */
public class LogisticsNetworkCSVLoaderTest {

	private CSVLoader instance;

	@Before
	public void setup() throws Exception {
		instance = new LogisticsNetworkCSVLoader("Logistiknetz.csv");
	}

	@After
	public void cleanup() {
		instance = null;
	}

	// toMap():
	@Test
	public void toMap_passCorruptedFile_returnsNull() throws Exception {
		// TODO implement
	}

	@Test
	public void toMap_readCorrectFile_confirms() throws Exception {
		// TODO implement
	}
}
