package de.uni_oldenburg.transport;

import org.junit.Test;

/**
 * Tests {@link AppStarter}.
 */
public class AppStarterTest {

	@Test
	public void AppStarter_testDefaultConstructor_throwsNoError() {
		// Is a test to get the code coverage for the class line.
		new AppStarter();
	}

	@Test
	public void main_chooseOptimzerAndFiles() throws Exception {
		String[] args = {"0", "src/main/resources/Logistiknetz.csv", "src/main/resources/Lieferliste.csv"};
		AppStarter.main(args);
	}

	@Test
	public void main_chooseOptimzer() throws Exception {
		String[] args = {"0"};
		AppStarter.main(args);
	}

	@Test
	public void main_noArgs() throws Exception {
		AppStarter.main(new String[0]);
	}

}
