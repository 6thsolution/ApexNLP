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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest extends TestBase
{
    @Test
    public void testRegexParser() throws Exception
    {
        Matchable p1,p2;
        
        p1 = Pattern.anyOf("A","B");
        p2 = Pattern.regex("A|B");
        _check(p1,p2);
        
        p1 = Pattern.match("A").then(Pattern.anyOf("C","D"));
        p2 = Pattern.regex("A(C|D)");
        _check(p1,p2);
        
        p1 = Pattern.match("A").then(Pattern.anyOf("C","D")).then("B");
        p2 = Pattern.regex("A(C|D)B");
        _check(p1,p2);

        p1 = Pattern.match("A").thenMaybe(Pattern.anyOf("C","D")).then("B");
        p2 = Pattern.regex("A(C|D)?B");
        _check(p1,p2);
        
        p1 = Pattern.match("A").thenRepeat(Pattern.anyOf("C","D")).then("B");
        p2 = Pattern.regex("A(C|D)+B");
        _check(p1,p2);
        
        p1 = Pattern.match("A").thenMaybeRepeat(Pattern.anyOf("C","D")).then("B");
        p2 = Pattern.regex("A(C|D)*B");
        _check(p1,p2);
        
        p1 = Pattern.match("A").thenMaybeRepeat(Pattern.anyOf("C","D")).then("B");
        p2 = Pattern.regex("A(C|D)+?B");
        _check(p1,p2);
        
        p1 = Pattern.anyOf(Pattern.match("A").thenMaybeRepeat("B"), Pattern.match("C"));
        p2 = Pattern.regex("AB*|C");
        _check(p1,p2);
        
        p1 = Pattern.regex("\\s\\S\\d\\D\\w\\W");
        p2 = Pattern.regex("[ \\t\\n\\x0B\\f\\r][^ \\t\\n\\x0B\\f\\r][0-9][^0-9][a-zA-Z_0-9][^a-zA-Z_0-9]");
        _check(p1,p2);
        
        p1 = Pattern.regex("[^\\d][\\d]");
        p2 = Pattern.regex("[\\D][^\\D]");
        _check(p1,p2);
        
        p1 = Pattern.regex("[Cc][Aa][Tt][^0-9a-fA-F][^0-9a-f@-F]");
        p2 = Pattern.regexI("cAt[^\\da-f][^\\d@-F]");
        _check(p1,p2);
    }
    
    private void _check(Matchable pWant, Matchable pHave) throws Exception
    {
        String want = _pToString(pWant);
        String have = _pToString(pHave);
        if (!want.equals(have))
        {
            Assert.assertEquals(want, have);
        }
    }
    
    private String _pToString(Matchable p)
    {
        DfaBuilder<Boolean> builder = new DfaBuilder<>();
        builder.addPattern(p, true);
        DfaState<Boolean> dfa = builder.build(null);
        StringWriter w = new StringWriter();
        m_printer.print(new PrintWriter(w), dfa);
        return w.toString();
    }
}
