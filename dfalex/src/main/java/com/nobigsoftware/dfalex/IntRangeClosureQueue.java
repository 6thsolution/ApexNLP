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
 * Closure queue containing integers in a limited range.
 */
class IntRangeClosureQueue
{
	final int[] m_bitmask;
	final int[] m_queue;
	int m_readpos, m_writepos;
	
	/**
	 * Create a new IntRangeClosureQueue.
	 * <P>
	 * The queue can contain integer in [0,range)
	 * 
	 * @param range 
	 */
	public IntRangeClosureQueue(int range)
	{
		m_bitmask = new int[(range+31)>>5];
		m_queue = new int[m_bitmask.length*32 + 1];
	}
	
	/**
	 * Add an integer to the tail of the queue if it's not already present
	 * 
	 * @param val  integer to add
	 * @return true if the integer was added to the queue, or false
	 *         if it was not added, because it was already in the queue
	 */
	public boolean add(int val)
	{
		int i = val>>5;
		int bit = 1<<(val&31);
		int oldbits = m_bitmask[i];
		if ((oldbits & bit)==0)
		{
			m_bitmask[i] = oldbits|bit;
			m_queue[m_writepos] = val;
			if (++m_writepos >= m_queue.length)
			{
				m_writepos = 0;
			}
			assert(m_writepos != m_readpos);
			return true;
		}
		else
		{
			return false;
		}
	}
	
    /**
     * Remove an integer from the head of the queue, if it's non-empty
     * 
     * @return the integer removed from the head of the queue, or -1 if the
     *         queue was empty.
     */
	public int poll()
	{
		if (m_readpos == m_writepos)
		{
			return -1;
		}
		int val = m_queue[m_readpos];
		if (++m_readpos >= m_queue.length)
		{
			m_readpos = 0;
		}
		int i = val>>5;
		int bit = 1<<(val&31);
		assert((m_bitmask[i]&bit) != 0);
		m_bitmask[i]&=~bit;
        assert((m_bitmask[i]&bit) == 0);
		return val;
	}
}
