/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import java.util.ArrayList;
import wmnlibnotation.noteobjects.Score;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import wmnlibnotation.noteobjects.ScoreTest;
import static org.junit.Assert.*;
import wmnlibnotation.TestHelper;

/**
 *
 * @author Otso Björklund
 */
public class ScoreBuilderTest {

	public ScoreBuilderTest() {
	}

	public static List<PartBuilder> getTestPartBuilders(int partCount, int measureCount) {
		List<PartBuilder> partBuilders = new ArrayList<>();

		for (int p = 1; p <= partCount; ++p) {
			PartBuilder partBuilder = new PartBuilder("Part" + p);
			for (int m = 1; m <= measureCount; ++m) {
				partBuilder.add(TestHelper.getTestMeasureBuilder(m));
			}

			partBuilders.add(partBuilder);
		}

		return partBuilders;
	}

	@Test
	public void testBuildingScore() {
		ScoreBuilder builder = new ScoreBuilder();
		Map<Score.Attribute, String> attributes = ScoreTest.getTestAttributes();
		List<PartBuilder> partBuilders = getTestPartBuilders(5, 5);

		for (Score.Attribute attr : attributes.keySet())
			builder.setAttribute(attr, attributes.get(attr));

		for (PartBuilder partBuilder : partBuilders)
			builder.addPart(partBuilder);

		Score score = builder.build();
		assertEquals(ScoreTest.SCORE_NAME, score.getName());
		assertEquals(ScoreTest.COMPOSER_NAME, score.getAttribute(Score.Attribute.COMPOSER));

		assertEquals(5, score.getPartCount());
	}
}
