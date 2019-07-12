/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Durational;

import java.util.List;

public final class PolyphonicPattern implements Pattern {

	@Override
	public List<Durational> getContents() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#isMonophonic()
	 */
	@Override
	public boolean isMonophonic() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsInPitch(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsEnharmonically(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsEnharmonically(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsTranspositionally(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsTranspositionally(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsInDurations(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInDurations(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}
}
