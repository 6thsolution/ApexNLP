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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import backport.java.util.function.Consumer;

import static backport.java.util.function.BackportFuncs.forEach;

/**
 * Simple non-deterministic finite automaton (NFA) representation
 * <p>
 * A set of {@link Matchable} patterns is converted to an NFA as an intermediate step toward
 * creating the DFA.
 * <p>
 * You can also build an NFA directly with this class and convert it to a DFA with {@link
 * DfaBuilder#buildFromNfa(Nfa, int[], DfaAmbiguityResolver, com.nobigsoftware.util.BuilderCache)}.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton">NFA on
 * Wikipedia</a>
 *
 * @param MATCHRESULT The type of result produce by matching a pattern.  This must be serializable
 *                    to support caching of built DFAs
 */
public class Nfa<MATCHRESULT> {
    private final ArrayList<List<NfaTransition>> m_stateTransitions = new ArrayList<>();
    private final ArrayList<List<Integer>> m_stateEpsilons = new ArrayList<>();
    private final ArrayList<MATCHRESULT> m_stateAccepts = new ArrayList<>();

    /**
     * Get the number of states in the NFA
     *
     * @return the total number of states that have been added with {@link #addState(Object)}
     */
    public int numStates() {
        return m_stateAccepts.size();
    }

    /**
     * Add a new state to the NFA
     *
     * @param accept if non-null, then the NFA will produce this result for any string that reaches
     *               the new state
     * @return the number of the new state
     */
    public int addState(MATCHRESULT accept) {
        int ret = m_stateAccepts.size();
        m_stateAccepts.add(accept);
        m_stateTransitions.add(null);
        m_stateEpsilons.add(null);
        assert (m_stateTransitions.size() == m_stateAccepts.size());
        assert (m_stateEpsilons.size() == m_stateAccepts.size());
        return ret;
    }

    /**
     * Add a transition to the NFA
     * <p>
     * A new transition will be created from state <tt>from</tt> to state <tt>to</tt>
     * for all characters with code points in [from,to] (inclusive)
     *
     * @param from      the number of the state to transition from
     * @param to        the number of the state to transition to, for characters in the accepted
     *                  range
     * @param firstChar the first character in the accepted range
     * @param lastChar  the last character in the accepted range
     */
    public void addTransition(int from, int to, char firstChar, char lastChar) {
        List<NfaTransition> list = m_stateTransitions.get(from);
        if (list == null) {
            list = new ArrayList<>();
            m_stateTransitions.set(from, list);
        }
        list.add(new NfaTransition(firstChar, lastChar, to));
    }

    /**
     * Add an epsilon transition to the NFA
     * <p>
     * An epsilon transition is created from state <tt>from</tt> to state <tt>to</tt>.
     * <p>
     * This will cause any string that is accepted by <tt>to</tt> to be accepted by <tt>from</tt> as
     * well
     *
     * @param from the number of the state to transition from
     * @param to   the number of the state to transition to
     */
    public void addEpsilon(int from, int to) {
        List<Integer> list = m_stateEpsilons.get(from);
        if (list == null) {
            list = new ArrayList<Integer>();
            m_stateEpsilons.set(from, list);
        }
        list.add(to);
    }

    /**
     * Get the result attached to the given state
     *
     * @param state the state number
     * @return the result that was provided to {@link #addState(Object)} when the state was created
     */
    public MATCHRESULT getAccept(int state) {
        return m_stateAccepts.get(state);
    }

    /**
     * Check whether a state has any non-epsilon transitions or has a result attached
     *
     * @param state the state number
     * @return true if the state has any transitions or accepts
     */
    public boolean hasTransitionsOrAccepts(int state) {
        return (m_stateAccepts.get(state) != null || m_stateTransitions.get(state) != null);
    }

    /**
     * Get all the epsilon transitions from a state
     *
     * @param state the state number
     * @return An iterable over all transitions out of the given state
     */
    public Iterable<Integer> getStateEpsilons(int state) {
        List<Integer> list = m_stateEpsilons.get(state);
        List<Integer> emptyList = Collections.emptyList();
        return (list != null ? list : emptyList);
    }

    /**
     * Get all the non-epsilon transitions from a state
     *
     * @param state the state number
     * @return An iterable over all transitions out of the given state
     */
    public Iterable<NfaTransition> getStateTransitions(int state) {
        List<NfaTransition> list = m_stateTransitions.get(state);
        List<NfaTransition> emptyList = Collections.emptyList();
        return (list != null ? list : emptyList);
    }

    /**
     * Make modified state, if necessary, that doesn't match the empty string
     * <p>
     * If <tt>state</tt> has a non-null result attached, or can reach such a state through
     * epsilon transitions, then a DFA made from that state would match the empty string.  In
     * that case a new NFA state will be created that matches all the same strings <i>except</i>
     * the empty string.
     *
     * @param state the number of the state to disemptify
     * @return If <tt>state</tt> matches the empty string, then a new state that does not match the
     * empty string is returned.  Otherwise <tt>state</tt> is returned.
     */
    public int Disemptify(final int state) {
        ArrayList<Integer> reachable = new ArrayList<>();

        //first find all epsilon-reachable states
        {
            Set<Integer> checkSet = new HashSet<>();
            reachable.add(state);
            checkSet.add(reachable.get(0)); //same Integer instance
            for (int i = 0; i < reachable.size(); ++i) {
                forStateEpsilons(reachable.get(i), num -> {
                    if (checkSet.add(num)) {
                        reachable.add(num);
                    }
                });
            }
        }

        //if none of them accept, then we're done
        for (int i = 0; ; ++i) {
            if (i >= reachable.size()) {
                return state;
            }
            if (getAccept(reachable.get(i)) != null) {
                break;
            }
        }

        //need to make a new disemptified state.  first get all transitions
        int newState = addState(null);
        Set<NfaTransition> transSet = new HashSet<>();
        for (Integer src : reachable) {
            forStateTransitions(src, trans -> {
                if (transSet.add(trans)) {
                    addTransition(newState, trans.m_stateNum, trans.m_firstChar, trans.m_lastChar);
                }
            });
        }
        return newState;
    }

    void forStateEpsilons(int state, Consumer<Integer> dest) {
        List<Integer> list = m_stateEpsilons.get(state);
        if (list != null) {
            forEach(list, dest);
        }
    }

    void forStateTransitions(int state, Consumer<NfaTransition> dest) {
        List<NfaTransition> list = m_stateTransitions.get(state);
        if (list != null) {
            forEach(list, dest);
        }
    }
}
