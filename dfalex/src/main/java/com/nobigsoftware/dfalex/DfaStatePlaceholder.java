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
 * Base class for serializable placeholders that construct final-form DFA states and
 * temporarily assume their places in the DFA.
 * <P>
 * In serialized placeholders, target states are identified by their state number in a
 * SerializableDfa.
 */
abstract class DfaStatePlaceholder<MATCH> extends DfaStateImpl<MATCH> implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	protected transient DfaStateImpl<MATCH> m_delegate = null;
	
	/**
	 * Create a new DfaStatePlaceholder
	 * <P>
	 * The initially constructed state will accept no strings
	 */
	public DfaStatePlaceholder()
	{
	}

	/**
	 * Creates the final form delegate state, implementing all the required
	 * transitions and matches.
	 * <P>
	 * This is called on all DFA state placeholders after they are constructed
	 */
	abstract void createDelegate(int statenum, List<DfaStatePlaceholder<MATCH>> allStates);
	
	@Override
	final void fixPlaceholderReferences()
	{
		m_delegate.fixPlaceholderReferences();
	}
	
	@Override
	final DfaStateImpl<MATCH> resolvePlaceholder()
	{
		return m_delegate.resolvePlaceholder();
	}
	
	@Override
	final public DfaState<MATCH> getNextState(char c)
	{
		return m_delegate.getNextState(c);
	}
	@Override
	final public MATCH getMatch()
	{
		return m_delegate.getMatch();
	}
    @Override
    final public void enumerateTransitions(DfaTransitionConsumer<MATCH> consumer)
    {
        m_delegate.enumerateTransitions(consumer);
    }

    @Override
    final public int getStateNumber()
    {
        return m_delegate.getStateNumber();
    }

    @Override
    public Iterable<DfaState<MATCH>> getSuccessorStates()
    {
        return m_delegate.getSuccessorStates();
    }
}
