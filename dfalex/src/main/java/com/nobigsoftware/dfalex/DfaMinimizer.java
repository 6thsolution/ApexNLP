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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Converts a DFA into a minimal DFA using a fast variant of Hopcroft's algorithm
 */
class DfaMinimizer<RESULT>
{
	private final RawDfa<RESULT> m_origDfa;
	private final List<DfaStateInfo> m_origStates;
	private final int[] m_newStartStates;
	
	//map from transition target to all sources, using original state numbers
	private final int[][] m_origBackReferences;
	
	//for each original state, it's current partition number
	//during partitioning, partition numbers are the partition start position in m_partitionOrderStates
	//when that's done, they are contiguous ints
	private final int[] m_origOrderPartNums;
	
	//the original state numbers, sorted by partition number
	private final int[] m_partitionOrderStates;
	
	private final int[] m_origOrderHashes;
	private final int[] m_hashBuckets;
	private final int[] m_scratchSpace;
    private final int[] m_scratchPartitions;
	private final int m_hashTableSize;
	private final ArrayList<DfaStateInfo> m_minStates = new ArrayList<>();
	
	public DfaMinimizer(RawDfa<RESULT> dfa)
	{
		m_origDfa = dfa;
		m_origStates = dfa.getStates();
		m_origBackReferences = _createBackReferences();
		m_origOrderPartNums = new int[m_origStates.size()];
		m_partitionOrderStates = new int[m_origStates.size()];
		m_origOrderHashes = new int[m_origStates.size()];
		m_scratchSpace = new int[m_origStates.size()];
        m_scratchPartitions = new int[m_origStates.size()];
		m_hashTableSize = PrimeSizeFinder.findPrimeSize(m_origStates.size());
		m_hashBuckets = new int[m_hashTableSize];
		m_newStartStates = new int[m_origDfa.getStartStates().length];
		_createMinimalPartitions();
		_createNewStates();
	}
	
	public RawDfa<RESULT> getMinimizedDfa()
	{
		return new RawDfa<RESULT>(m_minStates, m_origDfa.getAcceptSets(), m_newStartStates);
	}
	
	private void _createNewStates()
	{
		m_minStates.clear();
		ArrayList<NfaTransition> m_tempTrans = new ArrayList<>();
		for (int i=0;i<m_partitionOrderStates.length;++i)
		{
			int statenum = m_partitionOrderStates[i];
			int partnum = m_origOrderPartNums[statenum];
			if (partnum < m_minStates.size())
			{
				continue;
			}
			assert(partnum == m_minStates.size());
			
			//compress and redirect transitions
			m_tempTrans.clear();
			final DfaStateInfo instate = m_origStates.get(statenum);
			final int inlen = instate.getTransitionCount();
			int inpos=0;
			while (inpos<inlen)
			{
				NfaTransition trans = instate.getTransition(inpos++);
				char startc = trans.m_firstChar;
				char endc = trans.m_lastChar;
				int dest = m_origOrderPartNums[trans.m_stateNum];
				for(;inpos < inlen;++inpos)
				{
					trans = instate.getTransition(inpos);
					if (trans.m_firstChar - endc > 1 || m_origOrderPartNums[trans.m_stateNum] != dest) 
					{
						break;
					}
					endc = trans.m_lastChar;
				}
				m_tempTrans.add(new NfaTransition(startc, endc, dest));
			}
			
			m_minStates.add(new DfaStateInfo(m_tempTrans, instate.getAcceptSetIndex()));
		}
		
		int [] origStartStates = m_origDfa.getStartStates();
		for (int i=0; i<m_newStartStates.length; ++i)
		{
			m_newStartStates[i] = m_origOrderPartNums[origStartStates[i]];
		}
	}
	
