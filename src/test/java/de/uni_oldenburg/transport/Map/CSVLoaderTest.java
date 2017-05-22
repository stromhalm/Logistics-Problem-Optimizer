package de.uni_oldenburg.transport.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Tests {@link CSVLoader}.
 */
public class CSVLoaderTest {

	private CSVLoader instance;

	@Before
	public void setup() {
		// nothing to do ...
	}

	@After
	public void cleanup() {
		CSVLoader.setResources("src/main/resources/");
	}

	// Constructor:
	@Test(expected = FileNotFoundException.class)
	public void CSVLoader_passInvalidFile_throwsIOException() throws FileNotFoundException {
		String file = "invalid.csv";
		this.instance = new CSVLoader(file) {
			@Override
			public HashMap toMap() {
				return null;
			}
		};
	}

	@Test
	public void CSVLoader_passValidFile_throwsNoIOException() throws Exception {
		CSVLoader.setResources("src/test/resources/");
		String file = "test.csv";
		this.instance = new CSVLoader(file) {
			@Override
			public HashMap toMap() {
				return null;
			}
		};
	}

}
