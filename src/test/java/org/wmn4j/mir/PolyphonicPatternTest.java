package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolyphonicPatternTest {

	private Map<Integer, List<? extends Durational>> createReferencePatternVoices() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.QUARTER));
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();

		voice2.add(Note.of(Pitch.of(Pitch.Base.E, 0, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.EIGHTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.EIGHTH));

		voices.put(1, voice1);
		voices.put(2, voice2);

		return voices;
	}

	@Test
	void testCreatingPatternWithInvalidContentsPossible() {
		List<Durational> nullContents = null;
		Map<Integer, List<? extends Durational>> nullVoices = null;

		assertThrows(NullPointerException.class, () -> new PolyphonicPattern(nullContents),
				"Did not throw NullPointerException when trying to create polyphonic pattern with null contents");

		assertThrows(NullPointerException.class, () -> new PolyphonicPattern(nullVoices),
				"Did not throw NullPointerException when trying to create polyphonic pattern with null voices");

		assertThrows(IllegalArgumentException.class, () -> new PolyphonicPattern(Collections.emptyMap()),
				"Did not throw IllegalArgumentException when trying to create polyphonic pattern with empty voices");

		List<Note> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.B, 0, 3), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 3), Durations.QUARTER));

		Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		voices.put(1, notes);

		assertThrows(IllegalArgumentException.class, () -> new PolyphonicPattern(voices),
				"Did not throw IllegalArgumentException when trying to create polyphonic pattern with monophonic contents");
	}

	@Test
	void testIsMonophonicReturnsFalseForMultiVoicePattern() {
		final Pattern polyphonicPattern = new PolyphonicPattern(createReferencePatternVoices());
		assertFalse(polyphonicPattern.isMonophonic());
	}

	@Test
	void testIsMonophonicReturnsFalseForPatternWithChords() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice = new ArrayList<>();
		List<Note> chordContents = new ArrayList<>();
		chordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH));
		voice.add(Chord.of(chordContents));
		voice.add(Rest.of(Durations.QUARTER));
		voice.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		voices.put(1, voice);

		final Pattern patternWithChord = new PolyphonicPattern(voices);
		assertFalse(patternWithChord.isMonophonic());
	}

	@Test
	void testGetContentsReturnsAllContentsOfPattern() {
		final Map<Integer, List<? extends Durational>> voiceContents = createReferencePatternVoices();

		final Pattern pattern = new PolyphonicPattern(voiceContents);
		final List<Durational> contents = pattern.getContents();

		assertEquals(6, contents.size());

		List<? extends Durational> expectedContents = voiceContents.values().stream().flatMap(voice -> voice.stream())
				.collect(Collectors.toList());

		expectedContents.forEach(notationElement -> assertTrue(contents.contains(notationElement)));
	}

	@Test
	void testGetNumberOfVoicesReturnsCorrectNumber() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());
		assertEquals(2, pattern.getNumberOfVoices());
	}

	@Test
	void testGetVoiceNumbersReturnsCorrectNumbers() {
		final Map<Integer, List<? extends Durational>> contents = createReferencePatternVoices();
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());

		final List<Integer> voiceNumbers = pattern.getVoiceNumbers();
		assertEquals(contents.keySet().size(), voiceNumbers.size());
		for (Integer voiceNumber : voiceNumbers) {
			assertTrue(contents.containsKey(voiceNumber),
					"Voice number " + voiceNumber + " not expected to be in voice numbers");
		}

		for (Integer expectedVoiceNumber : contents.keySet()) {
			assertTrue(voiceNumbers.contains(expectedVoiceNumber),
					"Expected voice number " + expectedVoiceNumber + " missing from voice numbers");
		}
	}

	@Test
	void testGetVoiceReturnsCorrectContents() {
		final Map<Integer, List<? extends Durational>> contents = createReferencePatternVoices();
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());

		assertEquals(contents.get(1), pattern.getVoice(1));
		assertEquals(contents.get(2), pattern.getVoice(2));
	}

	@Test
	void testEqualsReturnsTrueOnlyForExactEquality() {
		final Pattern pattern1 = new PolyphonicPattern(createReferencePatternVoices());
		final Pattern pattern2 = new PolyphonicPattern(createReferencePatternVoices());

		assertEquals(pattern1, pattern1);
		assertEquals(pattern1, pattern2);

		Map<Integer, List<? extends Durational>> modifiedVoices = createReferencePatternVoices();
		List<Durational> modifiedVoice = new ArrayList<>(modifiedVoices.get(2));
		Note note = Note.of(Pitch.of(Pitch.Base.C, 1, 5), Durations.EIGHTH);
		modifiedVoice.add(note);
		modifiedVoices.put(2, modifiedVoice);

		final Pattern modifiedPattern = new PolyphonicPattern(modifiedVoices);
		assertNotEquals(modifiedPattern, pattern1);

		Map<Integer, List<? extends Durational>> contentsWithAddedVoice = createReferencePatternVoices();
		List<Note> notes = Collections.singletonList(note);
		contentsWithAddedVoice.put(3, notes);
		final Pattern withAddedVoice = new PolyphonicPattern(contentsWithAddedVoice);

		assertNotEquals(withAddedVoice, pattern1);
	}

	@Test
	void testEqualsInPitchReturnsTrueForExactEquality() {
		final Pattern pattern1 = new PolyphonicPattern(createReferencePatternVoices());
		final Pattern pattern2 = new PolyphonicPattern(createReferencePatternVoices());

		assertTrue(pattern1.equalsInPitch(pattern1));
		assertTrue(pattern1.equalsInPitch(pattern2));
	}

	@Test
	void testEqualsInPitchReturnsTrueWhenPatternsHaveSamePitches() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());

		Map<Integer, List<? extends Durational>> modifiedVoices = createReferencePatternVoices();
		List<Durational> modifiedVoice = new ArrayList<>(modifiedVoices.get(1));
		Note note = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.SIXTEENTH);
		modifiedVoice.set(0, note);
		modifiedVoices.put(1, modifiedVoice);

		final Pattern modifiedPattern = new PolyphonicPattern(modifiedVoices);
		assertTrue(modifiedPattern.equalsInPitch(pattern));

		final Map<Integer, List<? extends Durational>> voicesWithDifferentRests = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.EIGHTH));
		voice1.add(Rest.of(Durations.SIXTEENTH));
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();

		voice2.add(Note.of(Pitch.of(Pitch.Base.E, 0, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.SIXTEENTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.EIGHTH));

		voicesWithDifferentRests.put(1, voice1);
		voicesWithDifferentRests.put(2, voice2);

		final Pattern patternWithDifferentRests = new PolyphonicPattern(voicesWithDifferentRests);
		assertTrue(pattern.equalsInPitch(patternWithDifferentRests));
	}

	@Test
	void testEqualsInPitchReturnsFalseForPatternsWithDifferentPitches() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());
		Map<Integer, List<? extends Durational>> modifiedVoices = createReferencePatternVoices();
		List<Durational> modifiedVoice = new ArrayList<>(modifiedVoices.get(2));
		Note note = Note.of(Pitch.of(Pitch.Base.C, 1, 5), Durations.EIGHTH);
		modifiedVoice.set(0, note);
		modifiedVoices.put(2, modifiedVoice);

		final Pattern modifiedPattern = new PolyphonicPattern(modifiedVoices);
		assertFalse(modifiedPattern.equalsInPitch(pattern));

		Map<Integer, List<? extends Durational>> contentsWithAddedVoice = createReferencePatternVoices();
		List<Note> notes = Collections.singletonList(note);
		contentsWithAddedVoice.put(3, notes);
		final Pattern withAddedVoice = new PolyphonicPattern(contentsWithAddedVoice);

		assertFalse(withAddedVoice.equalsInPitch(pattern));
	}

	@Test
	void testEqualsInPitchWithChords() {
		List<Durational> contentsA = new ArrayList<>();
		List<Note> chordContents = new ArrayList<>();
		chordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH));
		contentsA.add(Chord.of(chordContents));
		contentsA.add(Rest.of(Durations.QUARTER));
		contentsA.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		final Pattern patternA = new PolyphonicPattern(contentsA);
		final Pattern patternACopy = new PolyphonicPattern(contentsA);

		assertTrue(patternA.equalsInPitch(patternA));
		assertTrue(patternA.equalsInPitch(patternACopy));

		List<Durational> contentsB = new ArrayList<>(contentsA);

		List<Note> differentChordContents = new ArrayList<>();
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.G, 1, 4), Durations.EIGHTH));
		contentsB.set(0, Chord.of(differentChordContents));

		final Pattern patternB = new PolyphonicPattern(contentsB);

		assertFalse(patternA.equalsInPitch(patternB));
	}

	@Test
	void testEqualsEnharmonicallyReturnsTrueForExactEquality() {
		final Pattern pattern1 = new PolyphonicPattern(createReferencePatternVoices());
		final Pattern pattern2 = new PolyphonicPattern(createReferencePatternVoices());

		assertTrue(pattern1.equalsEnharmonically(pattern1));
		assertTrue(pattern1.equalsEnharmonically(pattern2));
	}

	@Test
	void testEqualsEnharmonicallyReturnsTrueWhenPatternsHaveEnharmonicallySamePitches() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());

		Map<Integer, List<? extends Durational>> modifiedVoices = createReferencePatternVoices();
		List<Durational> modifiedVoice = new ArrayList<>(modifiedVoices.get(1));
		Note note = Note.of(Pitch.of(Pitch.Base.D, -2, 4), Durations.SIXTEENTH);
		modifiedVoice.set(0, note);
		modifiedVoices.put(1, modifiedVoice);

		final Pattern modifiedPattern = new PolyphonicPattern(modifiedVoices);
		assertTrue(modifiedPattern.equalsEnharmonically(pattern));

		final Map<Integer, List<? extends Durational>> voicesWithDifferentRests = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.EIGHTH));
		voice1.add(Rest.of(Durations.SIXTEENTH));
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();

		voice2.add(Note.of(Pitch.of(Pitch.Base.E, 0, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.SIXTEENTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.G, -1, 3), Durations.EIGHTH));

		voicesWithDifferentRests.put(1, voice1);
		voicesWithDifferentRests.put(2, voice2);

		final Pattern patternWithDifferentRests = new PolyphonicPattern(voicesWithDifferentRests);
		assertTrue(pattern.equalsEnharmonically(patternWithDifferentRests));
	}

	@Test
	void testEqualsEnharmonicallyWithChords() {
		List<Durational> contentsA = new ArrayList<>();
		List<Note> chordContents = new ArrayList<>();
		chordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.F, 2, 4), Durations.EIGHTH));
		contentsA.add(Chord.of(chordContents));
		contentsA.add(Rest.of(Durations.QUARTER));
		contentsA.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		final Pattern patternA = new PolyphonicPattern(contentsA);
		final Pattern patternACopy = new PolyphonicPattern(contentsA);

		assertTrue(patternA.equalsEnharmonically(patternA));
		assertTrue(patternA.equalsEnharmonically(patternACopy));

		List<Durational> contentsB = new ArrayList<>(contentsA);

		List<Note> differentChordContents = new ArrayList<>();
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.G, 1, 4), Durations.EIGHTH));
		contentsB.set(0, Chord.of(differentChordContents));

		final Pattern patternB = new PolyphonicPattern(contentsB);

		assertFalse(patternA.equalsEnharmonically(patternB));
	}

	@Test
	void testEqualsTranspositionallyReturnsTrueForExactEquality() {
		final Pattern pattern1 = new PolyphonicPattern(createReferencePatternVoices());
		final Pattern pattern2 = new PolyphonicPattern(createReferencePatternVoices());

		assertTrue(pattern1.equalsTranspositionally(pattern1));
		assertTrue(pattern1.equalsTranspositionally(pattern2));
	}

	@Test
	void testEqualsTranspositionallyReturnsTrueWhenPatternsAreTranspositionallyEqual() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());

		final Map<Integer, List<? extends Durational>> transposedVoicesWithDifferentRests = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.EIGHTH));
		voice1.add(Rest.of(Durations.SIXTEENTH));
		voice1.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();
		voice2.add(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.SIXTEENTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.A, -1, 3), Durations.EIGHTH));

		transposedVoicesWithDifferentRests.put(1, voice1);
		transposedVoicesWithDifferentRests.put(2, voice2);

		final Pattern patternWithDifferentRests = new PolyphonicPattern(transposedVoicesWithDifferentRests);
		assertTrue(pattern.equalsTranspositionally(patternWithDifferentRests));
	}

	@Test
	void testEqualsTranspositionallyReturnsFalseForPatternsThatAreNotTransposiotallyEquals() {
		final Pattern pattern = new PolyphonicPattern(createReferencePatternVoices());
		Map<Integer, List<? extends Durational>> modifiedVoices = createReferencePatternVoices();
		List<Durational> modifiedVoice = new ArrayList<>(modifiedVoices.get(2));
		Note note = Note.of(Pitch.of(Pitch.Base.C, 1, 5), Durations.EIGHTH);
		modifiedVoice.set(0, note);
		modifiedVoices.put(2, modifiedVoice);

		final Pattern modifiedPattern = new PolyphonicPattern(modifiedVoices);
		assertFalse(modifiedPattern.equalsTranspositionally(pattern));

		Map<Integer, List<? extends Durational>> contentsWithAddedVoice = createReferencePatternVoices();
		List<Note> notes = Collections.singletonList(note);
		contentsWithAddedVoice.put(3, notes);
		final Pattern withAddedVoice = new PolyphonicPattern(contentsWithAddedVoice);

		assertFalse(withAddedVoice.equalsTranspositionally(pattern));
	}

	@Test
	void testEqualsTranspositionallyWithChords() {
		List<Durational> contents = new ArrayList<>();
		List<Note> chordContents = new ArrayList<>();
		chordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH));
		contents.add(Chord.of(chordContents));
		contents.add(Rest.of(Durations.QUARTER));
		contents.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		final Pattern patternA = new PolyphonicPattern(contents);
		final Pattern patternACopy = new PolyphonicPattern(contents);

		assertTrue(patternA.equalsTranspositionally(patternA));
		assertTrue(patternA.equalsTranspositionally(patternACopy));

		List<Durational> transposedContents = new ArrayList<>();
		List<Note> transposedChordContents = new ArrayList<>();
		transposedChordContents.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		transposedChordContents.add(Note.of(Pitch.of(Pitch.Base.F, 1, 4), Durations.EIGHTH));
		transposedChordContents.add(Note.of(Pitch.of(Pitch.Base.A, 0, 4), Durations.EIGHTH));
		transposedContents.add(Chord.of(transposedChordContents));
		transposedContents.add(Rest.of(Durations.QUARTER));
		transposedContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));

		final Pattern transposedPatternA = new PolyphonicPattern(transposedContents);
		assertTrue(patternA.equalsTranspositionally(transposedPatternA));

		List<Durational> contentsB = new ArrayList<>(contents);

		List<Note> differentChordContents = new ArrayList<>();
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		differentChordContents.add(Note.of(Pitch.of(Pitch.Base.G, 1, 4), Durations.EIGHTH));
		contentsB.set(0, Chord.of(differentChordContents));

		final Pattern patternB = new PolyphonicPattern(contentsB);

		assertFalse(patternA.equalsTranspositionally(patternB));
	}

}
