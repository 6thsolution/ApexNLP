package com.sixthsolution.apex.nlp.persian.test;


import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.PersianTokenizer;
import com.sixthsolution.apex.nlp.persian.PersianVocabulary;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion;

import org.junit.Before;

/**
 * Created by rozhin on 7/30/2017.
 */



public abstract class PersianDetectorTest {
    @Before
    public void setUp() {
        ChunkDetectorAssertion.init(new PersianTokenizer(),
                new StandardTagger(PersianVocabulary.build()), provideDetector());
    }

    protected abstract ChunkDetector provideDetector();

}
