package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

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
	@Test(expected = Exception.class)
	public void toList_passCorruptedFile_returnsNull() throws Exception {
		instance = new LogisticsNetworkCSVLoader("test.csv");
		instance.toList();
	}

	@Test
	public void toList_readCorrectFile_confirms() throws Exception {
		String expectedName = "Oldenburg";
		int expectedAmount = 40;

		String expectedNeighbouringLocationName = "Bremen";
		int expectedNeighbouringLocationAmount = 60;
		long expectedExpense = 55;

		ArrayList<Location> logisticsNetworkList = instance.toList();

		assertEquals("The logisticsNetworkList must contain only two element.", 2, logisticsNetworkList.size());

		assertEquals("The actual location name must met the expected one.", expectedName, logisticsNetworkList.get(0).getName());
		assertEquals("The actual locations amount must met the expected one.", expectedAmount, logisticsNetworkList.get(0).getAmount());

		assertEquals("The actual location name must met the expected one.", expectedNeighbouringLocationName, logisticsNetworkList.get(1).getName());
		assertEquals("The actual locations amount must met the expected one.", expectedNeighbouringLocationAmount, logisticsNetworkList.get(1).getAmount());

		// Test the neighbouringLocations from the first Location
		HashMap<Location, Long> neighbouringLocations = logisticsNetworkList.get(0).getNeighbouringLocations();
		assertNeighbouringLocations(neighbouringLocations, expectedNeighbouringLocationName, expectedNeighbouringLocationAmount, expectedExpense);

		// Test the neighbouringLocations from the second Location
		neighbouringLocations = logisticsNetworkList.get(1).getNeighbouringLocations();
		assertNeighbouringLocations(neighbouringLocations, expectedName, expectedAmount, expectedExpense);

	}

	// Helper
	private void assertNeighbouringLocations(HashMap<Location, Long> neighbouringLocations, String expectedName, int expectedNeighbouringLocationAmount, long expectedExpense) {
		assertEquals("The neighbouringLocations map must contain only one element.", 1, neighbouringLocations.size());

		for (Location neighbouringLocation : neighbouringLocations.keySet()) {
			assertEquals("The actual neighbouring location name must met the expected one.", expectedName, neighbouringLocation.getName());
			assertEquals("The actual neighbouring locations amount must met the expected one.", expectedNeighbouringLocationAmount, neighbouringLocation.getAmount());

			long actualExpense = neighbouringLocations.get(neighbouringLocation);
			assertEquals("The actual expense must met the expected one.", expectedExpense, actualExpense);

		}
	}

}
