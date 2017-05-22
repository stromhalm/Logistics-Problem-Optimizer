package de.uni_oldenburg.transport.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
		// nothing to do ...
	}

	// Constructor:
	@Test(expected = IOException.class)
	public void CSVLoader_passInvalidFile_throwsIOException() throws IOException {
		String file = "invalid.csv";
		this.instance = new CSVLoader(file) {
			@Override
			public HashMap toMap() {
				return null;
			}
		};
	}

	@Test(expected = IOException.class)
	public void CSVLoader_passValidFile_throwsNoIOException() throws IOException {
		String file = "Liefer.csv";
		this.instance = new CSVLoader(file) {
			@Override
			public HashMap toMap() {
				return null;
			}
		};
	}

}
