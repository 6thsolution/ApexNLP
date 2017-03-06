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

import java.util.NoSuchElementException;

/**
 * Performs fast searches of a whole string for patterns.  When you need to search the
 * entire string for the same set of patterns, this class is faster than
 * {@link StringMatcher}.
 * <P>
 * NOTE: Instances of this class are thread-safe.
 * 
 * @param MATCHRESULT The type of result associated with the patterns being searched for
 */
public class StringSearcher<MATCHRESULT>
{
    private static final StringMatchIterator<?> NO_MATCHES = new NoMatchIterator();
    private final DfaState<MATCHRESULT> m_matcher;
    private final DfaState<?> m_reverseFinder;
    
    /**
     * Create a new StringSearcher.
     * 
     * @param matcher  A DFA that matches the patterns being searched for
     * @param reverseFinder A DFA that can be applied to a string backwards to
     *      find all the places where matches start.  See {@link DfaBuilder#buildReverseFinder()}
     */
    public StringSearcher(DfaState<MATCHRESULT> matcher,
            DfaState<?> reverseFinder)
    {
        m_matcher = matcher;
        m_reverseFinder = reverseFinder;
    }
    
    /**
     * Search the string for all occurrences of the patterns that this searcher finds
     * 
     * @param src   String to search
     * @return  a {@link StringMatchIterator} that returns all (non-overlapping) matches
     */
    @SuppressWarnings("unchecked")
    public StringMatchIterator<MATCHRESULT> searchString(String src)
    {
        int pos=src.length();
        DfaState<?> finderState = m_reverseFinder;
        if (finderState == null)
        {
            return (StringMatchIterator<MATCHRESULT>)NO_MATCHES;
        }
        //see if the string has at least one match.  If there are
        //no matches, then we don't have to allocate anything
        for (;;)
        {
            if (pos<=0)
            {
                return (StringMatchIterator<MATCHRESULT>)NO_MATCHES;
            }
            --pos;
            finderState = finderState.getNextState(src.charAt(pos));
            if (finderState == null)
            {
                return (StringMatchIterator<MATCHRESULT>)NO_MATCHES;
            }
            if (finderState.getMatch() != null)
            {
                break;
            }
        }
        //found at least one (the last) match
        //make a bit mask of matching positions, starting at the end
        int[] maskArray = new int[8];
        maskArray[maskArray.length-1] = 1<<31;
        int maskStartPos = pos-(maskArray.length*32-1);
        while(pos > 0)
        {
            --pos;
            finderState = finderState.getNextState(src.charAt(pos));
            if (finderState == null)
            {
                break;
            }
            if (finderState.getMatch() != null)
            {
                if (pos < maskStartPos)
                {
                    //need a longer array
                    int toadd = Math.max(maskStartPos-pos, maskArray.length<<5);
                    toadd = (toadd|31)>>5; //bits to ints
                    int[] newMask = new int[maskArray.length + toadd];
                    for (int i=0;i<maskArray.length;++i)
                    {
                        newMask[i+toadd] = maskArray[i];
                    }
                    maskArray = newMask;
                    maskStartPos -= toadd<<5;
                    assert(maskStartPos<=pos);
                }
                int offset = pos-maskStartPos;
                maskArray[offset>>>5] |= 1<<(offset&31);
            }
        }
        return new IteratorImpl<>(src, m_matcher, maskArray, maskStartPos);
    }

    /**
     * Replace all occurrences of patterns in a string
     * <P>
     * The string is searched for all (non-overlapping) occurrences of patterns in this searcher,
     * for each occurrence, the provided replacer is called to supply a replacement value for
     * that part of the string.  If it returns null, that part of the string remains unchanged.
     * If it returns a String, then the pattern occurrence will be replaced with the string returned.
     * 
     * @param src  the String to search   
     * @param replacer  the {@link ReplacementSelector} that provides new values for matches in the string
     * @return the new string with values replaced
     */
    public String findAndReplace(String src, ReplacementSelector<? super MATCHRESULT> replacer)
    {
        StringMatchIterator<MATCHRESULT> it = searchString(src);
        StringReplaceAppendable dest=null;
        int doneTo=0;
        while(it.hasNext())
        {
            MATCHRESULT mr = it.next();
            int s = it.matchStartPosition();
            int e = it.matchEndPosition();
            if (dest == null)
            {
                dest = new StringReplaceAppendable(src);
            }
            if (doneTo < s)
            {
                dest.append(src, doneTo, s);
            }
            doneTo = replacer.apply(dest, mr, src, s, e);
            if (doneTo <= 0)
            {
                doneTo = e;
            }
            else
            {
                if (doneTo <= s)
                {
                    throw new IndexOutOfBoundsException("Replacer tried to rescan matched string");
                }
                it.reposition(doneTo);
            }
        }
        if (dest != null)
        {
            if (doneTo < src.length())
            {
                dest.append(src, doneTo, src.length());
            }
            return dest.toString();
        }
        else
        {
            return src;
        }
    }


    private static class IteratorImpl<MR> implements StringMatchIterator<MR>
    {
        private final String m_src;
        private final DfaState<MR> m_matcher;
        private final int [] m_matchMask;
        private final int m_matchMaskPos;
        private DfaState<MR> m_nextEndState;
        private int m_nextScanStart; //where we started looking for m_next*
        private int m_nextPos;
        private int m_nextEnd;
        private int m_prevPos;
        private int m_prevEnd;
        private MR m_prevResult;
        private String m_prevString;
        
