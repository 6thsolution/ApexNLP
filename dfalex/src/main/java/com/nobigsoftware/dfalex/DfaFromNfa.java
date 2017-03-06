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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static backport.java.util.function.BackportFuncs.computeIfAbsent;


/**
 * Turns an NFA into a non-minimal RawDfa by powerset construction
 */
class DfaFromNfa<RESULT> {
    //inputs
    private final Nfa<RESULT> m_nfa;
    private final int[] m_nfaStartStates;
    private final int[] m_dfaStartStates;
    private final DfaAmbiguityResolver<? super RESULT> m_ambiguityResolver;

    //utility
    private final DfaStateSignatureCodec m_dfaSigCodec = new DfaStateSignatureCodec();

    //These fields are scratch space
    private final IntListKey m_tempStateSignature = new IntListKey();
    private final ArrayDeque<Integer> m_tempNfaClosureList = new ArrayDeque<>();
    private final HashSet<RESULT> m_tempResultSet = new HashSet<RESULT>();

    //accumulators
    private final HashMap<RESULT, Integer> m_acceptSetMap = new HashMap<>();
    private final ArrayList<RESULT> m_acceptSets = new ArrayList<>();

    private final HashMap<IntListKey, Integer> m_dfaStateSignatureMap = new HashMap<>();
    private final ArrayList<IntListKey> m_dfaStateSignatures = new ArrayList<>();
    private final ArrayList<DfaStateInfo> m_dfaStates = new ArrayList<>();

    public DfaFromNfa(Nfa<RESULT> nfa, int[] nfaStartStates,
                      DfaAmbiguityResolver<? super RESULT> ambiguityResolver) {
        m_nfa = nfa;
        m_nfaStartStates = nfaStartStates;
        m_dfaStartStates = new int[nfaStartStates.length];
        m_ambiguityResolver = ambiguityResolver;
        m_acceptSets.add(null);
        _build();
    }

    public RawDfa<RESULT> getDfa() {
        return new RawDfa<>(m_dfaStates, m_acceptSets, m_dfaStartStates);
    }

    private void _build() {

        final CompactIntSubset nfaStateSet = new CompactIntSubset(m_nfa.numStates());
        final ArrayList<NfaTransition> dfaStateTransitions = new ArrayList<>();
        final ArrayList<NfaTransition> transitionQ = new ArrayList<>(1000);

        //Create the DFA start states
        for (int i = 0; i < m_dfaStartStates.length; ++i) {
            nfaStateSet.clear();
            _addNfaStateAndEpsilonsToSubset(nfaStateSet, m_nfaStartStates[i]);
            m_dfaStartStates[i] = _getDfaState(nfaStateSet);
        }

        //Create the transitions and other DFA states.
        //m_dfaStateSignatures grows as we discover new states.
        //m_dfaStates grows as we complete them
        for (int stateNum = 0; stateNum < m_dfaStateSignatures.size(); ++stateNum) {
            final IntListKey dfaStateSig = m_dfaStateSignatures.get(stateNum);

            dfaStateTransitions.clear();

            //For each DFA state, combine the NFA transitions for each
            //distinct character range into a DFA transiton, appending new DFA states
            //as we discover them.
            transitionQ.clear();

            //dump all the NFA transitions for the state into the Q
            DfaStateSignatureCodec.expand(dfaStateSig,
                    state -> m_nfa.forStateTransitions(state, transitionQ::add));

            //sort all the transitions by first character
            Collections.sort(transitionQ, (arg0, arg1) -> {
                if (arg0.m_firstChar != arg1.m_firstChar) {
                    return (arg0.m_firstChar < arg1.m_firstChar ? -1 : 1);
                }
                return 0;
            });

            final int tqlen = transitionQ.size();

            //first character we haven't accounted for yet
            char minc = 0;

            //NFA transitions at index < tqstart are no longer relevant
            //NFA transitions at index >= tqstart are in first char order OR have first char <= minc
            //The sequence of NFA transitions contributing the the previous DFA transition starts here
            int tqstart = 0;

            //make a range of NFA transitions corresponding to the next DFA transition
            while (tqstart < tqlen) {
                NfaTransition trans = transitionQ.get(tqstart);
                if (trans.m_lastChar < minc) {
                    ++tqstart;
                    continue;
                }

                //INVAR - trans contributes to the next DFA transition
                nfaStateSet.clear();
                _addNfaStateAndEpsilonsToSubset(nfaStateSet, trans.m_stateNum);
                char startc = trans.m_firstChar;
                char endc = trans.m_lastChar;
                if (startc < minc) {
                    startc = minc;
                }
                //make range of all transitions that include the start character, removing ones
                //that drop out
                for (int tqend = tqstart + 1; tqend < tqlen; ++tqend) {
                    trans = transitionQ.get(tqend);
                    if (trans.m_lastChar < startc) {
                        //remove this one
                        transitionQ.set(tqend, transitionQ.get(tqstart++));
                        continue;
                    }
                    if (trans.m_firstChar > startc) {
                        //this one is for the next transition
                        if (trans.m_firstChar <= endc) {
                            endc = (char) (trans.m_firstChar - 1);
                        }
                        break;
                    }
                    //this one counts
                    if (trans.m_lastChar < endc) {
                        endc = trans.m_lastChar;
                    }
                    _addNfaStateAndEpsilonsToSubset(nfaStateSet, trans.m_stateNum);
                }

                dfaStateTransitions.add(new NfaTransition(startc, endc, _getDfaState(nfaStateSet)));

                minc = (char) (endc + 1);
                if (minc < endc) {
                    //wrapped around
                    break;
                }
            }

            //INVARIANT: m_dfaStatesOut.size() == stateNum
            m_dfaStates.add(_createStateInfo(dfaStateSig, dfaStateTransitions));
        }

    }

