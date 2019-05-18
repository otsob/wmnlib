/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.elements.Score;

public class MusicXmlWriterDomTest {

	public MusicXmlWriterDomTest() {
	}

	private Score readScore(String path) {
		return TestHelper.readScore(path);
	}

	@Test
	public void testWritingSingleC() {

	}

}
