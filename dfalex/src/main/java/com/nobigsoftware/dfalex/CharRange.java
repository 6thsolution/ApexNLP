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

import java.util.Arrays;

/**
 * A CharRange is a {@link Pattern} that matches a single character from some set or
 * range of characters.  In regular expressions, such a pattern is written
 * with [ ...stuff... ]
 * <P>
 * Several commonly used ranges are provided as constants (e.g., {@link #DIGITS}) and
 * the {@link Builder} class can be used to construct simple and complex ranges. 
 */
public class CharRange implements Matchable
{
    private static final long serialVersionUID = 1L;

    private static char[] NO_CHARS = new char[0];

    /**
     * A CharRange that matches any single character
     */
    public static final CharRange ALL = new CharRange(Character.MIN_VALUE,
            Character.MAX_VALUE);

    /**
     * A CharRange that matches no characters.  It's not very useful, but how could I have
     * ALL without NONE?
     */
    public static final CharRange NONE = new CharRange(NO_CHARS);

    /**
     * A CharRange that matches any decimal digit (0-9)
     */
    public static final CharRange DIGITS = new CharRange('0', '9');
    
    /**
     * A CharRange that matches any octal digit (0-7)
     */
    public static final CharRange OCTALDIGITS = new CharRange('0', '7');
    
    /**
     * A CharRange that matches any hexadecimal digit (0-9, a-f, and A-F)
     */
    public static final CharRange HEXDIGITS = builder()
            .addRange('0', '9')
            .addRange('A', 'F')
            .addRange('a', 'f')
            .build();

    /**
     * Matches any ascii lower-case letter
     */
    public static final CharRange ASCIILOWER = new CharRange('a', 'z');

    /**
     * Matches any ascii upper-case letter
     */
    public static final CharRange ASCIIUPPER = new CharRange('A', 'Z');

    /**
     * Matches any ascii white space character (values from 0 to 32)
     */
    public static final CharRange ASCIIWHITE = new CharRange('\u0000', ' ');
    
    /**
     * Matches Java identifier start characters
     */
    public static final CharRange JAVA_LETTER = builder().addRange('a', 'z').addRange('A', 'Z').addChars("_$").build();

    /**
     * Matches Java identifier characters
     */
    public static final CharRange JAVA_ID_CHAR = builder().addRange(JAVA_LETTER).addRange(DIGITS).build();

    // characers in here are in value order and are unique
    // a character c is in this CharRange iff m_bounds contains an ODD number of
    // characters <= c
    private final char[] m_bounds;

    private CharRange(char[] bounds)
    {
        m_bounds = bounds;
    }

    /**
     * Create a CharRange that matches all characters with code points front in to out, inclusive.
     * @param in  one end of the character range
     * @param out the other end of the character range
     */
    public CharRange(char in, char out)
    {
        if (out < in)
        {
            char t = out;
            out = in;
            in = t;
        }
        if (out >= Character.MAX_VALUE)
        {
            m_bounds = new char[]{ in };
        } 
        else
        {
            m_bounds = new char[]{ in, (char) (out + 1) };
        }
    }

    /**
     * Check whether or not this range contains a character c
     * @param c character to test
     * @return true iff this CharRange contains c
     */
    public boolean contains(char c)
    {
        int lo = 0, hi = m_bounds.length;
        while (hi > lo)
        {
            int test = lo+((hi-lo)>>1);
            if (m_bounds[test]<=c)
            {
              lo = test+1;  
            }
            else
            {
                hi=test;
            }
        }
        return ((lo&1)!=0);
    }
    
