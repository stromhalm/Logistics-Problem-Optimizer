package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link TransportNetworkCSVLoader}.
 */
public class TransportNetworkCSVLoaderTest {

	private TransportNetworkCSVLoader instance;

	@Before
	public void setup() throws Exception {
		CSVLoader.setResourcesFolder("src/test/resources/");
		instance = new TransportNetworkCSVLoader("shortenedLogistiknetz.csv");
	}


	@Test(expected = Exception.class)
	public void toList_passCorruptedFile_throwException() throws IOException {
		instance = new TransportNetworkCSVLoader("test.csv");
		instance.getTransportNetwork();
	}

	@Test
	public void toList_readCorrectFile_confirms() throws Exception {
		String expectedName = "Oldenburg";

		String expectedNeighbouringLocationName = "Bremen";
		int expectedExpense = 55;

		TransportNetwork transportNetwork = instance.getTransportNetwork();
		Location[] locations = transportNetwork.getLocations();

		assertEquals("The logisticsNetworkList must contain only two elements.", 2, locations.length);
		assertEquals("The actual location name must match the expected one.", expectedName, locations[1].getName());
		assertEquals("The actual location name must match the expected one.", expectedNeighbouringLocationName, locations[0].getName());
		assertEquals("The actual expense must match the expected one.", expectedExpense, (long) locations[0].getNeighbouringLocations().get(locations[1]));

	}

}
