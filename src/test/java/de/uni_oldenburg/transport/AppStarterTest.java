package de.uni_oldenburg.transport;

import org.junit.Test;

/**
 * Tests {@link AppStarter}.
 */
public class AppStarterTest {

	AppStarter appStarter = new AppStarter();

	@Test
	public void main() throws Exception {
		appStarter.main(new String[0]);
	}
}