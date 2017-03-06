package com.nobigsoftware.dfalex;


import org.junit.Assert;
import org.junit.Test;

public class StringMatcherTest extends TestBase
{
    @Test
    public void testStringMatcher()
    {
        DfaState<Integer> dfa;
        {
            DfaBuilder<Integer> builder = new DfaBuilder<>();
            builder.addPattern(Pattern.regex("a[ab]*b"), 1);
            builder.addPattern(Pattern.regex("a[ab]*c"), 2);
            dfa = builder.build(null);
        }
        StringMatcher matcher = new StringMatcher("bbbbbaaaaaaaaaaaaaaaaaaaaaaaabbbbcaaaaaaabbbaaaaaaa");
        Integer result = matcher.findNext(dfa);
        Assert.assertEquals((Integer)2, result);
        Assert.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaabbbbc", matcher.getLastMatch());
        Assert.assertEquals(5, matcher.getLastMatchStart());
        Assert.assertEquals(34, matcher.getLastMatchEnd());
        result = matcher.findNext(dfa);
        Assert.assertEquals((Integer)1, result);
        Assert.assertEquals("aaaaaaabbb", matcher.getLastMatch());
        result = matcher.findNext(dfa);
        Assert.assertEquals(null, result);

        matcher.setPositions(15, 20, 33);
        Assert.assertEquals("aaaaa", matcher.getLastMatch());
        result = matcher.findNext(dfa);
        Assert.assertEquals("aaaaaaaaabbbb", matcher.getLastMatch());
        result = matcher.findNext(dfa);
        Assert.assertEquals(null, result);
    }
}
