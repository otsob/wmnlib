/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class SingleStaffPartTest {

	final List<Measure> measures;
	final int measureCount = 5;

	KeySignature keySig = KeySignatures.CMAJ_AMIN;

	Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
	Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
	Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
	Note C4Quarter = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);

	public SingleStaffPartTest() {
		Map<Integer, List<Durational>> noteLayer = new HashMap<>();
		noteLayer.put(0, new ArrayList<>());
		noteLayer.get(0).add(C4Quarter);
		noteLayer.get(0).add(Rest.getRest(Durations.QUARTER));
		noteLayer.get(0).add(Chord.getChord(C4, E4, G4));

		Map<Integer, List<Durational>> noteLayers = new HashMap<>();
		noteLayers.put(0, noteLayer.get(0));
		noteLayers.put(1, new ArrayList<>());
		noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
		noteLayers.get(1).add(C4);
		noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));

		List<Measure> measureList = new ArrayList<>();
		for (int i = 1; i <= this.measureCount; ++i)
			measureList.add(new Measure(i, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G));

		this.measures = Collections.unmodifiableList(measureList);
	}

	@Test
	public void testImmutability() {
		List<Measure> measuresCopy = new ArrayList<>(this.measures);
		SingleStaffPart part = new SingleStaffPart("Test part", measuresCopy);
		measuresCopy.set(0, null);
		assertTrue("Modifying list used to create part modified part also.",
				part.getStaff().getMeasures().get(0) != null);
	}

	@Test
	public void testGetName() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		assertEquals("Test part", part.getName());
	}

	@Test
	public void testIsMultiStaff() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		assertFalse(part.isMultiStaff());
	}

	@Test
	public void testGetStaff() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		Staff staff = part.getStaff();
		assertEquals(5, staff.getMeasures().size());
	}

	@Test
	public void getMeasure() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		Measure m = part.getMeasure(1);
		assertTrue(m == this.measures.get(0));
	}

	@Test
	public void testIterator() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		int measCount = 0;
		int measureNumber = 1;
		for (Measure m : part) {
			assertEquals("Iterator went through measures in incorrect order", measureNumber++, m.getNumber());
			++measCount;
		}

		assertEquals("Iterator did not go through all measures/went through measures multiple times.",
				this.measureCount, measCount);
	}

	@Test
	public void testIteratorImmutability() {
		SingleStaffPart part = new SingleStaffPart("Test part", this.measures);
		Iterator<Measure> iter = part.iterator();
		iter.next();

		try {
			iter.remove();
			fail("Removing through iterator did not cause exception");
		} catch (Exception e) {
			/* Ignore */ }

		assertEquals(this.measureCount, part.getStaff().getMeasures().size());
	}

}
