/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import wmnlibio.musicxml.MusicXmlReader;
import wmnlibnotation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public class PointSetTest {

	public PointSetTest() {
	}

	@Before
	public void setUp() {
	}

	@Test
	public void testCreatingFromSingleStaffScore() {
		MusicXmlReader reader = MusicXmlReader.getReader(false);
		try {
			Score score = reader.readScore(Paths.get("test/testfiles/musicxml/twoMeasures.xml"));
			PointSet pointset = new PointSet(score);
			System.out.println(score);
			System.out.println(pointset);
			fail("This test is not implemented yet");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}