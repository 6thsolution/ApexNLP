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

import java.util.List;

/**
 * A DFA in uncomrpessed form
 */
class RawDfa<RESULT>
{
	private final List<DfaStateInfo> m_dfaStates;
	private final List<RESULT> m_acceptSets;
	private final int[] m_startStates;

	/**
	 * Create a new RawDfa.
	 */
	public RawDfa(List<DfaStateInfo> dfaStates,
			List<RESULT> acceptSets,
			int[] startStates)
	{
		m_dfaStates = dfaStates;
		m_acceptSets = acceptSets;
		m_startStates = startStates;
	}
	
	public List<DfaStateInfo> getStates()
	{
		return m_dfaStates;
	}
	
	public List<RESULT> getAcceptSets()
	{
		return m_acceptSets;
	}

	public int[] getStartStates()
	{
		return m_startStates;
	}
}
