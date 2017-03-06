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

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import backport.java.util.function.BiConsumer;

/**
 * Utility class to calculate various auxiliary information about DFAs.
 * <P>
 * An instance of this class is created with a set of start states, which all the
 * other methods of this class will calculate information about.
 * <P>
 * Unless otherwise noted, the methods of this class all operated in linear time
 * or better.
 * <P>
 * Use one instance of this class to calculate everything you need.  It will remember
 * results you ask for and reuse them for other calculations when required.
 */
public class DfaAuxiliaryInformation<MATCHRESULT>
{
    private static final Object SENTINEL = new Object();
    private final List<DfaState<MATCHRESULT>> m_startStates;
    private List<DfaState<MATCHRESULT>> m_statesByNumber = null;
    private int[] m_cycleNumbers = null;
    private List<MATCHRESULT> m_destiniesByNumber = null;
    
    /**
     * Create a new DfaAuxiliaryInformation.
     * 
     * @param startStates   A collection of start states returned by a single call to {@link DfaBuilder}.
     *      The states must have been returned by a single call, so that the state numbers of all states
     *      they reach will be unique.  Methods of this class will calculate various information about
     *      these states
     */
    public DfaAuxiliaryInformation(Collection<DfaState<MATCHRESULT>> startStates)
    {
        m_startStates = new ArrayList<>(startStates.size());
        m_startStates.addAll(startStates);
    }
    
    /**
     * Get a list of all states reachable from the start states.
     * <P>
     * Multiple calls to this method will return the same list.
     * 
     * @return a list that contains every state reachable from the start states, with
     *      the index of each state s equal to s.getStateNumber().  Unused indexes will
     *      have null values.
     */
    public synchronized List<DfaState<MATCHRESULT>> getStatesByNumber()
    {
        if (m_statesByNumber == null)
        {
            List<DfaState<MATCHRESULT>> statesByNumber = new ArrayList<>();
            ArrayDeque<DfaState<MATCHRESULT>> q = new ArrayDeque<>();
            for (DfaState<MATCHRESULT> state : m_startStates)
            {
                if (state != null)
                {
                    int i = state.getStateNumber();
                    while(statesByNumber.size()<=i)
                    {
                        statesByNumber.add(null);
                    }
                    if (statesByNumber.get(i)==null)
                    {
                        statesByNumber.set(i, state);
                        q.add(state);
                    }
                }
            }
            while (!q.isEmpty())
            {
                DfaState<MATCHRESULT> state = q.removeFirst();
                state.enumerateTransitions((in, out, target) -> {
                    int i = target.getStateNumber();
                    while(statesByNumber.size()<=i)
                    {
                        statesByNumber.add(null);
                    }
                    if (statesByNumber.get(i)==null)
                    {
                        statesByNumber.set(i, target);
                        q.add(target);
                    }
                });
            }
            m_statesByNumber = statesByNumber;
        }
        return m_statesByNumber;
    }
    
