package com.nobigsoftware.dfalex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class IntTest extends TestBase
{
    final PrettyPrinter m_printer = new PrettyPrinter();
    @Test
    public void testTo100K() throws Exception
    {
        DfaBuilder<Integer> builder = new DfaBuilder<>();
        for (int i=0;i<100000;++i)
        {
            builder.addPattern(Pattern.match(Integer.toString(i)), i%7);
        }
        long tstart = System.currentTimeMillis();
        DfaState<?> start = builder.build(null);
        int numstates = _countStates(start);
        long telapsed = System.currentTimeMillis() - tstart;
        System.out.printf("Mininmized 100000 numbers -> value mod 7 (down to %d states) in %1.3f seconds",
                numstates,telapsed*.001).println();
        Assert.assertEquals(null, StringMatcher.matchWholeString(start, ""));
        Assert.assertEquals(null, StringMatcher.matchWholeString(start, "100001"));
        for (int i=0;i<100000;++i)
        {
            Assert.assertEquals(i%7, StringMatcher.matchWholeString(start, Integer.toString(i)));
        }
        Assert.assertEquals(36, numstates);
    }
    
    @Test
    public void testSimultaneousLanguages()
    {
        DfaBuilder<Integer> builder = new DfaBuilder<>();
        for (int i=0;i<100000;++i)
        {
            if ((i%21)==0)
            {
                builder.addPattern(Pattern.match(Integer.toString(i)), 3);
            }
            else if ((i%3)==0)
            {
                builder.addPattern(Pattern.match(Integer.toString(i)), 1);
            }
            else if ((i%7)==0)
            {
                builder.addPattern(Pattern.match(Integer.toString(i)), 2);
            }
        }
        List<Set<Integer>> langs = new ArrayList<>();
        {
            HashSet<Integer> s1 = new HashSet<>();
            s1.add(1);s1.add(3);
            HashSet<Integer> s2 = new HashSet<>();
            s2.add(2);s2.add(3);
            langs.add(s1);
            langs.add(s2);
        }
        long tstart = System.currentTimeMillis();
        List<DfaState<Integer>> starts = builder.build(langs, null);
        DfaState<Integer> start3 = starts.get(0);
        DfaState<Integer> start7 = starts.get(1);
        int numstates = _countStates(start3,start7);
        long telapsed = System.currentTimeMillis() - tstart;
        System.out.printf("Mininmized 1000000 numbers -> divisible by 7 and 3 (down to %d states) in %1.3f seconds",
                numstates,telapsed*.001).println();
        for (int i=0;i<100000;++i)
        {
            if ((i%21)==0)
            {
                Assert.assertEquals((Integer)3, StringMatcher.matchWholeString(start3, Integer.toString(i)));
                Assert.assertEquals((Integer)3, StringMatcher.matchWholeString(start7, Integer.toString(i)));
            }
            else if ((i%3)==0)
            {
                Assert.assertEquals((Integer)1, StringMatcher.matchWholeString(start3, Integer.toString(i)));
                Assert.assertEquals(null, StringMatcher.matchWholeString(start7, Integer.toString(i)));
            }
            else if ((i%7)==0)
            {
                Assert.assertEquals(null, StringMatcher.matchWholeString(start3, Integer.toString(i)));
                Assert.assertEquals((Integer)2, StringMatcher.matchWholeString(start7, Integer.toString(i)));
            }
        }
        Assert.assertEquals(137, numstates);
    }
}
