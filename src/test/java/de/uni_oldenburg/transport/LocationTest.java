package de.uni_oldenburg.transport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Location}.
 */
public class LocationTest {

	String name;
	int amount;
	HashMap<Location, Long> neighbouringLocations;
	Location instance;

	@Before
	public void setup() throws Exception {
		name = "Test town";
		amount = 60;
		neighbouringLocations = new HashMap<>();
		instance = new Location(name, amount, neighbouringLocations);
	}

	@After
	public void cleanup() {
		name = null;
		amount = -1;
		neighbouringLocations = null;
		instance = null;
	}

	// Constructor:
	@Test(expected = Exception.class)
	public void Location_passEmptyAndNullName_throwsException() throws Exception {
		try {
			new Location("", amount, neighbouringLocations); // tests for empty names
		} catch (Exception e) { // catch the first exception than try to force the next
			new Location(null, amount, neighbouringLocations); // tests for nullable names
		}
	}

	@Test(expected = Exception.class)
	public void Location_passNegativeAmount_throwsException() throws Exception {
		new Location(name, -1, neighbouringLocations);
	}

	@Test(expected = Exception.class)
	public void Location_passNullMap_throwsException() throws Exception {
		new Location(name, amount, null);
	}

	// getName():
	@Test
	public void getName_passValidName_confirms() throws Exception {
		assertEquals("The names must be equal!", "Test town", instance.getName());
	}

	// getAmount():
	@Test
	public void getAmount_passValidAmount_confirms() throws Exception {
		assertEquals("The amount must met the expected one.", 60, instance.getAmount());
	}

	// getNeighbouringLocations():
	@Test
	public void getNeighbouringLocations_passEmptyMap_confirms() throws Exception {
		assertEquals("The neighbouring locations maps must be empty!", true, instance.getNeighbouringLocations().isEmpty());
	}

	// addNeighbouringLocation():
	@Test
	public void addNeighbouringLocation_passValidNeighbouringLocationTwice_confirms() throws Exception {
		long expenseExpected = 100;
		long expenseExpected2 = 200;
		String neighbouringLocationName = "Neighbouring test town";
		String neighbouringLocationName2 = "Neighbouring test town2";

		Location neighbouringLocation = new Location(neighbouringLocationName, amount, new HashMap<>());

		if (instance.addNeighbouringLocation(neighbouringLocation, expenseExpected)) {
			long expenseActual = instance.getNeighbouringLocations().get(neighbouringLocation);
			assertEquals("The expected expense must met the actual one.", expenseExpected, expenseActual);

			Location neighbouringLocation2 = new Location(neighbouringLocationName2, amount, new HashMap<>());

			if (instance.addNeighbouringLocation(neighbouringLocation2, expenseExpected2)) {
				expenseActual = instance.getNeighbouringLocations().get(neighbouringLocation2);
				assertEquals("The expected expense must met the actual one.", expenseExpected2, expenseActual);
			}

		}
	}

	@Test
	public void addNeighbouringLocation_doublePassSameValidNeighbouringLocation_returnsFalse() throws Exception {
		long expenseExpected = 100;
		String neighbouringLocationName = "Neighbouring test town";
		Location neighbouringLocation = new Location(neighbouringLocationName, amount, new HashMap<>());

		Location neighbouringLocation2 = new Location(neighbouringLocationName, amount, new HashMap<>());

		if (instance.addNeighbouringLocation(neighbouringLocation, expenseExpected)) {
			assertEquals("The location should not be added twice. The return value must met the expected one.", false, instance.addNeighbouringLocation(neighbouringLocation2, expenseExpected));
		}
	}

}