	private void _createMinimalPartitions()
	{
		if (m_partitionOrderStates.length<=0)
		{
			return;
		}
		//create initial partitioning
		//States in a partition are contiguous in m_partitionOrderStates
		//m_origOrderPartNums maps from state to partition number
		
        //initially, we just set these up so that states with different accept
		//sets will not compare equal
		for (int i=0;i<m_origOrderPartNums.length;++i)
		{
			m_origOrderPartNums[i] = m_origStates.get(i).getAcceptSetIndex();
			m_partitionOrderStates[i] = i;
		}
        //Then we repartition the whole state set, which will set m_origOrderPartNums and
		//m_partitionOrderStates properly and do an initial partitioning by previous
		//partition (accept set) AND transitions
		_repartition(0,m_partitionOrderStates.length);
		
		//from now on during partitioning, partition numbers are the partition start position in m_partitionOrderStates

		//We use this queue to keep track of all partitions that might need to split.  When it's
		//empty we are done.  Note that partition numbers are stable, since they are the index
		//of the partition's first state in m_partitionOrderStates
		IntRangeClosureQueue closureQ = new IntRangeClosureQueue(m_partitionOrderStates.length);
		
		//Initially, all of our partitions are new and need to be checked
		for (int i=0;i<m_partitionOrderStates.length;i++)
		{
			closureQ.add(m_origOrderPartNums[m_partitionOrderStates[i]]);
		}
		
		//split partitions as necessary until we're done
		int targetPart;
		while((targetPart = closureQ.poll())>=0)
		{
		    //find contiguous range in m_partitionOrderStates conrresponding to the target partition
			int targetEnd = targetPart+1;
			while(targetEnd < m_partitionOrderStates.length && m_origOrderPartNums[m_partitionOrderStates[targetEnd]] == targetPart)
			{
				++targetEnd;
			}
			//repartition it if necessary
			_repartition(targetPart, targetEnd);

			//queue other partitions.  Two states that were assumed to be equivalent,
			//because they had transitions into targetPart on the same character, might
			//now be recognizable as distict, because those transitions now go to different
			//partitions.
			//Any partition that transitions to two or more of our new partitions
			//needs to be queued for repartitioning
			
			//STEP 1: for each partition that transitions to the old target, remember
			//ONE of the new partitions it transitions to. 
			for (int i=targetPart; i<targetEnd; ++i)
			{
				int st=m_partitionOrderStates[i];
				int partNum = m_origOrderPartNums[st]; //NEW partition number for st!
				for(int src : m_origBackReferences[st])
				{
					int srcPart = m_origOrderPartNums[src];
					m_scratchSpace[srcPart] = partNum;
				}
			}
			//STEP 2: for each partition that transitions to the old target, see if any
			//of the new transitions it goes to are different from the one we remembered
			for (int i=targetPart; i<targetEnd; ++i)
			{
				int st=m_partitionOrderStates[i];
				int partNum = m_origOrderPartNums[st];
				for(int src : m_origBackReferences[st])
				{
					int srcPart = m_origOrderPartNums[src];
					if (m_scratchSpace[srcPart] != partNum)
					{
						closureQ.add(srcPart);
					}
				}
			}
		}
		
		//now renumber the partitions with contiguous ints instead of start positions
		int st = m_partitionOrderStates[0];
		int prevPartIn=m_origOrderPartNums[st];
		m_origOrderPartNums[st]=0;
		int prevPartOut=0;
		for (int i=1;i<m_partitionOrderStates.length;++i)
		{
			st = m_partitionOrderStates[i];
			int partIn = m_origOrderPartNums[st];
			if (partIn != prevPartIn)
			{
				prevPartIn = partIn;
				++prevPartOut;
			}
			m_origOrderPartNums[st]=prevPartOut;
		}
	}
	
	//given the start and end of a partition in m_partitionOrderStartStates, repartition it
	//into smaller partitions (equivalence classes) according to current information
	//each partition will end up with a number equal to its start position in m_partitionOrderStartStates
	private void _repartition(final int start, final int end)
	{
		if (end <= start)
		{
			return;
		}
		//hash all the states and initialize negated counts in the hash buckets
		for (int i=start;i<end;i++)
		{
			int state = m_partitionOrderStates[i]; 
			int h = _hashOrig(state);
			m_origOrderHashes[state] = h;
			int bucket = (h&Integer.MAX_VALUE)%m_hashTableSize;
			m_hashBuckets[bucket]=~0;
		}
		//calculate negated counts
		for (int i=start;i<end;i++)
		{
			int state = m_partitionOrderStates[i]; 
			int h = m_origOrderHashes[state];
			int bucket = (h&Integer.MAX_VALUE)%m_hashTableSize;
			m_hashBuckets[bucket]-=1;
		}
		//turn counts into start positions
		int totalLen=0;
		for (int i=start;i<end;i++)
		{
			int state = m_partitionOrderStates[i]; 
			int h = m_origOrderHashes[state];
			int bucket = (h&Integer.MAX_VALUE)%m_hashTableSize;
			int oldVal = m_hashBuckets[bucket];
			if (oldVal<0)
			{
				m_hashBuckets[bucket]=totalLen;
				totalLen+=~oldVal;
			}
		}
		assert(totalLen == end-start);
		//copy states in bucket order, turning start positions into end positions
		for (int i=start;i<end;i++)
		{
			int state = m_partitionOrderStates[i]; 
			int h = m_origOrderHashes[state];
			int bucket = (h&Integer.MAX_VALUE)%m_hashTableSize;
			int pos = m_hashBuckets[bucket]++;
			m_scratchSpace[pos]=state;
		}
		//copy bucket order back into partition order, separating different states in the same bucket
		int destpos=start;
		for (int bucketStart=0;bucketStart<totalLen;)
		{
			int state = m_scratchSpace[bucketStart];
			int hash = m_origOrderHashes[state];
			int bucket = (hash&Integer.MAX_VALUE)%m_hashTableSize;
			final int bucketEnd = m_hashBuckets[bucket];
			assert(destpos == bucketStart+start);
			m_partitionOrderStates[destpos++]=state;
			m_scratchPartitions[state]=bucketStart+start;
			int missPos = bucketStart;
			int nextPos = bucketStart+1;
			//add equivalent states in the same bucket
			for (;nextPos < bucketEnd;++nextPos)
			{
				int tempst = m_scratchSpace[nextPos];

				if (m_origOrderHashes[tempst]==hash && _compareOrig(tempst, state))
				{
					m_partitionOrderStates[destpos++]=tempst;
					m_scratchPartitions[tempst]=bucketStart+start;
				}
				else
				{
					m_scratchSpace[missPos++]=tempst;
				}
			}
			while(missPos > bucketStart)
			{
				m_scratchSpace[--nextPos] = m_scratchSpace[--missPos];
			}
			
			bucketStart=nextPos;
		}
		//all the counts line up and all states copied
		assert(destpos == end);
		
		//update partition numbers
		for (int i=start;i<end;++i)
		{
		    int state = m_partitionOrderStates[i];
		    m_origOrderPartNums[state] = m_scratchPartitions[state];
		}
	}

