package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import de.uni_oldenburg.transport.TransportNetwork;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DeliveryCSVLoader}.
 */
public class DeliveryCSVLoaderTest {

	private DeliveryCSVLoader instance;
	private TransportNetwork transportNetwork;

	@Before
	public void setup() throws Exception {
		TransportNetworkCSVLoader transportNetworkCSVLoader = new TransportNetworkCSVLoader("src/test/resources/shortenedLogistiknetz.csv");
		transportNetwork = transportNetworkCSVLoader.getTransportNetwork();
		instance = new DeliveryCSVLoader("src/test/resources/Lieferliste.csv", transportNetwork);
	}

	// getTransportNetworkWithDeliveries():
	@Test(expected = Exception.class)
	public void toList_passCorruptedFile_returnsNull() throws Exception {
		instance = new DeliveryCSVLoader("src/test/resources/test.csv", transportNetwork);
		instance.getTransportNetworkWithDeliveries();
	}

	@Test
	public void toList_readCorrectDeliveryFile_confirms() throws Exception {
		String expectedName1 = "Oldenburg";
		int expectedAmount1 = 40;

		String expectedName2 = "Bremen";
		int expectedAmount2 = 60;

		TransportNetwork transportNetwork = instance.getTransportNetworkWithDeliveries();
		Location[] locations = transportNetwork.getLocations();

		assertEquals("The deliveryList must contain only two elements.", 2, locations.length);

		assertEquals("The actual location name must met the expected one.", expectedName1, locations[1].getName());
		assertEquals("The actual locations amount must met the expected one.", expectedAmount1, locations[1].getAmount());

		assertEquals("The actual location name must met the expected one.", expectedName2, locations[0].getName());
		assertEquals("The actual locations amount must met the expected one.", expectedAmount2, locations[0].getAmount());
	}

}
