package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishTokenizer;
import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion;

import org.junit.Before;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class EnglishDetectorTest {
    @Before
    public void setUp() {
        ChunkDetectorAssertion.init(new EnglishTokenizer(),
                new StandardTagger(EnglishVocabulary.build()), provideDetector());
    }

    protected abstract ChunkDetector provideDetector();

}
