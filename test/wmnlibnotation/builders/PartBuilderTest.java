/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.noteobjects.MeasureAttributes;
import wmnlibnotation.noteobjects.MultiStaffPart;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.SingleStaffPart;
import wmnlibnotation.noteobjects.Staff;
import wmnlibnotation.noteobjects.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class PartBuilderTest {

	private final Map<Integer, List<DurationalBuilder>> measureContents;
	private final MeasureAttributes measureAttr;

	KeySignature keySig = KeySignatures.CMAJ_AMIN;

	NoteBuilder C4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
	NoteBuilder E4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
	NoteBuilder G4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
	NoteBuilder C4Quarter = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);

	public PartBuilderTest() {
		Map<Integer, List<DurationalBuilder>> noteLayer = new HashMap<>();
		noteLayer.put(0, new ArrayList<>());
		noteLayer.get(0).add(C4Quarter);
		noteLayer.get(0).add(new RestBuilder(Durations.QUARTER));
		ChordBuilder chordBuilder = new ChordBuilder(C4);
		chordBuilder.add(E4).add(G4);

		noteLayer.get(0).add(chordBuilder);

		Map<Integer, List<DurationalBuilder>> noteLayers = new HashMap<>();
		noteLayers.put(0, noteLayer.get(0));
		noteLayers.put(1, new ArrayList<>());
		noteLayers.get(1).add(new RestBuilder(Durations.QUARTER));
		noteLayers.get(1).add(C4);
		noteLayers.get(1).add(new RestBuilder(Durations.QUARTER));

		this.measureContents = Collections.unmodifiableMap(noteLayers);
		this.measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Clefs.G);
	}

	private MeasureBuilder getMeasureBuilder(int number) {
		MeasureBuilder builder = new MeasureBuilder(number, this.measureAttr);
		for (Integer layerNum : this.measureContents.keySet()) {
			builder.addLayer(this.measureContents.get(layerNum));
		}

		return builder;
	}

	@Test
	public void testGetStaffCount() {
		int measureCount = 5;
		PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			MeasureBuilder m = getMeasureBuilder(i);
			builder.addToStaff(0, m);
		}
		assertEquals(1, builder.getStaffCount());

		for (int i = 1; i <= measureCount; ++i) {
			MeasureBuilder m = getMeasureBuilder(i);
			builder.addToStaff(1, m);
		}
		assertEquals(2, builder.getStaffCount());
	}

	@Test
	public void testBuildSingleStaffPart() {
		int measureCount = 5;
		PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			MeasureBuilder m = getMeasureBuilder(i);
			builder.add(m);
		}

		Part part = builder.build();
		assertTrue(part instanceof SingleStaffPart);
		assertFalse(part.isMultiStaff());
	}

	@Test
	public void testBuildMultiStaffPart() {
		int measureCount = 5;
		PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			builder.addToStaff(1, getMeasureBuilder(i));
			builder.addToStaff(2, getMeasureBuilder(i));
		}

		Part part = builder.build();
		assertTrue(part.isMultiStaff());
		assertTrue(part instanceof MultiStaffPart);
		MultiStaffPart mpart = (MultiStaffPart) part;
		List<Integer> staffNumbers = mpart.getStaffNumbers();
		assertTrue(staffNumbers.size() == 2);
		assertTrue(staffNumbers.contains(1));
		assertTrue(staffNumbers.contains(2));

		Staff staff1 = mpart.getStaff(1);
		assertTrue(staff1.getMeasureCount() == 5);

		Staff staff2 = mpart.getStaff(2);
		assertTrue(staff2.getMeasureCount() == 5);
	}

	@Test
	public void testBuildPartWithTieBetweenMeasures() {

		MeasureBuilder firstMeasureBuilder = new MeasureBuilder(1);
		NoteBuilder firstNoteBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE);
		firstMeasureBuilder.addToLayer(1, firstNoteBuilder);

		MeasureBuilder secondMeasureBuilder = new MeasureBuilder(2);
		NoteBuilder secondNoteBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE);
		firstNoteBuilder.addTieToFollowing(secondNoteBuilder);
		secondMeasureBuilder.addToLayer(1, secondNoteBuilder);

		PartBuilder partBuilder = new PartBuilder("TiedMeasures");
		partBuilder.add(firstMeasureBuilder).add(secondMeasureBuilder);
		Part part = partBuilder.build();

		Note firstNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 1).get(1, 0);
		Note secondNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 2).get(1, 0);

		assertEquals(secondNote, firstNote.getFollowingTiedNote());
		assertTrue(secondNote.isTiedFromPrevious());
	}
}
