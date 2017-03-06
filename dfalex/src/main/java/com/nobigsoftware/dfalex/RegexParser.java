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

import java.io.Serializable;
import java.util.ArrayList;

import static backport.java.util.function.BackportFuncs.parseUnsignedInt;

/**
 * Parses regular expressions into {@link Matchable} implementations.
 * <p>
 * One would normally use {@link Pattern#regex(String)} or {@link Pattern#regexI(String)} instead
 * of using this class directly
 * <p>
 * Syntax supported includes:
 * <UL><LI>. (matches ANY character, including newlines)
 * </LI><LI> ?, +, *, |, ()
 * </LI><LI> [abc][^abc][a-zA-Z0-9], etc., character sets (
 * </LI><LI> \t, \n, \r, \f, \a, \e, &#92;xXX, &#92;uXXXX, \cX character escapes
 * </LI><LI> \\, or \x, where x is any non-alphanumeric character.  character escape for x
 * </LI><LI> \d, \D, \s, \S, \w, \W class escapes
 * </LI></UL>
 */
class RegexParser {
    public static CharRange DIGIT_CHARS = CharRange.DIGITS;
    public static CharRange NON_DIGIT_CHARS = DIGIT_CHARS.getComplement();
    public static CharRange SPACE_CHARS = CharRange.builder().addChars(" \t\n\r\f\u000B").build();
    public static CharRange NON_SPACE_CHARS = SPACE_CHARS.getComplement();
    public static CharRange WORD_CHARS = CharRange.builder()
            .addRange('a', 'z')
            .addRange('A', 'Z')
            .addRange('0', '9')
            .addChars("_")
            .build();
    public static CharRange NON_WORD_CHARS = WORD_CHARS.getComplement();
    private static final DfaState<Action> DFA = _buildParserDfa();
    private final boolean m_caseI;
    private final String m_src;
    private final CharRange.Builder m_charBuilder = CharRange.builder();
    private final StringBuilder m_symStack = new StringBuilder();
    private final ArrayList<Matchable> m_valStack = new ArrayList<>();
    private int m_readPos = 0;
    private char m_cprev, m_clast;
    private ArrayList<DfaState<Action>> m_stateStack = new ArrayList<>();

    private RegexParser(String str, boolean caseI) {
        m_src = str;
        m_caseI = caseI;
    }

    /**
     * Parse a regular expression
     *
     * @param str             a string containing the expression to parse
     * @param caseIndependent true to make it case-independent
     * @return a {@link Matchable} that implements the regular expression
     */
    public static Matchable parse(String str, boolean caseIndependent) {
        return (new RegexParser(str, caseIndependent))._parse();
    }

