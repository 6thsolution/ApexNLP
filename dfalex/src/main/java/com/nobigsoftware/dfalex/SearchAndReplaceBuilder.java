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
import java.util.List;
import java.util.Set;
import backport.java.util.function.Function;

import com.nobigsoftware.util.BuilderCache;

/**
 * Builds search and replace functions that finds patterns in strings and replaces them
 * <P>
 * Given a set of patterns and associated {@link StringReplacement} functions, you can produce an
 * optimized, thread-safe Function&lt;String,String&gt; that will find all occurrences of those patterns and replace
 * them with their replacements.
 * <P>
 * The returned function is thread-safe.
 * <P>
 * NOTE that building a search and replace function is a relatively complex procedure.  You should typically do it only once for each
 * pattern set you want to use.  Usually you would do this in a static initializer.
 * <P>
 * You can provide a cache that can remember and recall built functions, which allows you to build
 * them during your build process in various ways, instead of building them at runtime.  Or you can use
 * the cache to store built functions on the first run of your program so they don't need to be built
 * the next time...  But this is usually unnecessary, since building them is more than fast enough to
 * do during runtime initialization.
 */
public class SearchAndReplaceBuilder
{
    private final DfaBuilder<Integer> m_dfaBuilder;
    private final ArrayList<StringReplacement> m_replacements = new ArrayList<>();
    private DfaState<Integer> m_dfaMemo = null;
    private DfaState<Boolean> m_reverseFinderMemo = null;

    /**
     * Create a new SearchAndReplaceBuilder without a {@link BuilderCache}
     */
    public SearchAndReplaceBuilder()
    {
        m_dfaBuilder = new DfaBuilder<>();
    }
    
    /**
     * Create a new SearchAndReplaceBuilder, with a builder cache to bypass recalculation of pre-built functions
     * 
     * @param cache    The BuilderCache to use
     */
    public SearchAndReplaceBuilder(BuilderCache cache)
    {
        m_dfaBuilder = new DfaBuilder<>(cache);
    }
    
    /**
     * Reset this builder by forgetting all the patterns that have been added
     */
    public void clear()
    {
        _clearMemos();
        m_dfaBuilder.clear();
        m_replacements.clear();
    }
    
    
    /**
     * Add a search + string replacement.
     * <P>
     * Occurrences of the search pattern will be replaced with the given string.
     * <P>
     * This is equivalent to addReplacement(pat, StringReplacements.string(replacement));
     * 
     * @param pat   The pattern to search for
     * @param replacement   A function to generate the replacement value
     * @return this
     */
    public SearchAndReplaceBuilder addStringReplacement(Matchable pat, CharSequence replacement)
    {
        return addReplacement(pat, StringReplacements.string(replacement));
    }
    
    /**
     * Add a dynamic search + replacement.
     * <P>
     * The provided replacement function will be called to generate the replacement value for each
     * occurrence of the search pattern.
     * <P>
     * {@link StringReplacements} contains commonly used replacement functions
     * 
     * @param pat   The pattern to search for
     * @param replacement   A function to generate the replacement value
     * @return this
     */
    public SearchAndReplaceBuilder addReplacement(Matchable pat, StringReplacement replacement)
    {
        _clearMemos();
        Integer result = m_replacements.size();
        m_replacements.add(replacement);
        m_dfaBuilder.addPattern(pat, result);
        return this;
    }
    
    /**
     * Add a pattern to ignore
     * <P>
     * Occurrences of the search pattern will be left alone.  This just adds a replacer that
     * replaces occurrences of the search pattern with the same string.
     * <P>
     * With careful attention to match priority rules (see {@link #buildStringReplacer()}, this can be used for many
     * special purposes.
     * <P>
     * This is equivalent to addReplacement(pat, StringReplacements.IGNORE);
     * 
     * @param pat   The pattern to search for
     * @return this
     */
    public SearchAndReplaceBuilder addIgnorePattern(Matchable pat)
    {
        return addReplacement(pat, StringReplacements.IGNORE);
    }
    
    /**
     * Build a search and replace function
     * <P>
     * The resulting function finds all patterns in the string you give it, and replaces them all with
     * the associated replacement.
     * <P>
     * Matches are found in order of their start positions.  If matches to more than one pattern occur at the same position,
     * then the <i>longest</i> match will be used.  If there is a tie, then the first one added to this
     * builder will be used.
     *
     * @return The search+replace function
     */
    public Function<String,String> buildStringReplacer()
    {
        if (m_dfaMemo == null)
        {
            m_dfaMemo = m_dfaBuilder.build(SearchAndReplaceBuilder::ambiguityResolver);
        }
        if (m_reverseFinderMemo == null)
        {
            m_reverseFinderMemo = m_dfaBuilder.buildReverseFinder();
        }
        final StringSearcher<Integer> searcher = new StringSearcher<>(m_dfaMemo, m_reverseFinderMemo);
        final StringSearcherReplacer replacer = new StringSearcherReplacer(m_replacements);
        return (str -> searcher.findAndReplace(str, replacer));
    }
    
    /**
     * Build a search and replace function from a searcher and replacer
     * 
     * @param searcher the searcher
     * @param replacer the replacer
     * @return The search+replace function
     */
    public static <MR> Function<String,String> buildFromSearcher(StringSearcher<MR> searcher, ReplacementSelector<? super MR> replacer)
    {
        return (str -> searcher.findAndReplace(str, replacer));
    }
    
    private void _clearMemos()
    {
        m_dfaMemo = null;
        m_reverseFinderMemo = null;
    }
    private static Integer ambiguityResolver(Set<? extends Integer> candidates)
    {
        Integer ret = null;
        for (Integer c : candidates)
        {
            if (ret == null || c < ret)
            {
                ret = c;
            }
        }
        return ret;
    }
    
    private static class StringSearcherReplacer implements ReplacementSelector<Integer>
    {
        final StringReplacement[] m_replacements;
        
        public StringSearcherReplacer(List<StringReplacement> replacements)
        {
            m_replacements = replacements.toArray(new StringReplacement[replacements.size()]);
        }

        @Override
        public int apply(SafeAppendable dest, Integer mr, CharSequence src,
                int startPos, int endPos)
        {
            return m_replacements[mr].apply(dest, src, startPos, endPos);
        }
    }
}