    /**
     * Return a new CharRange that matches the characters that this one does not match.
     * @return  the complement of this CharRange
     */
    public final CharRange getComplement()
    {
        if (m_bounds.length == 0)
        {
            return ALL;
        }
        if (m_bounds[0] == '\u0000')
        {
            // range includes 0
            if (m_bounds.length == 1)
            {
                //this == ALL
                return NONE;
            }
            return new CharRange(Arrays.copyOfRange(m_bounds, 1,
                    m_bounds.length));
        } 
        else
        {
            char[] ar = new char[m_bounds.length + 1];
            System.arraycopy(m_bounds, 0, ar, 1, m_bounds.length);
            ar[0] = '\u0000';
            return new CharRange(ar);
        }
    }

    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object r)
    {
        if (!(r instanceof CharRange))
        {
            return false;
        }
        return Arrays.equals(m_bounds, ((CharRange)r).m_bounds);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        //FNV-1a, except we xor in chars instead of bytes
        int hash = (int)2166136261L;
        for (char c : m_bounds)
        {
            hash = (hash ^ (int)c)*16777619;
        }
        return hash;
    }

    @Override
    public boolean matchesEmpty()
    {
        return false;
    }
    
    @Override
    public boolean matchesNonEmpty()
    {
        return (m_bounds.length>0);
    }

    @Override
    public boolean matchesSomething()
    {
        return (m_bounds.length>0);
    }

    @Override
    public boolean isUnbounded()
    {
        return false;
    }

    @Override
    public Matchable getReversed()
    {
        return this;
    }

    @Override
    public final int addToNFA(Nfa<?> nfa, int targetState)
    {
        final int len = m_bounds.length;
        int startState = nfa.addState(null);
        for (int i = 0; i < len; i += 2)
        {
            char cfirst = m_bounds[i];
            char clast;
            if (i + 1 < len)
            {
                clast = (char) (m_bounds[i + 1] - 1);
            }
            else
            {
                clast = Character.MAX_VALUE;
            }
            nfa.addTransition(startState, targetState, cfirst, clast);
        }
        return startState;
    }
    
    
    /**
     * Create a pattern that matches a single character
     * 
     * @param c character to match
     * @return the pattern that matches the character
     */
    public static CharRange single(char c)
    {
        return new CharRange(c,c);
    }
    
    /**
     * Create a pattern that matches all single characters with a range of values
     * <P>
     * The pattern will match a character c if from &lt;= c &lt;= to.
     * 
     * @param from inclusive lower bound 
     * @param to inclusive upper bound
     * @return the pattern that matches the range
     */
    public static CharRange range(char from, char to)
    {
        return new CharRange(from, to);
    }
    
    /**
     * Create a CharRange that matches any of the characters in the given string
     * @param chars characters to match
     * @return the new CharRange, or NONE if that's appropriate
     */
    public static CharRange anyOf(String chars)
    {
        if (chars.isEmpty())
        {
            return NONE;
        }
        return builder().addChars(chars).build();
    }
    
    /**
     * Create a CharRange that matches any characters EXCEPT the characters in the given string
     * @param chars characters to exclude
     * @return the new CharRange, or NONE if that's appropriate
     */
    public static CharRange notAnyOf(String chars)
    {
        if (chars.isEmpty())
        {
            return ALL;
        }
        return builder().addChars(chars).invert().build();
    }
    
    /**
     * Create a CharRange.Builder
     * @return new Builder()
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Instances of this class are used to incrementally build {@link CharRange}s
     * <P>
     * Initially it contains an empty set of matching characters (CharRange.NONE).
     * Methods like addChars, addRange, exclude, etc. are called to add and remove characters
     * from the set, and then {@link #build()} is called to produce an immutable CharRange
     * object that matches characters in the set.
     */
    public static class Builder
    {
        //intermediate representation for a set of characters.
        //There's an 'in' at char c when the array contains c*2
        //There's an 'out' at char c when the array contains c*2+1
        //a char c is in the set then the number of ins at or before c exceeds
        //the number of outs at or before c
        int[] m_inouts;

        //number of inouts in the inout array
        int m_size;

        //true if the inout array is normalized.  In normalized form, the ints are all in order, and
        //alternate in-out-in-out
        boolean m_normalized;

        /**
         * Create a new Builder.
         * <P>
         * It's usually a little more convenient to use {@link CharRange#builder()}
         */
        public Builder()
        {
            m_inouts = new int[8];
            m_normalized = true;
            m_size = 0;
        }
        
        /**
         * Clears the current range.
         * <P>
         * After this call, {@link #build()} will return {@link CharRange#NONE}
         * @return this
         */
        public Builder clear()
        {
            m_size = 0;
            m_normalized = true;
            return this;
        }

        /**
         * Add a character to the current set
         * @param c  This character will be added to the set
         * @return this
         */
        public Builder addChar(char c)
        {
            addRange(c,c);
            return this;
        }
        
        /**
         * Add characters to the current set
         * @param chars  All characters in this string will be added to the set
         * @return this
         */
        public Builder addChars(String chars)
        {
            _reserve(chars.length()*2);
            for (int i=0;i<chars.length();++i)
            {
                char c = chars.charAt(i);
                addRange(c,c);
            }
            return this;
        }
        
        /**
         * Add a range of characters to the current set
         * <P>
         * Adds all characters x such that first &lt;= x and x &lt;= last 
         * @param first least-valued character to add
         * @param last greatest-valued character to add
         * @return this
         */
        public Builder addRange(char first, char last)
        {
            _reserve(2);
            m_normalized = false;
            if (first > last)
            {
                char t = first;
                first = last;
                last = t;
            }
            m_inouts[m_size++] = ((int) first) << 1;
            if (last < Character.MAX_VALUE)
            {
                m_inouts[m_size++] = (((int) last) << 1) + 3;
            }
            return this;
        }

        /**
         * Add characters from another {@link CharRange}
         * @param cr All characters matched by this CharRange will be added to the current set
         * @return this
         */
        public Builder addRange(CharRange cr)
        {
            _reserve(m_size + cr.m_bounds.length);
            for (int i = 0; i < cr.m_bounds.length; ++i)
            {
                m_normalized = false;
                m_inouts[m_size++] = (((int) cr.m_bounds[i]) << 1) | (i & 1);
            }
            return this;
        }
        
        /**
         * Remove characters from another {@link CharRange}
         * <P>
         * This is implemented using {@link #invert()} and {@link #addRange(CharRange)}
         * 
         * @param cr All characters matched by this CharRange will be removed from the current set
         * @return this
         */
        public final Builder exclude(CharRange cr)
        {
            invert();
            addRange(cr);
            invert();
            return this;
        }

        /**
         * Intersect with another {@link CharRange}
         * <P>
         * This is implemented by {@link #exclude(CharRange)}ing cr.getComplement()
         *  
         * @param cr All characters that are NOT matched by this CharRange will be removed from the current set
         * @return this
         */
        public final Builder intersect(CharRange cr)
        {
            exclude(cr.getComplement());
            return this;
        }

        /**
         * Make the current range case independent.
         * <P>
         * For every char c in the range, Character.toUpperCase(c) and
         * Character.toLowerCase(c) are added.
         * 
         * @return this
         */
        public Builder expandCases()
        {
            _normalize();
            final int len = m_size;
            int[] src = Arrays.copyOf(m_inouts, len);
            for (int i=0; i<src.length; i+=2)
            {
                char s = (char) (src[i] >> 1);
                char e = (i+1 >= len ? Character.MAX_VALUE : (char)( (src[i+1]>>1) -1 ));
                RangeDecaser.expandRange(s, e, this);
            }
            return this;
        }

        /**
         * Invert the current set
         * <P>
         * When this returns the current set will include only those characters that were NOT in the set before the call.
         * 
         * @return this
         */
        public Builder invert()
        {
            _normalize();

            if (m_size <= 0)
            {
                addRange(ALL);
                return this;
            }

            if (m_inouts[0] == 0)
            {
                // current range includes 0
                --m_size;
                for (int i = 0; i < m_size; ++i)
                {
                    m_inouts[i] = m_inouts[i + 1] ^ 1;
                }
            }
            else
            {
                // current range !includes 0
                _reserve(1);
                ++m_size;
                for (int i = m_size - 1; i > 0; --i)
                {
                    m_inouts[i] = m_inouts[i - 1] ^ 1;
                }
                m_inouts[0] = 0;
            }
            return this;
        }
        
        /**
         * Produce a CharRange for the current set
         * <P>
         * This method does not alter the current set in any way -- it may be further modified and
         * used to produce more CharRange objects.
         * 
         * @return the new CharRange.  Multiple calls that build equivalent ranges may or may not
         *      return the same object.
         */
        public CharRange build()
        {
            _normalize();
            if (m_size <= 0)
            {
                return NONE;
            }
            if (m_size == 1 && m_inouts[0] == 0)
            {
                return ALL;
            }
            char[] ar = new char[m_size];
            for (int i = 0; i < m_size; ++i)
            {
                ar[i] = (char) (m_inouts[i] >> 1);
            }
            return new CharRange(ar);
        }

        
        private void _normalize()
        {
            if (m_size > 0 && !m_normalized)
            {
                Arrays.sort(m_inouts, 0, m_size);
                int d = 0;
                int depth = 0;
                for (int s = 0; s < m_size; )
                {
                    int olddepth = depth;
                    int inout = m_inouts[s++];
                    depth += ((inout&1) == 0 ? 1 : -1);
                    while(s<m_size && (m_inouts[s]>>1)==(inout>>1))
                    {
                        depth += ((m_inouts[s++]&1) == 0 ? 1 : -1);
                    }
                    if (depth > 0)
                    {
                        if (olddepth <= 0)
                        {
                            m_inouts[d++]=inout&~1;
                        }
                    }
                    else if (olddepth > 0)
                    {
                        m_inouts[d++]=inout|1;
                    }
                }
                m_size = d;
            }
            m_normalized = true;
        }

        //make sure we have room to add n ints to m_inouts
        private void _reserve(int n)
        {
            if (m_inouts.length < m_size + n)
            {
                _normalize();
                if ((m_inouts.length >> 1) > m_size + n)
                {
                    return;
                }
                m_inouts = Arrays.copyOf(m_inouts,
                        Math.max(m_inouts.length * 2, m_size + n));
            }
        }
    }
    
    //helper class for calculating case-independent character ranges
    //We make this a separate class to defer its initialization until the first time we use it
    private static class RangeDecaser
    {
        //contains quads of characters s,e,lcdelta,ucdelta, where:
        //s and e define an inclusive range of contiguous characters
        //lcdelta and ucdelta define the deltas to the lower case and upper case versions of characters in the range
        static char[] s_deltaTable = _buildDeltaTable();
        
        static void expandRange(final char s, final char e, Builder target)
        {
            final int tableSize = s_deltaTable.length>>2;
            int lo=0;
            int hi=1;
            //finger search to find the first range with an end >= s
            while(s_deltaTable[(hi<<2)+1] < s)
            {
                //hi is too lo
                lo=hi+1;
                hi<<=1;
                if (hi >= tableSize)
                {
                    hi = tableSize;
                    break;
                }
            }
            while(hi > lo)
            {
                int test = lo + ((hi-lo)>>1);
                if (s_deltaTable[(test<<2)+1] < s)
                {
                    lo = test+1;
                }
                else
                {
                    hi = test;
                }
            }
            for (;lo < tableSize;++lo)
            {
                char subs = s_deltaTable[lo<<2];
                char sube = s_deltaTable[(lo<<2)+1];
                if (subs > e)
                {
                    break;
                }
                if (s > subs)
                {
                    subs = s;
                }
                if (e < sube)
                {
                    sube = e;
                }
                if (sube<subs)
                {
                    continue;
                }
                char delta = s_deltaTable[(lo<<2)+2];
                if (delta != '\0')
                {
                    target.addRange((char)((subs + delta)&65535), (char)((sube + delta)&65535));
                }
                delta = s_deltaTable[(lo<<2)+3];
                if (delta != '\0')
                {
                    target.addRange((char)((subs + delta)&65535), (char)((sube + delta)&65535));
                }
            }
        }
       
        
        private static char[] _buildDeltaTable()
        {
            int s=0;
            int dpos=0;
            char[] ar = new char[256];
            while(s<65536)
            {
                int lcd = Character.toLowerCase(s)-s;
                int ucd = Character.toUpperCase(s)-s;
                if (lcd == 0 && ucd == 0)
                {
                    ++s;
                    continue;
                }
                int e = s+1;
                for (;e<65536;++e)
                {
                    int lcd2 = Character.toLowerCase(e)-e;
                    int ucd2 = Character.toUpperCase(e)-e;
                    if (lcd2 != lcd || ucd2!=ucd)
                    {
                        break;
                    }
                }
                while (dpos+4 > ar.length)
                {
                    ar = Arrays.copyOf(ar, ar.length*2);
                }
                ar[dpos++] = (char)s;
                ar[dpos++] = (char)(e-1);
                ar[dpos++] = (char)(lcd&65535);
                ar[dpos++] = (char)(ucd&65535);
                s=e;
            }
            return Arrays.copyOf(ar, dpos);
        }
    }
}