    //Add an NFA state to m_currentNFASubset, along with the transitive
    //closure over its epsilon transitions
    private void _addNfaStateAndEpsilonsToSubset(CompactIntSubset dest, int stateNum) {
        m_tempNfaClosureList.clear();
        if (dest.add(stateNum)) {
            m_tempNfaClosureList.add(stateNum);
        }
        Integer newNfaState;
        while ((newNfaState = m_tempNfaClosureList.poll()) != null) {
            m_nfa.forStateEpsilons(newNfaState, (Integer src) -> {
                if (dest.add(src)) {
                    m_tempNfaClosureList.add(src);
                }
            });
        }
    }

    private void _addNfaStateToSignatureCodec(int stateNum) {
        if (m_nfa.hasTransitionsOrAccepts(stateNum)) {
            m_dfaSigCodec.acceptInt(stateNum);
        }
    }


    //Make a DFA state for a set of simultaneous NFA states
    private Integer _getDfaState(CompactIntSubset nfaStateSet) {
        //dump state combination into compressed form
        m_tempStateSignature.clear();
        m_dfaSigCodec.start(m_tempStateSignature::add, nfaStateSet.getSize(),
                nfaStateSet.getRange());
        nfaStateSet.dumpInOrder(this::_addNfaStateToSignatureCodec);
        m_dfaSigCodec.finish();

        //make sure it's in the map
        Integer dfaStateNum = m_dfaStateSignatureMap.get(m_tempStateSignature);
        if (dfaStateNum == null) {
            dfaStateNum = m_dfaStateSignatures.size();
            IntListKey newSig = new IntListKey(m_tempStateSignature);
            m_dfaStateSignatures.add(newSig);
            m_dfaStateSignatureMap.put(newSig, dfaStateNum);
        }
        return dfaStateNum;
    }

    @SuppressWarnings("unchecked")
    private DfaStateInfo _createStateInfo(IntListKey sig, List<NfaTransition> transitions) {
        //calculate the set of accepts
        m_tempResultSet.clear();
        DfaStateSignatureCodec.expand(sig, nfastate -> {
            RESULT accept = m_nfa.getAccept(nfastate);
            if (accept != null) {
                m_tempResultSet.add(accept);
            }
        });

        //and get an accept set index for it
        RESULT dfaAccept = null;
        if (m_tempResultSet.size() > 1) {
            dfaAccept = (RESULT) m_ambiguityResolver.apply(m_tempResultSet);
        } else if (!m_tempResultSet.isEmpty()) {
            dfaAccept = m_tempResultSet.iterator().next();
        }

        int acceptSetIndex = 0;
        if (dfaAccept != null) {
            acceptSetIndex = computeIfAbsent(m_acceptSetMap, dfaAccept, keyset -> {
                m_acceptSets.add(keyset);
                return m_acceptSets.size() - 1;
            });
        }

        return new DfaStateInfo(transitions, acceptSetIndex);
    }
}
