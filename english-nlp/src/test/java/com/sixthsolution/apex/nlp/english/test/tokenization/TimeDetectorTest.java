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
import static com.sixthsolution.apex.nlp.ner.Label.RANGE_TIME;
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
        assertChunkedPart("in the evening").text("in_the_evening").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("6p_m").text("6 p_m").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("6p").text("6 p").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at 23:10").text("at 23 : 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at 8.20 pm").text("at 8 . 20 pm").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("23:10").text("23 : 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("at four").text("at four").label(FIXED_TIME).entity(TIME);
    }

    @Test
    public void test_invalid_fixed_time(){
        assertChunkedPart("7").noDetection();
        assertChunkedPart("12.2.2016").noDetection();
    }

    @Test
    public void test_range_time() {
        assertChunkedPart("from 5pm till 6pm").text("from 5 pm till 6 pm")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("at 5-6pm").text("at 5 - 6 pm")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("at nine till eleven").text("at nine till eleven")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("from 5pm to 6pm").text("from 5 pm to 6 pm")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("from 9:30 to 10:30").text("from 9 : 30 to 10 : 30")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("from morning - 9pm").text("from morning - 9 pm")
                .label(RANGE_TIME).entity(TIME);
        assertChunkedPart("from 11.5 - 12.5 ").text("from 11 . 5 - 12 . 5")
                .label(RANGE_TIME).entity(TIME);

    }

    @Test
    public void test_invalid_range_time() {
    }
}
