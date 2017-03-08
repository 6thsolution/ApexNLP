package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishTokenizer;
import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
import com.sixthsolution.apex.nlp.english.TimeDetector;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion;

import org.junit.Before;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.TIME;
import static com.sixthsolution.apex.nlp.ner.Label.FIXED_TIME;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TimeDetectorTest {

    @Before
    public void setUp() {
        ChunkDetectorAssertion.init(new EnglishTokenizer(),
                new StandardTagger(EnglishVocabulary.build()), new TimeDetector());
    }

    @Test
    public void test_fixed_time() {
        assertChunkedPart("at 10 am").text("at 10 am").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at 10").text("at 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at noon").text("at noon").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("in the evening").text("in_the_evening").label(FIXED_TIME).entity(
                TIME);
        assertChunkedPart("6p_m").text("6 p_m").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("6p").text("6 p").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at 23:10").text("at 23 : 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("23:10").text("23 : 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at four").text("at four").label(FIXED_TIME).entity(TIME);
    }

}
