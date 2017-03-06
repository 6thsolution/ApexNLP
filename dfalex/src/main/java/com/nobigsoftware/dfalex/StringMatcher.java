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
 * This class implements fast matching in a string using DFAs
 * <P>
 * Substrings matching patterns are discoverd with the {@link #findNext(DfaState)} and
 * {@link #matchAt(DfaState, int)} methods, both of which take a DFA start state for the
 * patterns to find.
 * <P>
 * NOTE that you don't have to pass the same state every time -- different calls with the
 * same matcher can search for different patterns and return different kinds of results.
 * <P>
 * 3 pointers are maintained in the string:
 * <UL><LI>
 *  The LastMatchStart position is the position in the source string of the start of the
 *  last successful match, or if no match has been performed yet.
 * </LI><LI>
 *  The LastMatchEnd position is the position in the source string of the end of the last
 *  successful match, or 0 of no match has been performed yet
 * </LI><LI>
 *  The SearchLimit is highest position to search.  This is initially set to the source string
 *  length.  No characters at positions &gt;= SearchLimit will be included in matches
 * </LI></UL>
 */
public class StringMatcher
{
    private static final int NMM_SIZE = 40;
    private final String m_src;
    private int m_lastMatchStart = 0;
    private int m_lastMatchEnd = 0;
    private int m_limit;
    
    //non-matching memo
    //For all x >= m_nmmStart, whenever you're in m_nmmState[x] at position m_nmmPositions[x],
    //you will fail to find a match 
    private int m_nmmStart = NMM_SIZE;
    private final int[] m_nmmPositions = new int[NMM_SIZE];
    private final DfaState<?>[] m_nmmStates = (DfaState<?>[]) new DfaState[NMM_SIZE];
    
    /**
     * Create a new StringMatcher.
     * <P>
     * The LastMatchStart and LastMatchEnd positions are initialized to zero
     * 
     * @param src the source string to be searched
     */
    public StringMatcher(String src)
    {
        m_src = src;
        m_limit = m_src.length();
    }
    
    /**
     * Set the LastMatchStart, LastMatchEnd, and SearchLimit positions explicitly.
     * 
     * @param lastMatchStart  the new lastMatchStartPosition  
     * @param lastMatchEnd the new lastMatchEnd position
     * @param searchLimit the new searchLimit.  This will be limited to the source
     *  string length, so you can pass Integer.MAX_VALUE to set it to the string length
     *  explicitly.
     *  @throws IndexOutOfBoundsException if (lastMatchStart &lt; 0 || lastMatchEnd &lt; lastMatchStart || searchLimit &lt; lastMatchEnd)
     */
    public void setPositions(int lastMatchStart, int lastMatchEnd, int searchLimit)
    {
        searchLimit = Math.min(searchLimit, m_src.length());
        if (lastMatchStart < 0 || lastMatchEnd < lastMatchStart || searchLimit < lastMatchEnd)
        {
            throw new IndexOutOfBoundsException("Invalid positions in StringMatcher.setPositions");
        }
        m_lastMatchStart = lastMatchStart;
        m_lastMatchEnd = lastMatchEnd;
        m_limit = searchLimit;
        m_nmmStart = NMM_SIZE;
    }
    
    /**
     * Resets the matcher to its initial state
     * <P>
     * This is equivalent to setPositions(0,0,Integer.MAX_VALUE);
     */
    public void reset()
    {
        setPositions(0,0,Integer.MAX_VALUE);
    }
    
    /**
     * Get the start position of the last successful match, or 0 if there isn't one
     * 
     * @return the current LastMatchStart position
     */
    public int getLastMatchStart()
    {
        return m_lastMatchStart;
    }

    /**
     * Get the end position of the last successful match, or 0 if there isn't one
     * 
     * @return the current LastMatchEnd position
     */
    public int getLastMatchEnd()
    {
        return m_lastMatchEnd;
    }
    
    /**
     * Get the last successful matching substring, or "" if there isn't one.
     * 
     * @return  The last successful matching string or empty.
     */
    public String getLastMatch()
    {
        if (m_lastMatchEnd <= m_lastMatchStart)
        {
            return "";
        }
        return m_src.substring(m_lastMatchStart, m_lastMatchEnd);
    }

    /**
     * Find the next non-empty match
     * <P>
     * The string is searched from getLastMatchEnd() to the search limit to find a substring that
     * matches a pattern in the given DFA.
     * <P>
     * If there is a match, then the LastMatchStart and LastMatchEnd positions are set to the
     * start and end of the first match, and the MATCHRESULT that the DFA produces for that
     * match is returned.
     * <P>
     * If there is more than one match starting at the same position, the longest one is selected.
     * 
     * @param <MATCHRESULT> the type of results produced by the DFA  
     * @param state The start state of the DFA for the patterns you want to find
     * @return The MATCHRESULT for the next non-empty match in the string, or null if there isn't one
     */
    public <MATCHRESULT> MATCHRESULT findNext(DfaState<MATCHRESULT> state)
    {
        for (int pos = m_lastMatchEnd; pos < m_limit; ++pos)
        {
            MATCHRESULT ret=matchAt(state, pos);
            if (ret!=null)
            {
                return ret;
            }
        }
        return null;
    }
    
    /**
     * Find the longest match starting at a given position.
     * <P>
     * If there is a non-empty match for the DFA in the source string starting at
     * startPos, then the LastMatchStart position is set to startPos, the
     * LastMatchEnd position is set to the end of the longest such match, and 
     * the MATCHRESULT from that match is returned.
     * 
     * @param <MATCHRESULT> the type of results produced by the DFA  
     * @param state The start state of the DFA for the patterns you want to match
     * @param startPos the position in the source string to test for a match
     * @return If the source string matches a pattern in the DFA at startPos, the MATCHRESULT that
     *      the pattern match produces.  Otherwise null.
     */
    public <MATCHRESULT> MATCHRESULT matchAt(DfaState<MATCHRESULT> state, final int startPos)
    {
        MATCHRESULT ret = null; 
        int newNmmSize = 0;
        int writeNmmNext = startPos + 4;

        POSLOOP:
        for(int pos = startPos; pos < m_limit ;)
        {
            state = state.getNextState(m_src.charAt(pos));
            pos++;
            if (state == null)
            {
                break;
            }
            MATCHRESULT match = state.getMatch();
            if (match != null)
            {
                ret = match;
                m_lastMatchEnd = pos;
                newNmmSize = 0;
                continue;
            }
            
            //Check and update the non-matching memo, to accelerate processing long sequences
            //of non-accepting states at multiple positions
            //Many DFAs simply don't have long sequences of non-accepting states, so we only
            //want to incur this overhead when we're actually in a non-accepting state
            while (m_nmmStart < NMM_SIZE && m_nmmPositions[m_nmmStart] <= pos)
            {
                if (m_nmmPositions[m_nmmStart] == pos && m_nmmStates[m_nmmStart] == state)
                {
                    //hit the memo -- we won't find a match.
                    break POSLOOP;
                }
                //we passed this memo entry without using it -- remove it.
                ++m_nmmStart;
            }
            if (pos >= writeNmmNext && newNmmSize < NMM_SIZE)
            {
                m_nmmPositions[newNmmSize] = pos;
                m_nmmStates[newNmmSize] = state;
                ++newNmmSize;
                writeNmmNext = pos+(2<<newNmmSize);
                if (m_nmmStart < newNmmSize)
                {
                    m_nmmStart = newNmmSize;
                }
            }
        }
        //successful or not, we're done.  Merge in our new entries for the non-matching memo
        while (m_nmmStart < NMM_SIZE && m_nmmPositions[m_nmmStart] < writeNmmNext)
        {
            ++m_nmmStart;
        }
        while(newNmmSize > 0)
        {
            --newNmmSize;
            --m_nmmStart;
            m_nmmPositions[m_nmmStart] = m_nmmPositions[newNmmSize]; 
            m_nmmStates[m_nmmStart] = m_nmmStates[newNmmSize]; 
        }
        if (ret != null)
        {
            m_lastMatchStart = startPos;
        }
        return ret;
    }
    
    /**
     * See if a whole string matches a DFA
     * 
     * @param <MATCHRESULT> the type of results produced by the DFA  
     * @param state  DFA start state
     * @param str string to test
     * @return If the whole string matches the DFA, this is the match result produced.  Otherwise null.
     */
    static public <MATCHRESULT> MATCHRESULT matchWholeString(DfaState<MATCHRESULT> state, String str)
    {
        final int len = str.length();
        for (int i=0; i<len; i++)
        {
            if (state == null)
            {
                return null;
            }
            state = state.getNextState(str.charAt(i));
        }
        return (state == null ? null : state.getMatch());
    }
}