    /**
     * Perform a depth first search of all states, starting at the start states
     * <P>
     * To avoid stack overflow errors on large DFAs, the implementation uses an auxiliary
     * stack on the heap instead of recursing
     * 
     * @param onEnter  called with (parent, child) when a child is entered.  parent == null for roots.
     * @param onSkip  called with (parent, child) when a child is skipped because it has been entered
     *                  previously.  exited.  parent == null for roots.
     * @param onLeave  called with (parent, child) when a child is exited.  parent == null for roots.
     */
    public void depthFirstSearch(
            BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onEnter,
            BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onSkip,
            BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onLeave)
    {
        @SuppressWarnings("unchecked")
        final Iterator<DfaState<MATCHRESULT>>[] iterators = 
            (Iterator<DfaState<MATCHRESULT>>[]) new Iterator<?>[getStatesByNumber().size()];
        final ArrayDeque<DfaState<MATCHRESULT>> stack = new ArrayDeque<>();
        for (int rootIndex = 0; rootIndex < m_startStates.size(); ++rootIndex)
        {
            DfaState<MATCHRESULT> st = m_startStates.get(rootIndex);
            if (iterators[st.getStateNumber()] != null)
            {
                onSkip.accept(null, st);
                continue;
            }
            iterators[st.getStateNumber()] = st.getSuccessorStates().iterator();
            stack.push(st);
            onEnter.accept(null, st);
            for (;;)
            {
                //process the next child of the stack top
                st = stack.peek();
                final int sti = st.getStateNumber();
                final Iterator<DfaState<MATCHRESULT>> iter = iterators[sti];
                if (iter.hasNext())
                {
                    final DfaState<MATCHRESULT> child = iter.next();
                    if (child == null)
                    {
                        //shouldn't happen, but if it does get the next child
                        continue;
                    }
                    final int childi = child.getStateNumber();
                    if (iterators[childi] != null)
                    {
                        onSkip.accept(st, child);
                    }
                    else
                    {
                        iterators[childi] = child.getSuccessorStates().iterator();
                        stack.push(child);
                        onEnter.accept(st, child);
                    }
                }
                else
                {
                    //top element is done
                    stack.pop();
                    if (stack.isEmpty())
                    {
                        onLeave.accept(null, st);
                        break;
                    }
                    onLeave.accept(stack.peek(), st);
                }
            }
        }
    }
    
    
    /**
     * Get an array that maps each state number to the state's 'cycle number', such that:
     * <UL><LI>States that are not in a cycle have cycle number -1
     * </LI><LI>States that are in a cycle have cycle number &gt;= 0
     * </LI><LI>States in cycles have the same cycle number IFF they are in the same cycle
     *      (i.e., they are reachable from each other)
     * </LI><LI>Cycles are compactly numbered from 0
     * </LI></UL>
     * Note that states with cycle numbers &gt;=0 match an infinite number of different strings, while
     * states with cycle number -1 match a finite number of strings with lengths &lt;= the size
     * of this array.
     * 
     * @return  the cycle numbers array
     */
    public synchronized int[] getCycleNumbers()
    {
        if (m_cycleNumbers != null)
        {
            return m_cycleNumbers;
        }
        //Tarjan's algorithm
        final int[] pindex = new int[]{0};
        final int[] pcycle = new int[]{0};
        final ArrayDeque<DfaState<MATCHRESULT>> stack = new ArrayDeque<>();
        final int[] orderIndex = new int[getStatesByNumber().size()];
        final int[] backLink = new int[orderIndex.length];
        final int[] cycleNumbers = new int[orderIndex.length];
        for (int i = 0; i<orderIndex.length; ++i)
        {
            orderIndex[i] = -1;
            backLink[i] = -1; //not on stack
            cycleNumbers[i] = -1; //no cycle
        }
        BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onEnter = (parent, child) ->
        {
            stack.push(child);
            backLink[child.getStateNumber()] = orderIndex[child.getStateNumber()] = pindex[0]++;
        };
        BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onSkip = (parent, child) ->
        {
            int childLink = backLink[child.getStateNumber()];
            if (parent != null && childLink >= 0 && childLink < backLink[parent.getStateNumber()])
            {
                backLink[parent.getStateNumber()] = childLink;
            }
        };
        BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onExit = (parent, child) ->
        {
            final int childi = child.getStateNumber();
            final int childLink = backLink[childi]; 
            if (childLink == orderIndex[childi])
            {
                //child is a cycle root
                int cycleNum = -1;
                if (stack.peek()!=child)
                {
                    cycleNum = pcycle[0]++;
                }
                for (;;)
                {
                    DfaState<MATCHRESULT> st = stack.pop();
                    int sti = st.getStateNumber();
                    cycleNumbers[sti]=cycleNum;
                    backLink[sti]=-1;
                    if (st == child)
                    {
                        break;
                    }
                }
            }
            if (parent != null && childLink>=0 && childLink < backLink[parent.getStateNumber()])
            {
                backLink[parent.getStateNumber()] = childLink;
            }
        };
        
        m_cycleNumbers = cycleNumbers;
        depthFirstSearch(onEnter, onSkip, onExit);
        return cycleNumbers;
    }
    
    
    /**
     * Get a list that maps each state number to the state's "destiny"
     * <P>
     * If all strings accepted by the state produce the same MATCHRESULT,
     * then that MATCHRESULT is the state's destiny.  Otherwise the state's
     * destiny is null.
     * 
     * @return  The list of destinies by state number
     */
    public synchronized List<MATCHRESULT> getDestinies()
    {
        if (m_destiniesByNumber != null)
        {
            return m_destiniesByNumber;
        }
        getCycleNumbers();
        int numCycles = 0;
        for (int i=0;i<m_cycleNumbers.length;++i)
        {
            if (m_cycleNumbers[i]>=numCycles)
            {
                numCycles = m_cycleNumbers[i]+1;
            }
        }
        final Object[] destinies = new Object[getStatesByNumber().size()];
        final Object[] cycleDestinies = new Object[numCycles];
        BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onEnter = (parent, child) ->
        {
            int childi = child.getStateNumber();
            int cycle = m_cycleNumbers[childi];
            if (cycle >= 0)
            {
                cycleDestinies[cycle]=_destinyMerge(cycleDestinies[cycle],child.getMatch());
            }
            else
            {
                destinies[childi] = child.getMatch();
            }
        };
        BiConsumer<DfaState<MATCHRESULT>, DfaState<MATCHRESULT>> onMerge = (parent, child) ->
        {
            if (parent != null)
            {
                int childi = child.getStateNumber();
                int pari = parent.getStateNumber();
                int cycle = m_cycleNumbers[childi];
                Object o = (cycle >= 0 ? cycleDestinies[cycle] : destinies[childi]);
                cycle = m_cycleNumbers[pari];
                if (cycle>=0)
                {
                    cycleDestinies[cycle] = _destinyMerge(cycleDestinies[cycle],o);
                }
                else
                {
                    destinies[pari] = _destinyMerge(destinies[pari],o);
                }
            }
        };
        depthFirstSearch(onEnter, onMerge, onMerge);
  
        for (int i=0; i<destinies.length; ++i)
        {
            int cycleNum = m_cycleNumbers[i];
            Object o = (cycleNum >= 0 ? cycleDestinies[cycleNum] : destinies[i]);
            destinies[i] = (o == SENTINEL ? null : o);
        }
        m_destiniesByNumber = new ListWrap<>(destinies);
        return m_destiniesByNumber;
    }
    
    
    private static Object _destinyMerge(Object a, Object b)
    {
        if (b==null)
        {
            return a;
        }
        if (a==null)
        {
            return b;
        }
        if (a==SENTINEL || b==SENTINEL)
        {
            return SENTINEL;
        }
        return (a.equals(b) ? a : SENTINEL);
    }
    
    private static class ListWrap<T> extends AbstractList<T>
    {
        private final Object[] m_array;

        ListWrap(Object[] array)
        {
            m_array = array;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T get(int index)
        {
            return (T)m_array[index];
        }

        @Override
        public int size()
        {
            return m_array.length;
        }
        
    }
}
