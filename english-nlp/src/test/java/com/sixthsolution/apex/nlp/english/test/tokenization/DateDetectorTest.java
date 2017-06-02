package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.DateDetector;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.FORMAL_DATE;
import static com.sixthsolution.apex.nlp.ner.Label.RELAX_DATE;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class DateDetectorTest extends EnglishDetectorTest {

    @Test
    public void test_formal_date() {
        assertChunkedPart("3/14/2016").text("3 / 14 / 2016").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("3/14/16").text("3 / 14 / 16").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("03/14/16").text("03 / 14 / 16").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("03/apr/2016").text("03 / apr / 2016").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("12/12/16").text("12 / 12 / 16").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("2016/apr/21").text("2016 / apr / 21").label(FORMAL_DATE).entity(DATE);
//        assertChunkedPart("2001-12-12").text("2001 - 12 - 12").label(FORMAL_DATE).entity(DATE);

    }

    @Test
    public void test_relax_date() {
//        assertChunkedPart("20apr").text("20 apr").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("3/14/16").text("3 / 14 / 16").label(RELAX_DATE).entity(DATE);
//        assertChunkedPart("03/14/16").text("03 / 14 / 16").label(FORMAL_DATE).entity(DATE);
//        assertChunkedPart("03/apr/2016").text("03 / apr / 2016").label(FORMAL_DATE).entity(DATE);
//        assertChunkedPart("12/12/16").text("12 / 12 / 16").label(FORMAL_DATE).entity(DATE);
//        assertChunkedPart("2016/apr/21").text("2016 / apr / 21").label(FORMAL_DATE).entity(DATE);
////        assertChunkedPart("2001-12-12").text("2001 - 12 - 12").label(FORMAL_DATE).entity(DATE);

    }

    @Override
    protected ChunkDetector provideDetector() {
        return new DateDetector();
    }
}
