package com.nobigsoftware.dfalex;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.nobigsoftware.dfalex.CharRange;
import com.nobigsoftware.dfalex.DfaBuilder;
import com.nobigsoftware.dfalex.DfaState;
import com.nobigsoftware.dfalex.Pattern;

public class By3Test extends TestBase
{
    @Test
    public void test() throws Exception
    {
        //make pattern for whole numbers divisible by 3
        
        //digits mod 3
        Matchable d0=CharRange.anyOf("0369");
        Pattern d1=Pattern.match(CharRange.anyOf("147")).thenMaybeRepeat(d0);
        Pattern d2=Pattern.match(CharRange.anyOf("258")).thenMaybeRepeat(d0);
        
        Pattern Plus2 = Pattern.maybeRepeat(d1.then(d2)).then(Pattern.anyOf(
                d1.then(d1),
                d2
                ));
        Pattern Minus2 = Pattern.maybeRepeat(d2.then(d1)).then(Pattern.anyOf(
                d2.then(d2),
                d1
                ));
        
        Pattern By3 = Pattern.maybeRepeat(Pattern.anyOf(
                d0,
                d1.then(d2),
                Plus2.then(Minus2)
                ));
        DfaBuilder<Boolean> builder = new DfaBuilder<>();
        builder.addPattern(By3, true);
        DfaState<?> start = builder.build(Collections.singleton(Boolean.TRUE), null);
        Assert.assertEquals(3, _countStates(start));
        _checkDfa(start, "By3Test.out.txt", false);
    }
    

}