	//compute back-references for all states, for m_origBackReferences
	private int[][] _createBackReferences()
	{
		final int nstates = m_origStates.size();
		int[] backRefCounts = new int[m_origStates.size()];
		for (int st = 0; st < nstates; ++st)
		{
			m_origStates.get(st).forEachTransition(trans -> backRefCounts[trans.m_stateNum]++);
		}
		int[][] backrefs=new int[nstates][];
		for (int st = 0; st < nstates; ++st)
		{
			backrefs[st] = new int[backRefCounts[st]];
			backRefCounts[st]=0;
		}
		final int captureFix[] = new int[1]; //avoid making a new consumer for each st
		for (int st = 0; st < nstates; ++st)
		{
			captureFix[0] = st;
			m_origStates.get(st).forEachTransition(trans -> {
				int target = trans.m_stateNum;
				backrefs[target][backRefCounts[target]++] = captureFix[0];
			});
		}
		
		//dedup
		for (int st = 0; st < nstates; ++st)
		{
			int[] refs = backrefs[st];
			if (refs.length < 1)
			{
				continue;
			}
			Arrays.sort(refs);
			int newLen = 1;
			for (int s=1;s<refs.length;++s)
			{
				if (refs[s]!=refs[s-1])
				{
					refs[newLen++] = refs[s];
				}
			}
			if (newLen != refs.length)
			{
				backrefs[st] = Arrays.copyOf(refs, newLen);
			}
		}
		
		return backrefs;
	}

	//Make a hash of the original state, using its transitions and
	//the current partition
	private int _hashOrig(int st)
	{
		int h = m_origOrderPartNums[st]*5381;
		int nextc=0;
		int prevtarget=-1;
		final DfaStateInfo info = m_origStates.get(st);
		final int len = info.getTransitionCount();
		for (int i=0; i<len; i++)
		{
			NfaTransition trans = info.getTransition(i);
			int curtarget = m_origOrderPartNums[trans.m_stateNum]&0x7FFFFFFF;
			if (trans.m_firstChar != nextc && prevtarget != -1)
			{
				h*=65599;
				h+=nextc;
				h*=65599;
				prevtarget = -1;
			}
			if (curtarget != prevtarget)
			{
				h*=65599;
				h+=trans.m_firstChar;
				h*=65599;
				h+=curtarget+1;
				prevtarget = curtarget;
			}
			nextc = trans.m_lastChar+1;
		}
		if (nextc<0x10000 && prevtarget!=-1)
		{
			h*=65599;
            h+=nextc;
		}
        h*=65599;
		h^=(h>>16);
		h^=(h>>8);
		h^=(h>>4);
		h^=(h>>2);
		return h;
	}
	
    //Compare two original states to see if they're equivalent, as far as
	//we know based on the current partitioning and transitions
	private boolean _compareOrig(int st1, int st2)
	{
	    if (m_origOrderPartNums[st1] != m_origOrderPartNums[st2])
	    {
	        return false;
	    }
		final DfaStateInfo info1 = m_origStates.get(st1);
		final DfaStateInfo info2 = m_origStates.get(st2);
		final int len1 = info1.getTransitionCount();
		final int len2 = info2.getTransitionCount();
		if (len2 <= 0)
		{
			return (len1<=0);
		}
		if (len1 <=0)
		{
			return false;
		}
		int pos1 = 1, pos2 = 1;
		NfaTransition trans1 = info1.getTransition(0);
		NfaTransition trans2 = info2.getTransition(0);
		int nextc=0;
		for (;;)
		{
			while(trans1.m_lastChar < nextc)
			{
				if (pos1>=len1)
				{
					trans1 = null;
					break;
				}
				trans1 = info1.getTransition(pos1++);
			}
			while(trans2.m_lastChar < nextc)
			{
				if (pos2>=len2)
				{
					trans2 = null;
					break;
				}
				trans2 = info2.getTransition(pos2++);
			}
			if (trans1 == null || trans2 == null)
			{
				return trans1 == trans2;
			}
			if (trans1.m_firstChar > nextc || trans2.m_firstChar > nextc)
			{
				if (trans1.m_firstChar != trans2.m_firstChar)
				{
					return false;
				}
				nextc = trans1.m_firstChar;
			}
			if (m_origOrderPartNums[trans1.m_stateNum] != m_origOrderPartNums[trans2.m_stateNum])
			{
				return false;
			}
			nextc = Math.min(trans1.m_lastChar+1, trans2.m_lastChar+1);
		}
	}
}
