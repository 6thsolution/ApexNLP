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

/**
 * A state in a char-matching deterministic finite automaton (that's the google phrase) or DFA
 * 
 * @param MATCHRESULT the type of result produced by matching patterns with this DFA
 */
public abstract class DfaState<MATCHRESULT>
{
	/**
	 * Process a character and get the next state
	 * 
	 * @param c input character
	 * @return The DfaState that c transitions to from this one, or null if there is no such state
	 */
	public abstract DfaState<MATCHRESULT> getNextState(char c);
	
	/**
	 * Get the result that has been matched if we've transitioned into this state
	 * 
	 * @return If the sequence of characters that led to this state match a pattern in the
	 *     language being processed, the match result for that pattern is returned.  Otherwise
	 *     null.
	 */
	public abstract MATCHRESULT getMatch();
	
	
	/**
     * Get the state number.  All states reachable from the output of a single call to
     * a {@link DfaBuilder} build method will be compactly numbered starting at 0.
     * <P>
     * These state numbers can be used to maintain auxiliary information about a DFA.
     * <P>
     * See {@link DfaAuxiliaryInformation}
     * 
     * @return this state's state number
     */
    public abstract int getStateNumber();
    
    /**
     * Enumerate all the transitions out of this state
     * 
     * @param consumer each DFA transition will be sent here
     */
    public abstract void enumerateTransitions(DfaTransitionConsumer<MATCHRESULT> consumer);

    /**
     * Get an {@link Iterable} of all the successor states of this state.
     * <P>
     * Note that the same successor state may appear more than once in the interation
     * 
     * @return an iterable of successor states.
     */
    public abstract Iterable<DfaState<MATCHRESULT>> getSuccessorStates();
}
