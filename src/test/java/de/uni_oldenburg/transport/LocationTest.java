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
	Location location;

	@Before
	public void setup() throws Exception {
		name = "Test town";
		location = new Location(name);
	}

	// getName()
	@Test
	public void getName_passValidName_confirms() throws Exception {
		assertEquals("The names must be equal!", "Test town", location.getName());
	}

	// getAmount()
	@Test
	public void getAmount_passValidAmount_confirms() throws Exception {
		location.setAmount(60);
		assertEquals("The amount must met the expected one.", 60, location.getAmount());
	}

	// getNeighbouringLocations()
	@Test
	public void getNeighbouringLocations_confirmsEmptyNeighbors() throws Exception {
		assertEquals("The neighbouring locations maps must be empty!", true, location.getNeighbouringLocations().isEmpty());
	}

	// addNeighbouringLocation()
	@Test
	public void addNeighbouringLocation_passValidNeighbouringLocationTwice_confirms() throws Exception {
		int expenseExpected = 100;
		int expenseExpected2 = 200;
		String neighbouringLocationName = "Neighbouring test town";
		String neighbouringLocationName2 = "Neighbouring test town2";

		Location neighbouringLocation = new Location(neighbouringLocationName);

		if (location.addNeighbouringLocation(neighbouringLocation, expenseExpected)) {
			long expenseActual = location.getNeighbouringLocations().get(neighbouringLocation);
			assertEquals("The expected expense must met the actual one.", expenseExpected, expenseActual);

			Location neighbouringLocation2 = new Location(neighbouringLocationName2);

			if (location.addNeighbouringLocation(neighbouringLocation2, expenseExpected2)) {
				expenseActual = location.getNeighbouringLocations().get(neighbouringLocation2);
				assertEquals("The expected expense must met the actual one.", expenseExpected2, expenseActual);
			}

		}
	}

	@Test
	public void addNeighbouringLocation_doublePassSameValidNeighbouringLocation_returnsFalse() throws Exception {
		int expenseExpected = 100;
		String neighbouringLocationName = "Neighbouring test town";
		Location neighbouringLocation = new Location(neighbouringLocationName);

		Location neighbouringLocation2 = new Location(neighbouringLocationName);

		if (location.addNeighbouringLocation(neighbouringLocation, expenseExpected)) {
			assertEquals("The location should not be added twice. The return value must met the expected one.", false, location.addNeighbouringLocation(neighbouringLocation2, expenseExpected));
		}
	}

}
