/*
 * Copyright 2015 Matthew Timmermans
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nobigsoftware.dfalex;

import java.io.Serializable;

/**
 * Base interface for the types of patterns that can be used with {@link DfaBuilder} to specify a set of strings to match.
 * <P>
 * The primary implementation classes are {@link Pattern} and {@link CharRange}.
 */
public interface Matchable extends Serializable
{
    /**
     * Add states to an NFA to match the desired pattern
     * <P>
     * New states will be created in the NFA to match the pattern and transition to
     * the given targetState.
     * <P>
     * NO NEW TRANSITIONS will be added to the target state or any other pre-existing state
     * 
     * @param nfa   nfa to add to
     * @param targetState target state after the pattern is matched
     * @return a state that transitions to targetState after matching the pattern, and
     *      only after matching the pattern.  This may be targetState if the pattern is an
     *      empty string.
     */
    public int addToNFA(Nfa<?> nfa, int targetState);
    
    /**
     * @return true if this pattern matches the empty string
     */
    public boolean matchesEmpty();

    /**
     * @return true if this pattern matches any non-empty strings
     */
    public boolean matchesNonEmpty();
    
    /**
     * @return true if this pattern matches can match anything at all
     */
    public boolean matchesSomething();

    /**
     * @return true if this pattern matches an infinite number of strings
     */
    public boolean isUnbounded();
    
    /**
     * Get the reverse of this pattern
     * <P>
     * The reverse of a pattern matches the reverse of all the strings that this pattern matches
     * 
     * @return the reverse of this pattern
     */
    public Matchable getReversed();
}
