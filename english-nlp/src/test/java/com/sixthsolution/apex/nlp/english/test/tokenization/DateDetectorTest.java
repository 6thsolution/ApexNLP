package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.DateDetector;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.*;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
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
        assertChunkedPart("april 20,2012").text("april 20 , 2012").label(RELAX_DATE).entity(DATE);
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
        assertChunkedPart("next sunday").text("next sunday").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("2 monday from_today").text("2 monday from_today").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next april").text("next april").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next apr 20").text("next apr 20").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next spring").text("next spring").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("2 summer from today").text("2 summer from today").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("winter 2014").text("winter 2014").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next week third day").text("next week third day").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("four weeks from_now").text("four weeks from_now").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("next year may 20th").text("next year may 20 th").label(RELATIVE_DATE).entity(DATE);
    }


    @Test
    public void test_global_date(){
        assertChunkedPart("day after tomorrow").text("day after tomorrow").label(GLOBAL_DATE).entity(DATE);
        assertChunkedPart("one week before sunday").text("one week before sunday").label(GLOBAL_DATE).entity(DATE);

    }


    @Test
    public void test_limited_date(){
        assertChunkedPart("till next month").text("till next month").label(LIMITED_DATE).entity(DATE);
        assertChunkedPart("from sunday until june 20").text("from sunday until june 20").label(LIMITED_DATE).entity(DATE);
        assertChunkedPart("from 7/15/2017 until 8/15/2017").text("from 7 / 15 / 2017 until 8 / 15 / 2017").label(LIMITED_DATE).entity(DATE);
    }

    @Test
    public void test_forever_date(){
        assertChunkedPart("every other day").text("every other day").label(FOREVER_DATE).entity(DATE);
        assertChunkedPart("every day").text("every day").label(FOREVER_DATE).entity(DATE);
        assertChunkedPart("every 2 sunday").text("every 2 sunday").label(FOREVER_DATE).entity(DATE);
        assertChunkedPart("every day until june").text("every day until june").label(FOREVER_DATE).entity(DATE);

    }
    @Test
    public void test_explicit_relative_date(){
//        assertChunkedPart("20th day of 1999").text("20 th day of 1999").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("the 20th day of two year from now").text("the 20 th day of two year from now").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("second month of this year").text("second month of this year").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("first week of april").text("first week of april").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("20th day of this month").text("20 th day of this month").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("100th day of the year 2020").text("100 th day of the year 2020").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("last week of april of next year").text("last week of april of next year").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("second day of next week").text("second day of next week").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("last day of second week of next month").text("last day of second week of next month").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("at the beginning of next year").text("at the beginning of next year").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("last day of second week of next month").text("last day of second week of next month").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("sunday of second week of next month").text("sunday of second week of next month").label(EXPLICIT_RELATIVE_DATE).entity(DATE);

    }
    @Override
    protected ChunkDetector provideDetector() {
        return new DateDetector();
    }
}
