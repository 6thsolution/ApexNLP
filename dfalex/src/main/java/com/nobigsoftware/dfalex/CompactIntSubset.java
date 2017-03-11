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
import backport.java.util.function.IntConsumer;

/**
 * A subset of the integers in [0, range), with compact representation, and add+remove in amortized
 * constant(ish) time
 */
class CompactIntSubset
{
	private final int m_range; //the set contains integers in [0, m_range)
	private final int[] m_bitmask; //bit mask of set members
	private final int[] m_usemarks; //indexes of non-zero words in m_bitmask. may contain duplicates
	private int m_size; //number of integers in the set == number non-zero bits in m_bitmask
	private int m_marksize; //number of marks in m_usemarks
	private boolean m_sorted = true; //true if m_usemarks is sorted and deduped
	
	public CompactIntSubset(int range)
	{
		m_range = range;
		m_bitmask = new int[(range+31)>>5];
		m_usemarks = new int[m_bitmask.length*2];
	}
	
	public int getRange()
	{
		return m_range;
	}
	
	public int getSize()
	{
		return m_size;
	}
	
	public void clear()
	{
		if (m_marksize > m_bitmask.length>>1)
		{
			for (int i=0; i<m_bitmask.length; ++i)
			{
				m_bitmask[i]=0;
			}
		}
		else
		{
			for (int i=0; i<m_marksize; ++i)
			{
				m_bitmask[m_usemarks[i]]=0;
			}
		}
		m_marksize=0;
		m_size = 0;
		m_sorted = true;
	}
	
	public boolean add(int val)
	{
		int bit = 1<<(val&31);
		int index = val>>>5;
		int v = m_bitmask[index];
		if ((v & bit) != 0)
		{
			return false;
		}
		m_bitmask[index] = v|bit;
        ++m_size;
		if (v==0)
		{
    		if (m_marksize < m_usemarks.length)
    		{
                m_usemarks[m_marksize++] = index;
                m_sorted = false;
    		}
    		else
    		{
    			_regenerateMarks();
    		}
		}
		return true;
	}
	
	public boolean remove(int val)
	{
		int bit = 1<<(val&31);
		int index = val>>>5;
		int v = m_bitmask[index];
		if ((v & bit) == 0)
		{
			return false;
		}
		m_bitmask[index] = v&~bit;
		m_sorted = false;
		--m_size;
		return true;
	}
	
	public void dumpInOrder(IntConsumer target)
	{
		_sortMarks();
		for (int i=0; i<m_marksize; ++i)
		{
			int wordIndex = m_usemarks[i];
			int bits = m_bitmask[wordIndex];
			while(bits != 0)
			{
				target.accept((wordIndex<<5)+BitUtils.lowBitIndex(bits));
                bits = BitUtils.turnOffLowBit(bits);
			}
		}
	}
	
	//for the debugger -- inefficient, but doesn't modify anything
	@Override
    public String toString()
	{
	    boolean first = true;
	    StringBuilder sb = new StringBuilder();
	    sb.append("[");
	    for (int wordIndex = 0; wordIndex < m_bitmask.length; ++wordIndex)
	    {
            int bits = m_bitmask[wordIndex];
            while(bits != 0)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sb.append(",");
                }
                sb.append((wordIndex<<5)+BitUtils.lowBitIndex(bits));
                bits = BitUtils.turnOffLowBit(bits);
            }
        }
	    sb.append("]");
	    return sb.toString();
    }
	    

	private void _regenerateMarks()
	{
		m_marksize = 0;
		for (int i = 0; i<m_bitmask.length; ++i)
		{
			if (m_bitmask[i]!=0)
			{
				m_usemarks[m_marksize++] = i;
			}
		}
		m_sorted = true;
	}
	
	private void _sortMarks()
	{
		if (m_sorted)
		{
			return;
		}
		if (m_size>=m_bitmask.length>>3)
		{
			_regenerateMarks();
			return;
		}
		int newsize=0;
		for (int i=0;i<m_marksize;++i)
		{
			m_usemarks[m_marksize+m_usemarks[i]] = 0;
		}
		for (int i=0;i<m_marksize;++i)
		{
			int v = m_usemarks[i];
			if (m_bitmask[v]!=0 && m_usemarks[m_marksize+v]==0)
			{
				m_usemarks[m_marksize+v] = 1;
				m_usemarks[newsize++]=v;
			}
		}
		m_marksize = newsize;
		Arrays.sort(m_usemarks, 0, m_marksize);
		m_sorted = true;
//		assert(m_marksize == m_size); //TODO
	}
}
