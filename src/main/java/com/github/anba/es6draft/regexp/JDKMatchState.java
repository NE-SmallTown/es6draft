/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.regexp;

import java.util.BitSet;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link MatchState} implementation for standard JDK {@link Pattern} regular expressions
 */
final class JDKMatchState implements MatchState, IterableMatchResult {
    private final Matcher matcher;
    private final CharSequence string;
    private final BitSet negativeLAGroups;

    public JDKMatchState(Matcher matcher, CharSequence string, BitSet negativeLAGroups) {
        this.matcher = matcher;
        this.string = string;
        this.negativeLAGroups = negativeLAGroups;
    }

    private boolean isUnicode() {
        return (matcher.pattern().flags() & Pattern.UNICODE_CASE) != 0;
    }

    private int toValidStartIndex(int start) {
        // Don't start matching in middle of a surrogate pair.
        if (start > 0 && Character.isSupplementaryCodePoint(Character.codePointAt(string, start - 1)) && isUnicode()) {
            return start - 1;
        }
        return start;
    }

    @Override
    public String toString() {
        return String.format("%s: [matcher=%s]", getClass().getSimpleName(), matcher);
    }

    @Override
    public Iterator<String> iterator() {
        return new GroupIterator(this, negativeLAGroups);
    }

    @Override
    public MatchResult toMatchResult() {
        MatchResult matchResult = matcher.toMatchResult();
        assert matchResult instanceof Matcher;
        return new JDKMatchState((Matcher) matchResult, string, negativeLAGroups);
    }

    @Override
    public boolean find(int start) {
        int actualStart = toValidStartIndex(start);
        return matcher.find(actualStart);
    }

    @Override
    public boolean matches(int start) {
        int actualStart = toValidStartIndex(start);
        return matcher.region(actualStart, matcher.regionEnd()).lookingAt();
    }

    @Override
    public int start() {
        return matcher.start();
    }

    @Override
    public int start(int group) {
        return matcher.start(group);
    }

    @Override
    public int end() {
        return matcher.end();
    }

    @Override
    public int end(int group) {
        return matcher.end(group);
    }

    @Override
    public String group() {
        return matcher.group();
    }

    @Override
    public String group(int group) {
        return matcher.group(group);
    }

    @Override
    public int groupCount() {
        return matcher.groupCount();
    }
}
