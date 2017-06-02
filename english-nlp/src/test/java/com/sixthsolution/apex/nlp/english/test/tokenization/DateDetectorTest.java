package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.DateDetector;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.*;
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
        assertChunkedPart("2001-12-12").text("2001 - 12 - 12").label(FORMAL_DATE).entity(DATE);

    }

    @Test
    public void test_relax_date() {
        assertChunkedPart("april").text("april").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("apr 20").text("apr 20").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("april 20 th").text("april 20 th").label(RELAX_DATE).entity(DATE);
//        assertChunkedPart("april 20 2012").text("april 20 2012").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 april").text("20 april").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 th apr").text("20 th apr").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 of april").text("20 of april").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 apr 2012").text("20 apr 2012").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("monday").text("monday").label(RELAX_DATE).entity(DATE);

    }

    @Test
    public void test_relative_date(){
        assertChunkedPart("today").text("today").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("tomorrow").text("tomorrow").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("tonight").text("tonight").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next sunday").text("next sunday").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("2 monday from today").text("2 monday from today").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next april").text("next april").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next apr 20").text("next apr 20").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("20 day of next april").text("20 day of next april").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next spring").text("next spring").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("2 summer from today").text("2 summer from today").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("winter 2014").text("winter 2014").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next week third day").text("next week third day").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("four weeks from now").text("four weeks from now").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next year may 20th").text("next year may 20 th").label(RELATIVE_DATE).entity(DATE);

    }


    @Test
    public void test_global_date(){
        //add "the" to structure
        assertChunkedPart("day after tomorrow").text("day after tomorrow").label(GLOBAL_DATE).entity(DATE);
        assertChunkedPart("one week before sunday").text("one week before sunday").label(GLOBAL_DATE).entity(DATE);

    }


    @Test
    public void test_limited_date(){
//        assertChunkedPart("till next month").text("till next month").label(LIMITED_DATE).entity(DATE);
        assertChunkedPart("from sunday until june 20").text("from sunday until june 20").label(LIMITED_DATE).entity(DATE);

    }

    @Override
    protected ChunkDetector provideDetector() {
        return new DateDetector();
    }
}
