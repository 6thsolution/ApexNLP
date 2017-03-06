package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishTokenizer;
import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;

import org.junit.Before;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.TaggerAssertion.assertSentence;
import static com.sixthsolution.apex.nlp.test.TaggerAssertion.init;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishTaggerTest {

    @Before
    public void setUp() throws Exception {
        init(new EnglishTokenizer(), new StandardTagger(EnglishVocabulary.build()));
    }

    @Test
    public void test() {
        assertSentence("party on monday 10").hasTags("N|PP|D_WD|NM");
    }
}
