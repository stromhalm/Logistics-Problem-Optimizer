package de.uni_oldenburg.transport.csv;

import de.uni_oldenburg.transport.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DeliveryCSVLoader}.
 */
public class DeliveryCSVLoaderTest {

	private CSVLoader instance;

	@Before
	public void setup() throws Exception {
		CSVLoader.setResources("src/test/resources/");
		instance = new DeliveryCSVLoader("Lieferliste.csv");
	}

	@After
	public void cleanup() {
		instance = null;
		CSVLoader.setResources("src/main/resources/");
	}

	// toList():
	@Test(expected = Exception.class)
	public void toList_passCorruptedFile_returnsNull() throws Exception {
		instance = new DeliveryCSVLoader("test.csv");
		instance.toList();
	}

	@Test
	public void toList_readCorrectDeliveryFile_confirms() throws Exception {
		String expectedName1 = "Oldenburg";
		int expectedAmount1 = 40;

		String expectedName2 = "Bremen";
		int expectedAmount2 = 60;

		ArrayList<Location> deliveryList = instance.toList();

		assertEquals("The deliveryList must contain only two elements.", 2, deliveryList.size());

		assertEquals("The actual location name must met the expected one.", expectedName1, deliveryList.get(0).getName());
		assertEquals("The actual locations amount must met the expected one.", expectedAmount1, deliveryList.get(0).getAmount());
		assertEquals("The actual locations neighbouring locations must be empty and therefore true.", true, deliveryList.get(0).getNeighbouringLocations().isEmpty());

		assertEquals("The actual location name must met the expected one.", expectedName2, deliveryList.get(1).getName());
		assertEquals("The actual locations amount must met the expected one.", expectedAmount2, deliveryList.get(1).getAmount());
		assertEquals("The actual locations neighbouring locations must be empty and therefore true.", true, deliveryList.get(1).getNeighbouringLocations().isEmpty());

	}
}
