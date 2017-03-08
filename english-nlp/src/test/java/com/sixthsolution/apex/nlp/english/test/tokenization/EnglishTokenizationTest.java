package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.english.EnglishTokenizer;
import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.Chunker;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.ner.regex.RegExChunker;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;
import com.sixthsolution.apex.nlp.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.sixthsolution.apex.nlp.test.TokenizerAssertion.assertTokens;
import static com.sixthsolution.apex.nlp.test.TokenizerAssertion.init;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishTokenizationTest {

    @Before
    public void setUp() {
        init(new EnglishTokenizer());
    }

    @Test
    public void test_sentences() {
        assertTokens(
                "Pizza party on the 2nd Friday of every month at 1pm\n",
                "Pizza", "party", "on", "the", "2", "nd", "Friday", "of", "every", "month", "at",
                "1", "pm");

        assertTokens(
                "Mission Trip at Jakarta on Nov 13-17 calendar Church\n",
                "Mission", "Trip", "at", "Jakarta", "on", "Nov", "13", "-", "17", "calendar",
                "Church"
        );
        assertTokens("Go GYM 2.05.2013 19:00",
                "Go", "GYM", "2", ".", "05", ".", "2013", ",", "19", ":", "00");
    }
}
