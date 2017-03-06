package com.nobigsoftware.dfalex;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

public class ReverseFinderTest extends TestBase
{
    @Test
    public void test() throws Exception
    {
        DfaBuilder<Boolean> revbuilder = new DfaBuilder<>();
        for (JavaToken tok : JavaToken.values())
        {
            revbuilder.addPattern(Pattern.ALL_STRINGS.then(tok.m_pattern.getReversed()), true);
        }
        DfaState<?> wantstart = revbuilder.build(null);
        String want = _toString(wantstart);
        
        DfaBuilder<JavaToken> builder = new DfaBuilder<>();
        for (JavaToken tok : JavaToken.values())
        {
            builder.addPattern(tok.m_pattern, tok);
        }
        DfaState<?> havestart = builder.buildReverseFinder();
        String have = _toString(havestart);
        Assert.assertEquals(want, have);
        
        //make sure we properly exclude the empty string from the reverse finder DFA
        builder.clear();
        for (JavaToken tok : JavaToken.values())
        {
            if ((tok.ordinal()&1)==0)
            {
                builder.addPattern(tok.m_pattern, tok);
            }
            else
            {
                builder.addPattern(Pattern.maybe(tok.m_pattern), tok);
            }
        }
        havestart = builder.buildReverseFinder();
        have = _toString(havestart);
        Assert.assertEquals(want, have);
    }
    
    private String _toString(DfaState<?> dfa)
    {
        StringWriter w = new StringWriter();
        m_printer.print(new PrintWriter(w), dfa);
        return w.toString();
    }

}
