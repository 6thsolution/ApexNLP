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

/**
 * A transition in a {@link Nfa}
 * <P>
 * Instances of this class are immutable
 */
public final class NfaTransition implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * The first character that triggers this transition
     */
    public final char m_firstChar;
    /**
     * The last character that triggers this transition
     */
	public final char m_lastChar;
    /**
     * The number of the target state of this transition
     */
	public final int m_stateNum;
	
	/**
	 * Create a new immutable NFA Transition.
	 * 
	 * @param firstChar value for {@link #m_firstChar}
	 * @param lastChar value for {@link #m_lastChar}
	 * @param stateNum value for {@link #m_stateNum}
	 */
	public NfaTransition(char firstChar, char lastChar, int stateNum)
	{
		super();
		m_firstChar = firstChar;
		m_lastChar = lastChar;
		m_stateNum = stateNum;
	}

    @Override
    public boolean equals(Object arg)
    {
        if (arg instanceof NfaTransition)
        {
            NfaTransition r = (NfaTransition)arg;
            return (r.m_firstChar == m_firstChar && r.m_lastChar==m_lastChar && r.m_stateNum == m_stateNum);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = (int)2166136261L;
        hash = (hash ^ (int)m_firstChar)*16777619;
        hash = (hash ^ (int)m_lastChar)*16777619;
        hash = (hash ^ (int)m_stateNum)*16777619;
        return hash ^ (hash>>16);
    }
}