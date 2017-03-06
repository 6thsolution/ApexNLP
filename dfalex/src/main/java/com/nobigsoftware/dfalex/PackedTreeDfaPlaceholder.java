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

import java.util.Iterator;
import java.util.List;

/**
 * Serializable placeholder for DFA states implemented
 * as packed binary search trees.
 */
class PackedTreeDfaPlaceholder<MATCH> extends DfaStatePlaceholder<MATCH>
{
	private static final long serialVersionUID = 1L;
	
	private static final char[] NO_CHARS = new char[0];

	//Array-packed binary search tree 
	//The BST contains an internal node for char c if the the transition on c is
	//different from the transition on c-1
	//Internal nodes are packed heap-style: 
	//the root node is at [0], the children of [x] are at [2x+1] and [2x+2]
	private char[] m_internalNodes;
	//The leaves of the packed tree, holding the state numbers transitioned to
	//the children of m_internalNodes[x] are at [2x+1-m_internalNodes.length] and [2x+2-m_internalNodes.length]
	//target number -1 means no transition
	private int[] m_targetStateNumbers;
	private MATCH m_match;
	
	PackedTreeDfaPlaceholder(RawDfa<MATCH> rawDfa, int stateNum)
	{
		DfaStateInfo info = rawDfa.getStates().get(stateNum);
        m_match = rawDfa.getAcceptSets().get(info.getAcceptSetIndex());
		
		int rawTransCount = info.getTransitionCount();
		if (rawTransCount<=0)
		{
			m_internalNodes = NO_CHARS;
			m_targetStateNumbers = new int[] {-1};
			return;
		}
		
		//Find all characters c such that the transition for c
		//is different from the transition for c-1
		char [] tempChars = new char[rawTransCount*2];
		
		int len=0;
		NfaTransition trans = info.getTransition(0);
		if (trans.m_firstChar != '\0')
		{
			tempChars[len++] = trans.m_firstChar;
		}
		for (int i=1;i<rawTransCount;++i)
		{
			NfaTransition nextTrans = info.getTransition(i);
			if (nextTrans.m_firstChar > trans.m_lastChar+1)
			{
				//there's a gap between transitions
				tempChars[len++] = (char)(trans.m_lastChar+1);
				tempChars[len++] = nextTrans.m_firstChar;
			}
			else if (nextTrans.m_stateNum != trans.m_stateNum)
			{
				tempChars[len++] = (char)(nextTrans.m_firstChar);
			}
			trans = nextTrans;
		}
		if (trans.m_lastChar != Character.MAX_VALUE)
		{
			tempChars[len++] = (char)(trans.m_lastChar+1);
		}
		
		if (len<1)
		{
			//all characters same transition
			m_internalNodes = NO_CHARS;
			m_targetStateNumbers = new int[] {trans.m_stateNum};
			return;
		}
		
		//make the packed tree 
		m_internalNodes = new char[len];
		m_targetStateNumbers = new int[len+1];
		_transcribeSubtree(0, new TranscriptionSource(tempChars, info));
	}


	@Override
	void createDelegate(int statenum, List<DfaStatePlaceholder<MATCH>> allStates)
	{
		DfaStateImpl<?>[] targetStates = new DfaStateImpl<?>[m_targetStateNumbers.length];
		for (int i=0; i < targetStates.length; ++i)
		{
		    int num = m_targetStateNumbers[i];
			targetStates[i] = (num < 0 ? null : allStates.get(num));
		}
		m_delegate = new StateImpl<>(m_internalNodes, targetStates, m_match, statenum);
	}
	
	//generate the tree by inorder traversal
	private void _transcribeSubtree(int root, TranscriptionSource ts)
	{
		if (root < m_internalNodes.length)
		{
			_transcribeSubtree(root*2+1, ts);
			m_internalNodes[root] = ts.nextChar();
			_transcribeSubtree(root*2+2, ts);
		}
		else
		{
			m_targetStateNumbers[root-m_internalNodes.length]=ts.getCurrentTarget();
		}
	}
	
	//Maintains a cursor in the list of transition characters
	private static class TranscriptionSource
	{
        final DfaStateInfo m_stateInfo;
		char[] m_srcChars;
		//cursor position is just before m_srcChars[m_srcPos]
		int m_srcPos;
		//transitions an indexes less than this are no longer relvant
		int m_currentTrans;
		
