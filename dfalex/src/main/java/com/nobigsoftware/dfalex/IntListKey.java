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
import backport.java.util.function.ObjIntConsumer;

/**
 * A simple list of integers that can be used as a hash map key and cloned
 */
class IntListKey implements Cloneable
{
	private static int[] NO_INTS = new int[0];
	
	private int[] m_buf = NO_INTS;
	private int m_size = 0;
	private int m_hash = 0;
	private boolean m_hashValid = false;
	
	public IntListKey()
	{}
	public IntListKey(IntListKey src)
	{
		if (src != null && src.m_size > 0)
		{
			m_buf = Arrays.copyOf(src.m_buf, src.m_size);
			m_size = src.m_size;
			if (src.m_hashValid)
			{
				m_hash = src.m_hash;
				m_hashValid = true;
			}
		}
	}
	
	public void clear()
	{
		m_size = 0;
		m_hashValid = false;
	}
	
	public void add(int v)
	{
		if (m_size >= m_buf.length)
		{
			m_buf = Arrays.copyOf(m_buf, m_size + (m_size>>1) + 16);
		}
		m_buf[m_size++] = v;
		m_hashValid = false;
	}
	
	public void forData(ObjIntConsumer<int[]> target)
	{
		target.accept(m_buf, m_size);
	}
	

	@Override
	protected IntListKey clone()
	{
		return new IntListKey(this);
	}
	

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof IntListKey))
		{
			return false;
		}
		IntListKey r = (IntListKey)obj;
		if (m_size != r.m_size || hashCode() != r.hashCode())
		{
			return false;
		}
		for (int i = m_size-1; i>=0; --i)
		{
			if (m_buf[i] != r.m_buf[i])
			{
				return false;
			}
		}
		return true;
	}
	

	@Override
	public int hashCode()
	{
		if (!m_hashValid)
		{
			int h = 0;
			for (int i=0;i<m_size;++i)
			{
				h*=65539;
				h+=m_buf[i];
			}
			h ^= (h>>>17);
			h ^= (h>>>11);
			h ^= (h>>>5);
			m_hash = h;
			m_hashValid = true;
		}
		return m_hash;
	}
}
