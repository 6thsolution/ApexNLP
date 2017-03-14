package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.DateDetector;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.FORMAL_DATE;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class DateDetectorTest extends EnglishDetectorTest {

    @Test
    public void test_formal_date() {
        assertChunkedPart("12/12/16").text("12 / 12 / 16").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("2016/apr/21").text("2016 / apr / 21").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("2001-12-12").text("2001 - 12 - 12").label(FORMAL_DATE).entity(DATE);
    }

    @Override
    protected ChunkDetector provideDetector() {
        return new DateDetector();
    }
}