		TranscriptionSource(char[] srcChars, DfaStateInfo stateInfo)
		{
			m_srcChars = srcChars;
			m_srcPos = 0;
			m_stateInfo = stateInfo;
			m_currentTrans = 0;
		}
		//get the next character and increment the cursor
		char nextChar()
		{
			return m_srcChars[m_srcPos++];
		}
		int getCurrentTarget()
		{
		    //get a representative character
			char c = ( m_srcPos>0 ? m_srcChars[m_srcPos-1] : '\0' );
			//and find the effective transition if any
			for (;;++m_currentTrans)
			{
				if (m_currentTrans >= m_stateInfo.getTransitionCount())
				{
					return -1;
				}
				NfaTransition trans = m_stateInfo.getTransition(m_currentTrans);
				if (trans.m_lastChar>=c)
				{
					return (c >= trans.m_firstChar ? trans.m_stateNum : -1);
				}
			}
		}
	}

	private static class StateImpl<M> extends DfaStateImpl<M> implements Iterable<DfaState<M>>
	{
		private final char[] m_internalNodes;
		private final DfaStateImpl<?>[] m_targetStates;
		private final M m_match;
		private final int m_stateNum;
		
		StateImpl(char[] internalNodes, DfaStateImpl<?>[] targetStates,
				M match, int stateNum)
		{
			super();
			m_internalNodes = internalNodes;
			m_targetStates = targetStates;
			m_match = match;
			m_stateNum = stateNum;
		}

		@Override
		void fixPlaceholderReferences()
		{
			for (int i=0;i < m_targetStates.length; ++i)
			{
				if (m_targetStates[i] != null)
				{
					m_targetStates[i] = m_targetStates[i].resolvePlaceholder();
				}
			}
		}

		@Override
		DfaStateImpl<M> resolvePlaceholder()
		{
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public DfaState<M> getNextState(char c)
		{
			int i=0;
			while(i<m_internalNodes.length)
			{
				i = i*2 + (c < m_internalNodes[i] ? 1 : 2);
			}
			return (DfaState<M>)m_targetStates[i-m_internalNodes.length];
		}

		@Override
		public M getMatch()
		{
			return m_match;
		}

        @Override
        public int getStateNumber()
        {
            return m_stateNum;
        }

        @Override
        public Iterable<DfaState<M>> getSuccessorStates()
        {
            return this;
        }

        @Override
        public Iterator<DfaState<M>> iterator()
        {
            return new TransitionArrayIterator<M>(m_targetStates);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void enumerateTransitions(DfaTransitionConsumer<M> consumer)
        {
            if (m_internalNodes.length<1)
            {
                if (m_targetStates[0]!=null)
                {
                    consumer.acceptTransition('\0', Character.MAX_VALUE,(DfaState<M>)m_targetStates[0]);
                }
                return;
            }
            int lastinternal = _enumInternal(consumer, 0,-1);
            char lastc = m_internalNodes[lastinternal];
            if (lastc >= Character.MAX_VALUE)
            {
                return;
            }
            DfaState<?> state = m_targetStates[lastinternal*2 + 2 - m_internalNodes.length];
            if (state != null)
            {
                consumer.acceptTransition(lastc, Character.MAX_VALUE, (DfaState<M>)state);
            }
        }
        
        @SuppressWarnings("unchecked")
        private int _enumInternal(final DfaTransitionConsumer<M> consumer, final int target, int previnternal)
        {
            int child = target*2+1; //left child of target
            if (child < m_internalNodes.length)
            {
                previnternal = _enumInternal(consumer, child, previnternal);
            }
            DfaState<?> state;
            int cfrom = (previnternal < 0 ? 0 : m_internalNodes[previnternal]);
            int cto = ((int)m_internalNodes[target])-1;
            //between adjacent internal nodes is a leaf
            if (previnternal > target)
            {
                state = m_targetStates[previnternal*2 + 2 - m_internalNodes.length];
            }
            else
            {
                state = m_targetStates[target*2 + 1 - m_internalNodes.length];
            }
            if (state != null && cfrom<=cto)
            {
                consumer.acceptTransition((char)cfrom, (char)cto, (DfaState<M>) state);
            }
            previnternal = target;
            ++child; // right child of target 
            if (child < m_internalNodes.length)
            {
                previnternal = _enumInternal(consumer, child, previnternal);
            }
            return previnternal;
        }
	}
	private static class TransitionArrayIterator<M> implements Iterator<DfaState<M>>
	{
	    private final DfaState<?>[] m_array;
	    private int m_pos;
	    
        TransitionArrayIterator(DfaState<?>[] array)
        {
            m_array = array;
            for (m_pos=0; m_pos<m_array.length && m_array[m_pos]==null; ++m_pos);
        }

        @Override
        public boolean hasNext()
        {
            return (m_pos < m_array.length);
        }

        @Override
        public DfaState<M> next()
        {
            @SuppressWarnings("unchecked")
            DfaState<M> ret = (DfaState<M>)m_array[m_pos];
            for (; m_pos<m_array.length && m_array[m_pos]==null; ++m_pos);
            return ret;
        }
	    
	}
}