    //Build a DFA that matches a parse stack from the bottom to produce the next LR(1) action to perform
    //The stack is of the form XXX:ccc, where XXX are previously recognized symbols, and ccc are all the
    //remaining characters in the input.
    private static DfaState<Action> _buildParserDfa() //produces A
    {
        DfaBuilder<Action> bld = new DfaBuilder<>();
        //S can be the whole expression, or group contents
        Pattern Spos = Pattern.maybeRepeat(Pattern.maybeRepeat(CharRange.anyOf("SCA|")).then("("));

        //S: C | S '|' C
        bld.addPattern(Spos.then("C"), x -> x._push("S", x._pop(1)));
        bld.addPattern(Spos.then("S:|"), x -> x._push("|", null));
        bld.addPattern(Spos.then("S|C"), x -> {
            Matchable p1 = x._pop(2);
            Matchable p2 = x._pop(1);
            x._push("S", Pattern.anyOf(p1, p2));
        });
        Pattern Cpos = Spos.thenMaybe("S|");

        //C: e | C A
        bld.addPattern(Cpos, x -> x._push("C", Pattern.EMPTY));
        bld.addPattern(Cpos.then("CA"), x -> {
            Matchable p2 = x._pop(1);
            Matchable p1 = x._pop(1);
            x._push("C", Pattern.match(p1).then(p2));
        });
        Pattern Apos = Cpos.then("C");

        //A: A? | A+ | A*
        bld.addPattern(Apos.then("A:?"), x -> x._push("A", Pattern.maybe(x._pop(1))));
        bld.addPattern(Apos.then("A:+"), x -> x._push("A", Pattern.repeat(x._pop(1))));
        bld.addPattern(Apos.then("A:*"), x -> x._push("A", Pattern.maybeRepeat(x._pop(1))));

        //A: GROUP
        bld.addPattern(Apos.then(":("), x -> x._push("(", null));
        bld.addPattern(Apos.then("(S:)"), x -> x._push("A", x._pop(2)));

        //A: literal | .
        bld.addPattern(
                Apos.then(":").then(CharRange.builder().addChars(".()[]+*?|\\").invert().build()),
                x -> {
                    CharRange range;
                    char c = x._lastChar();
                    if (!x.m_caseI) {
                        range = CharRange.single(c);
                    } else {
                        char lc = Character.toLowerCase(c);
                        char uc = Character.toUpperCase(c);
                        if (lc == uc && lc == c) {
                            range = CharRange.single(c);
                        } else {
                            range = CharRange.builder().addChar(c).addChar(lc).addChar(uc).build();
                        }
                    }
                    x._push("A", range);
                });
        bld.addPattern(Apos.then(":."), x -> x._push("A", CharRange.ALL));

        final Pattern charEscape = Pattern.match(":\\").then(Pattern.anyOf(
                Pattern.match("x").then(CharRange.HEXDIGITS).then(CharRange.HEXDIGITS),
                Pattern.match("u")
                        .then(CharRange.HEXDIGITS)
                        .then(CharRange.HEXDIGITS)
                        .then(CharRange.HEXDIGITS)
                        .then(CharRange.HEXDIGITS),
                Pattern.match("c")
                        .then(CharRange.builder().addRange('a', 'z').addRange('A', 'Z').build()),
                CharRange.builder().addChars("xucdDwWsS").invert().build()));
        final Pattern classEscape = Pattern.match(":\\").then(Pattern.anyCharIn("dDsSwW"));

        bld.addPattern(Apos.then(charEscape),
                x -> x._push("A", CharRange.single(x._parseCharEscape())));
        bld.addPattern(Apos.then(classEscape), x -> x._push("A", x._parseClassEscape()));

        //A: [R] | [^R]
        bld.addPattern(Apos.then(":[^"), x -> {
            x.m_charBuilder.clear();
            x._push("[^", null);
        });
        bld.addPattern(Apos.then(":["), x -> {
            x.m_charBuilder.clear();
            x._push("[", null);
        });
        bld.addPattern(Apos.then("[R:]"), x -> {
            x._pop(2);
            if (x.m_caseI) {
                x.m_charBuilder.expandCases();
            }
            x._push("A", x.m_charBuilder.build());
        });
        bld.addPattern(Apos.then("[^R:]"), x -> {
            x._pop(3);
            if (x.m_caseI) {
                x.m_charBuilder.expandCases();
            }
            x._push("A", x.m_charBuilder.invert().build());
        });
        Pattern Rpos = Apos.then(Pattern.anyOf("[^", "["));

        //R: e | R classEscape | R c | R c - c
        bld.addPattern(Rpos, x -> x._push("R", null));
        bld.addPattern(Rpos.then("R").then(classEscape), x -> {
            x.m_charBuilder.addRange(x._parseClassEscape());
            x._pop(0);
        });
        bld.addPattern(Rpos.then("Rc"), x -> {
            x._pop(1);
            x.m_charBuilder.addRange(x.m_clast, x.m_clast);
        });
        bld.addPattern(Rpos.then("Rc:-"), x -> x._push("-", null));
        bld.addPattern(Rpos.then("Rc-c"), x -> {
            x._pop(3);
            if (x.m_clast < x.m_cprev) {
                x.m_charBuilder.addRange(x.m_clast, x.m_cprev);
            } else {
                x.m_charBuilder.addRange(x.m_cprev, x.m_clast);
            }
        });
        Pattern cpos = Rpos.then("R").thenMaybe("c-");

        //class chars
        bld.addPattern(cpos.then(":").then(CharRange.builder().addChars("-[]\\").invert().build()),
                x -> {
                    x.m_cprev = x.m_clast;
                    x.m_clast = x._lastChar();
                    x._push("c", null);
                });
        bld.addPattern(cpos.then(charEscape), x -> {
            x.m_cprev = x.m_clast;
            x.m_clast = x._parseCharEscape();
            x._push("c", null);
        });

        return bld.build(null);
    }