        /**
         * Create a new IteratorImpl.
         * @param src
         * @param matcher
         */
        IteratorImpl(String src, DfaState<MR> matcher, int[] matchMask, int matchMaskPos)
        {
            m_src = src;
            m_matcher = matcher;
            m_matchMask = matchMask;
            m_matchMaskPos = matchMaskPos;
            m_nextScanStart = 0;
            if (!_scanForNext(0, m_src.length()))
            {
                m_nextEndState = null;
                m_nextPos = m_nextEnd = m_src.length();
            }
        }

        @Override
        public boolean hasNext()
        {
            return (m_nextEndState != null);
        }

        @Override
        public MR next()
        {
            if (m_nextEndState == null)
            {
                throw new NoSuchElementException();
            }
            m_prevPos = m_nextPos;
            m_prevEnd = m_nextEnd;
            m_prevResult = m_nextEndState.getMatch();
            m_prevString = null;
            //extend the previously found match as far as possible
            DfaState<MR> st = m_nextEndState;
            final int len = m_src.length();
            for (int pos = m_nextEnd; pos < len; pos++)
            {
                st = st.getNextState(m_src.charAt(pos));
                if (st == null)
                {
                    break;
                }
                MR match = st.getMatch();
                if (match != null)
                {
                    m_prevResult = match;
                    m_prevEnd = pos+1;
                }
            }
            m_nextScanStart = m_prevEnd;
            if (!_scanForNext(m_prevEnd,len))
            {
                m_nextEndState = null;
                m_nextPos = m_nextEnd = len;
            }
            return m_prevResult;
        }

        @Override
        public int matchStartPosition()
        {
            if (m_prevResult == null)
            {
                throw new IllegalStateException();
            }
            return m_prevPos;
        }

        @Override
        public int matchEndPosition()
        {
            if (m_prevResult == null)
            {
                throw new IllegalStateException();
            }
            return m_prevEnd;
        }

        @Override
        public String matchValue()
        {
            if (m_prevString == null)
            {
                if (m_prevResult == null)
                {
                    throw new IllegalStateException();
                }
                m_prevString = m_src.substring(m_prevPos, m_prevEnd);
            }
            return m_prevString;
        }

        @Override
        public MR matchResult()
        {
            if (m_prevResult == null)
            {
                throw new IllegalStateException();
            }
            return m_prevResult;
        }

        @Override
        public boolean reposition(int pos)
        {
            if (pos >= m_nextScanStart)
            {
                if (m_nextEndState == null)
                {
                    return false;
                }
                if (pos <= m_nextPos)
                {
                    return true;
                }
                m_nextScanStart = pos;
                if (!_scanForNext(pos, m_src.length()))
                {
                    m_nextEndState = null;
                    m_nextPos = m_nextEnd = m_src.length();
                    return false;
                }
                return true;
            }
            else
            {
                //the start positions between pos and m_nextScanStart are unchecked. 
                //See if there's a match in there.  If not, leave the next* fields alone
                //No need to scan forward into the part we've already scanned
                _scanForNext(pos, m_nextScanStart);
                m_nextScanStart = pos;
                return (m_nextEndState != null);
            }
        }
        
        private boolean _scanForNext(int start, int end)
        {
            if (start < m_matchMaskPos)
            {
                start = m_matchMaskPos;
            }
            //switch from string positions to mask array bit positions
            start -= m_matchMaskPos;
            end -= m_matchMaskPos;
            while(start < end)
            {
                int wi = start>>5;
                if (wi >= m_matchMask.length)
                {
                    return false;
                }
                //all bits with positions >= start&31
                int mask = -1<<(start&31);
                mask &= m_matchMask[wi];    //only ones with bits set
                if (mask == 0)
                {
                    start = (start|31)+1;   //next start position is after the current word
                    continue;
                }
                //move start position up to next bit set
                start = (wi<<5)+BitUtils.lowBitIndex(mask);
                
                //get corresponding string position and find the _shortest_ match
                //(it will be expanded to the longest match when next() is called)
                final int trypos = start + m_matchMaskPos;
                final int len = m_src.length();
                DfaState<MR> st = m_matcher;
                for (int pos = trypos; pos<len; ++pos)
                {
                    st = st.getNextState(m_src.charAt(pos));
                    if (st == null)
                    {
                        break;
                    }
                    if (st.getMatch() != null)
                    {
                        //found one!
                        m_nextPos = trypos;
                        m_nextEnd = pos+1;
                        m_nextEndState = st;
                        return true;
                    }
                }
                //missed (shouldn't happen if the reverse finder is accurate)
                ++start;
            }
            return false;
        }
    }
    
    private static class NoMatchIterator implements StringMatchIterator<Object>
    {
        @Override
        public boolean hasNext()
        {
            return false;
        }
        @Override
        public Object next()
        {
            throw new NoSuchElementException();
        }
        @Override
        public int matchStartPosition()
        {
            throw new IllegalStateException();
        }
        @Override
        public int matchEndPosition()
        {
            throw new IllegalStateException();
        }
        @Override
        public String matchValue()
        {
            throw new IllegalStateException();
        }
        @Override
        public Object matchResult()
        {
            throw new IllegalStateException();
        }
        @Override
        public boolean reposition(int pos)
        {
            return false;
        }
    }
}
