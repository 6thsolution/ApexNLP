package com.nobigsoftware.dfalex;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test DFA cycles and destiny finding
 */
public class TarjanTest
{
    private Random r = new Random(0x4D54494D4D45524DL);
    private ArrayList<State> m_states = new ArrayList<>();
    private int[] m_cycleNumbers;

    @Test
    public void test() throws Exception
    {
        //tested up to 10000 once, but that takes a very long time (O(n^3))
        for (int n=0;n<500;n++)
        {
            double pcycle = r.nextDouble();
            pcycle*=pcycle*pcycle;
            double plink = r.nextDouble();
            double paccept = r.nextDouble();
            _randomDfa(n, pcycle, plink, paccept);
            _check();
        }
    }

    private void _randomDfa(final int nstates, double Pcycle, double Plink, double Paccept)
    {
        m_cycleNumbers = new int[nstates];
        m_states.clear();
        int cycleCounter=0;
        while(m_states.size()<nstates)
        {
            final int pos = m_states.size();
            int cycSz=1;
            if (r.nextDouble() < Pcycle)
            {
                cycSz = Math.min(r.nextInt(20)+1, nstates-pos);
            }
            for (int i=0;i<cycSz;++i)
            {
                Integer accept = null;
                if (r.nextDouble() < Paccept)
                {
                    accept = r.nextInt(8);
                }
                m_states.add(new State(pos+i, accept));
            }
            if (cycSz > 1)
            {
                for (int i=0;i<cycSz;++i)
                {
                    m_cycleNumbers[pos+i] = cycleCounter;
                    if (i!=0) {
                        m_states.get(pos+i).link(m_states.get(pos+i-1));
                    }
                }
                m_states.get(pos).link(m_states.get(pos+cycSz-1));
                ++cycleCounter;
            }
            else
            {
                m_cycleNumbers[pos] = -1;
            }
        }
        //link
        for (int pos=1;pos<nstates;++pos)
        {
            int nlinks = (int)Math.round(Plink * pos);
            for (int i=0;i<nlinks; i++)
            {
                int target = r.nextInt(pos);
                m_states.get(pos).link(m_states.get(target));
            }
        }
        for (int pos=0;pos<nstates;++pos)
        {
            m_states.get(pos).moveLink0(r);
        }
        return;
    }
    
    private void _check()
    {
        final int nstates = m_states.size();
        ArrayList<DfaState<Integer>> starts = new ArrayList<>();
        //find roots that cover all the states
        {
            boolean[] reached=new boolean[m_states.size()];
            for (int i=0; i<nstates; i++)
            {
                final State src = m_states.get(i);
                for (DfaState<Integer> dest : src.getSuccessorStates())
                {
                    if (m_cycleNumbers[src.m_number]!=m_cycleNumbers[dest.getStateNumber()])
                    {
                        assert(dest.getStateNumber() < src.m_number);
                        reached[dest.getStateNumber()] = true;
                    }
                }
            }
            for (int i = nstates-2;i>=0;--i)
            {
                if (m_cycleNumbers[i]>=0 && m_cycleNumbers[i]==m_cycleNumbers[i+1] && reached[i+1])
                {
                    reached[i]=true;
                }
            }
            for (int i=0;i<nstates;i++) {
                if (i==0 || m_cycleNumbers[i]<0 || m_cycleNumbers[i] != m_cycleNumbers[i-1])
                {
                    if (!reached[i])
                    {
                        starts.add(m_states.get(i));
                    }
                }
            }
        }
        DfaAuxiliaryInformation<Integer> auxinfo = new DfaAuxiliaryInformation<>(starts);
        
        int[] gotCycles = auxinfo.getCycleNumbers();
        Assert.assertEquals(nstates, gotCycles.length);
        for (int i=0;i<nstates;i++)
        {
            if (m_cycleNumbers[i]<0)
            {
                Assert.assertTrue(gotCycles[i]<0);
            }
            else
            {
                Assert.assertTrue(gotCycles[i]>=0);
                if (i>0)
                {
                    Assert.assertEquals(m_cycleNumbers[i]==m_cycleNumbers[i-1],gotCycles[i]==gotCycles[i-1]);
                }
            }
        }
    }

    private static class State extends DfaState<Integer>
    {
        final ArrayList<DfaState<Integer>> m_transitions = new ArrayList<>();
        final int m_number;
        final Integer m_accept;
        
        /**
         * Create a new State.
         * @param number
         * @param accept
         */
        public State(int number, Integer accept)
        {
            m_number = number;
            m_accept = accept;
        }
        
        public void link(DfaState<Integer> target)
        {
            m_transitions.add(target);
        }

        public void moveLink0(Random r)
        {
            if (m_transitions.size()>1)
            {
                int d = r.nextInt(m_transitions.size());
                if (d!=0)
                {
                    DfaState<Integer> t = m_transitions.get(d);
                    m_transitions.set(d, m_transitions.get(0));
                    m_transitions.set(0,t);
                }
            }
        }

        @Override
        public DfaState<Integer> getNextState(char c)
        {
            if (c<=m_transitions.size())
            {
                return m_transitions.get(c);
            }
            return null;
        }

        @Override
        public Integer getMatch()
        {
            return m_accept;
        }

        @Override
        public int getStateNumber()
        {
            return m_number;
        }

        @Override
        public void enumerateTransitions(DfaTransitionConsumer<Integer> consumer)
        {
            for (int i=0; i<m_transitions.size(); ++i)
            {
                consumer.acceptTransition((char)i, (char)i, m_transitions.get(i));
            }
        }

        @Override
        public Iterable<DfaState<Integer>> getSuccessorStates()
        {
            return m_transitions;
        }
    }
}