    private Matchable _parse() {
        m_stateStack.clear();
        m_valStack.clear();
        m_symStack.setLength(0);
        m_readPos = 0;
        m_stateStack.add(DFA);
        final int srclen = m_src.length();
        int maxpos = 0;
        for (; ; ) {
            //Match up to the end of the recognized symbol stack.  If we can't do
            //this, then there's a bug and we've reduced something we shouldn't have
            DfaState<Action> st = m_stateStack.get(m_stateStack.size() - 1);
            while (m_stateStack.size() - 1 < m_symStack.length()) {
                st = st.getNextState(m_symStack.charAt(m_stateStack.size() - 1));
                if (st == null) {
                    throw new RuntimeException(
                            "Internal bug encountered parsing regular expression: " + m_src);
                }
                m_stateStack.add(st);
            }
            //get the reduction action at the end of the symbol stack
            Action action = st.getMatch();

            //if we can lex and then reduce, do that instead
            DfaState<Action> lexState = st.getNextState(':');
            if (lexState != null && m_readPos < srclen) {
                for (int i = m_readPos; i < srclen; ++i) {
                    lexState = lexState.getNextState(m_src.charAt(i));
                    if (lexState == null) {
                        break;
                    }
                    maxpos = i + 1;
                    if (lexState.getMatch() != null) {
                        action = lexState.getMatch();
                        m_readPos = i + 1;
                    }
                }
            }

            if (action == null) {
                //no applicable reduction -- we're either done or there's an error
                break;
            }
            action.apply(this);
        }
        if (!"S".equals(m_symStack.toString())) {
            throw new IllegalArgumentException("Invalid regular expression: \"" +
                    m_src +
                    "\" has error at position " +
                    maxpos);
        }
        return m_valStack.get(0);
    }

    private void _push(String codes, Matchable pat) {
        m_symStack.append(codes);
        while (m_valStack.size() < m_symStack.length()) {
            m_valStack.add(pat);
        }
    }

    private Matchable _pop(int nonterms) {
        Matchable ret = null;
        m_symStack.setLength(m_symStack.length() - nonterms);
        while (m_valStack.size() > m_symStack.length()) {
            Matchable pat = m_valStack.remove(m_valStack.size() - 1);
            if (ret == null) {
                ret = pat;
            }
        }
        while (m_stateStack.size() - 1 > m_symStack.length()) {
            m_stateStack.remove(m_stateStack.size() - 1);
        }
        return ret;
    }

    private char _lastChar() {
        return m_src.charAt(m_readPos - 1);

    }

    private char _parseCharEscape() {
        int spos = m_readPos - 1;
        for (; spos > 0 && m_src.charAt(spos - 1) != '\\'; --spos) ;
        try {
            char c = m_src.charAt(spos);
            switch (c) {
                case 't':
                    return '\t';

                case 'n':
                    return '\n';

                case 'r':
                    return '\r';

                case 'f':
                    return '\f';

                case 'a':
                    return '\u0007';

                case 'e':
                    return '\u001B';

                case 'x':
                case 'u':
                    return (char) parseUnsignedInt(m_src.substring(spos + 1, m_readPos), 16);

                case 'c':
                    if (c >= 'A' && c <= 'Z') {
                        return (char) (c - 'A' + 1);
                    }
                    if (c >= 'a' && c <= 'z') {
                        return (char) (c - 'z' + 1);
                    }
                    break;

                default:
                    if (c == '_' || !WORD_CHARS.contains(c)) {
                        return c;
                    }
                    break;
            }
        } catch (Exception e) {
        }
        throw new RuntimeException("Invalid character escape \\" + m_src.substring(spos, spos + 1));
    }

    private CharRange _parseClassEscape() {
        int spos = m_readPos - 1;
        for (; spos > 0 && m_src.charAt(spos - 1) != '\\'; --spos) ;
        switch (m_src.charAt(spos)) {
            case 'd':
                return DIGIT_CHARS;

            case 'D':
                return NON_DIGIT_CHARS;

            case 's':
                return SPACE_CHARS;

            case 'S':
                return NON_SPACE_CHARS;

            case 'w':
                return WORD_CHARS;

            case 'W':
                return NON_WORD_CHARS;
        }
        throw new RuntimeException("Invalid class escape \\" + m_src.substring(spos, spos + 1));
    }

    private static interface Action extends Serializable {
        void apply(RegexParser parser);
    }
}
