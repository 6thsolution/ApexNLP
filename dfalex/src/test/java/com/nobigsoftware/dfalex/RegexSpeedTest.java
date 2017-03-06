package com.nobigsoftware.dfalex;

import org.junit.Assert;
import org.junit.Test;

import backport.java.util.function.Function;

public class RegexSpeedTest extends TestBase
{
    private static final int SPINUP=1000;
    @Test
    public void notFoundReplaceTest() throws Exception
    {
        String patString = ("01235|/|456*1|abc|_|\\..*|013|0?1?2?3?4?57");
        String src;
        {
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<10000;i++)
            {
                sb.append("0123456789");
            }
            src = sb.toString();
        }
        
        int javaCount = timeJava(src, patString);
        int srCount = timeSearchAndReplaceBuilder(src, patString);
        int matcherCount = timeMatcher(src, patString);
        System.out.println("Search+Replace per second in 100K string, patterns not found:");
        System.out.format("Java Regex: %d    SearchAndReplaceBuilder: %d    StringMatcher: %d\n", javaCount, srCount, matcherCount);
    }
    
    int timeJava(String src, String patString)
    {
        int count=0;
        java.util.regex.Pattern javapat = java.util.regex.Pattern.compile(patString);
        long start = System.currentTimeMillis();
        String str = src; 
        for (long t = System.currentTimeMillis()-start;t < SPINUP+1000; t=System.currentTimeMillis()-start)
        {
            str = javapat.matcher(str).replaceAll("");
            if (t>=SPINUP)
            {
                ++count;
            }
        }
        Assert.assertEquals(src, str);
        return count;
    }
    int timeSearchAndReplaceBuilder(String src, String patString)
    {
        Function<String, String> replacer;
        {
            SearchAndReplaceBuilder builder=new SearchAndReplaceBuilder();
            builder.addReplacement(Pattern.regex(patString), (dest, srcStr, s, e) -> 0);
            replacer = builder.buildStringReplacer();
        }

        int count=0;
        long start = System.currentTimeMillis();
        String str = src; 
        for (long t = System.currentTimeMillis()-start;t < SPINUP+1000; t=System.currentTimeMillis()-start)
        {
            str = replacer.apply(str);
            if (t>=SPINUP)
            {
                ++count;
            }
        }
        Assert.assertEquals(src, str);
        return count;
    }
    
    int timeMatcher(String src, String patString)
    {
        DfaState<Boolean> startState;
        {
            DfaBuilder<Boolean> builder=new DfaBuilder<>();
            builder.addPattern(Pattern.regex(patString), true);
            startState = builder.build(null);
        }

        int count=0;
        long start = System.currentTimeMillis();
        for (long t = System.currentTimeMillis()-start;t < SPINUP+1000; t=System.currentTimeMillis()-start)
        {
            StringMatcher m = new StringMatcher(src);
            if (m.findNext(startState)!=null)
            {
                throw new RuntimeException("not supposed to find a match");
            }
            if (t>=SPINUP)
            {
                ++count;
            }
        }
        return count;
    }

}
