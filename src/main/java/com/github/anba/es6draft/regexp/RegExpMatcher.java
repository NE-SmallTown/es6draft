/**
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.regexp;

import java.util.BitSet;

/**
 * Common interface for regular expression matcher objects
 */
public interface RegExpMatcher {
    /**
     * Returns a {@link MatchState} object for {@code input}.
     * 
     * @param input
     *            the input string to match against
     * @return the match state
     */
    MatchState matcher(String input);

    /**
     * Returns an integer to boolean map.
     * 
     * @return the bitset describing negative lookahead groups
     */
    BitSet getNegativeLookaheadGroups();
}
