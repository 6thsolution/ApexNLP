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


import backport.java.util.function.IntConsumer;

/**
 * Handles translating a CompactIntSubset representing a combination of NFA states
 * to/from a compact DFA state signature representation
 */
class DfaStateSignatureCodec
{
	private IntConsumer m_currentTarget;
	private int m_fieldLength = 32;
	private int m_fieldMask;
	private int m_pendingBits;
	private int m_pendingSize; // always in [1,32]
	private int m_minval;
	
	/**
	 * To build a signature, call this first, then call acceptInt for all the
	 * NFA states, in order, then call finish()
	 * 
	 * @param size number of NFA states in this signature
	 * @param range total number of NFA states in the NFA
	 */
	public void start(IntConsumer target, int size, int range)
	{
		m_currentTarget = target;
		m_fieldLength = getCompactEncodingLengthForSize(size, range);
		if (m_fieldLength >= 31)
		{
			m_fieldMask = Integer.MAX_VALUE;
			m_fieldLength = 31;
		}
		else
		{
			if (m_fieldLength < 1)
			{
				m_fieldLength = 1;
			}
			m_fieldMask = (1<<m_fieldLength)-1;
		}
		m_pendingBits = m_fieldLength-1;
		m_pendingSize = 5;
		m_minval=0;
	}
	
	public void acceptInt(int value)
	{
		int gap = value - m_minval;
		if (gap < 0)
		{
			throw new IllegalArgumentException("values negative or out of order");
		}
		m_minval = value+1;
		if (m_fieldLength < 31)
		{
			while(gap >= m_fieldMask)
			{
				_putField(m_fieldMask);
				gap-=m_fieldMask;
			}
		}
		_putField(gap);
	}
	
	private void _putField(int val)
	{
		if (m_pendingSize >= 32)
		{
			m_currentTarget.accept(m_pendingBits);
			m_pendingBits = val;
			m_pendingSize = m_fieldLength;
			return;
		}
		
		m_pendingBits |= val << m_pendingSize;
		if ((m_pendingSize+=m_fieldLength) > 32)
		{
			m_currentTarget.accept(m_pendingBits);
			m_pendingSize-=32;
			m_pendingBits = val >>> (m_fieldLength - m_pendingSize);
		}
	}
	
	public void finish()
	{
		if (m_pendingSize < 32)
		{
			m_pendingBits |= (~0)<<m_pendingSize;
		}
		m_currentTarget.accept(m_pendingBits);
		m_currentTarget = null;
	}
	
	public static void expand(IntListKey key, IntConsumer target)
	{
		key.forData((buf, len) -> expand(buf, len, target));
	}
	
	public static void expand(int[] sigbuf, final int siglen, IntConsumer target)
	{
		if (siglen <= 0)
		{
			return;
		}
		int bits = sigbuf[0];
		int nextpos=1;
		final int fieldLen = (bits&31)+1;
		final int fieldMask;
		if (fieldLen>=31)
		{
			fieldMask = Integer.MAX_VALUE;
		}
		else
		{
			fieldMask = (1<<fieldLen)-1;
		}
		bits>>>=5;
		int bitsleft = 32-5;
		int minval = 0;
		for (;;)
		{
			int val = bits;
			if (bitsleft < fieldLen)
			{
				if (nextpos >= siglen)
				{
					break;
				}
				bits = sigbuf[nextpos++];
				val |= (bits << bitsleft);
				bits >>>= fieldLen - bitsleft;
				bitsleft += 32 - fieldLen;
			}
			else
			{
				bits >>>= fieldLen;
				bitsleft -= fieldLen;
			}
			val&=fieldMask;
			minval+=val;
			if (val != fieldMask || fieldLen>=31)
			{
				target.accept(minval++);
			}
		}
	}
	
	//Given Psym = 1_count/total_count
	//Probability that an extra word will be required with symbol length len
	//Px = (1-Psym)^(2^len-1)
	//Expected total encoding length per 1
	//BPS = len/(1-Px) = len/(1-(1-Psym)^(2^len-1))
	
	private static final int[] LENGTH_PROGRESSION = {
	/* len <= 1 until size/range >= 1/ */ 3,
	/* len <= 2 until size/range >= 1/ */ 5,
	/* len <= 3 until size/range >= 1/ */ 7,
	/* len <= 4 until size/range >= 1/ */ 12,
	/* len <= 5 until size/range >= 1/ */ 20,
	/* len <= 6 until size/range >= 1/ */ 36,
	/* len <= 7 until size/range >= 1/ */ 66,
	/* len <= 8 until size/range >= 1/ */ 124,
	/* len <= 9 until size/range >= 1/ */ 234,
	/* len <= 10 until size/range >= 1/ */ 445,
	/* len <= 11 until size/range >= 1/ */ 855,
	/* len <= 12 until size/range >= 1/ */ 1649,
	/* len <= 13 until size/range >= 1/ */ 3194,
	/* len <= 14 until size/range >= 1/ */ 6209,
	/* len <= 15 until size/range >= 1/ */ 12101,
	/* len <= 16 until size/range >= 1/ */ 23638,
	/* len <= 17 until size/range >= 1/ */ 46263,
	/* len <= 18 until size/range >= 1/ */ 90696,
	/* len <= 19 until size/range >= 1/ */ 178061,
	/* len <= 20 until size/range >= 1/ */ 350024,
	/* len <= 21 until size/range >= 1/ */ 688829,
	/* len <= 22 until size/range >= 1/ */ 1356923,
	/* len <= 23 until size/range >= 1/ */ 2675371,
	/* len <= 24 until size/range >= 1/ */ 5279086,
	/* len <= 25 until size/range >= 1/ */ 10424271,
	/* len <= 26 until size/range >= 1/ */ 20597568,
	/* len <= 27 until size/range >= 1/ */ 40723415,
	/* len <= 28 until size/range >= 1/ */ 80557921,
	/* len <= 29 until size/range >= 1/ */ 159436815,
	/* len <= 30 until size/range >= 1/ */ 315695254
	};

	public static int getCompactEncodingLengthForSize(int size, int range)
	{
		if (range < 1)
		{
			return 32;
		}
		int ratedivisor = range/size;
		int min = 0;
		int lim = LENGTH_PROGRESSION.length;
		while(min<lim)
		{
			int t = min + ((lim-min)>>1);
			if (ratedivisor >= LENGTH_PROGRESSION[t])
			{
				min = t+1;
			}
			else
			{
				lim = t;
			}
		}
		return min+1;
	}
	
	static double _expectedBitsPerEntry(double rate, double len)
	{
		double Pexceed = Math.pow(1.0-rate, Math.pow(2.0, len) - 1);
		return len / (1.0-Pexceed);
	}
	
	/*
	 * This was used to calculate LENGTH_PROGRESSION
	 * 
	public static void main(String []argv)
	{
		int divisor=1;
		for(int len=1;len<=30;len++)
		{
			while(_expectedBitsPerEntry(1.0/divisor, len) <= _expectedBitsPerEntry(1.0/divisor, len+1))
			{
				divisor += (divisor>>24)+1;
			}
			System.out.println("/* len <= " + len + " until size/range >= 1/ *"+"/ " + divisor + ",");
		}
	}
	 */
}
