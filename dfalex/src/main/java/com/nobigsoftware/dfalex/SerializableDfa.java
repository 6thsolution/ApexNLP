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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class SerializableDfa<RESULT> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final ArrayList<DfaStatePlaceholder<RESULT>> m_dfaStates;
	private final int[] m_startStateNumbers;
	
	private transient List<DfaState<RESULT>> m_startStatesMemo;

	public SerializableDfa(RawDfa<RESULT> rawDfa)
	{
		final List<DfaStateInfo> origStates = rawDfa.getStates();
		final int len = origStates.size();
		m_dfaStates = new ArrayList<>(len);
		m_startStateNumbers = rawDfa.getStartStates();
		while(m_dfaStates.size() < len)
		{
			m_dfaStates.add(new PackedTreeDfaPlaceholder<>(rawDfa, m_dfaStates.size()));
		}
	}
	
	public synchronized List<DfaState<RESULT>> getStartStates()
	{
		if (m_startStatesMemo == null)
		{
	        final int len = m_dfaStates.size();
	        for (int i=0;i<len;++i)
	        {
	            m_dfaStates.get(i).createDelegate(i, m_dfaStates);
	        }
	        for (int i=0;i<len;++i)
	        {
	            m_dfaStates.get(i).fixPlaceholderReferences();
	        }
			m_startStatesMemo = new ArrayList<>(m_startStateNumbers.length);
			for (int startState : m_startStateNumbers)
			{
				m_startStatesMemo.add(m_dfaStates.get(startState).resolvePlaceholder());
			}
		}
		return m_startStatesMemo;
	}
}
