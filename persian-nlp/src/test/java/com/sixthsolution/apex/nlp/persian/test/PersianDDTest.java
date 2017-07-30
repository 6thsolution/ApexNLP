package com.sixthsolution.apex.nlp.persian.test;

import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.PersianDateDetector;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.*;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */

public class PersianDDTest extends PersianDetectorTest {

    @Test
    public void test_formal_date() {
        assertChunkedPart("1396/8/9").text("1396 / 8 / 9").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("96/3/20").text("96 / 3 / 20").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("1398/تیر/16").text("1398 / تیر / 16").label(FORMAL_DATE).entity(DATE);
        assertChunkedPart("89-مهر-12").text("89 - مهر - 12").label(FORMAL_DATE).entity(DATE);

    }

    @Test
    public void test_relax_date() {
        assertChunkedPart("مهر").text("مهر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 تیر").text("20 تیر").label(RELAX_DATE).entity(DATE);
//        assertChunkedPart("اول مرداد").text("اول مرداد").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20ام تیر").text("20 ام تیر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("10 فروردین 59").text("10 فروردین 59").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("بیستم فروردین 75").text("بیستم فروردین 75").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("شنبه").text("شنبه").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("دوشنبه").text("دوشنبه").label(RELAX_DATE).entity(DATE);

    }

    @Test
    public void test_relative_date(){
//        assertChunkedPart("today").text("today").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("tomorrow").text("tomorrow").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next sunday").text("next sunday").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("2 monday from_today").text("2 monday from_today").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next april").text("next april").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next apr 20").text("next apr 20").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("next spring").text("next spring").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("2 summer from today").text("2 summer from today").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("winter 2014").text("winter 2014").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next week third day").text("next week third day").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("four weeks from_now").text("four weeks from_now").label(RELATIVE_DATE).entity(DATE);
//        assertChunkedPart("next year may 20th").text("next year may 20 th").label(RELATIVE_DATE).entity(DATE);
    }


    @Test
    public void test_global_date(){
//        assertChunkedPart(" دو روز بعد فردا").text("دو روز بعد فردا").label(GLOBAL_DATE).entity(DATE);
        assertChunkedPart("یک هفته قبل تیر").text("یک هفته قبل تیر").label(GLOBAL_DATE).entity(DATE);

    }


    @Test
    public void test_limited_date(){
        assertChunkedPart("تا پسفردا").text("تا پسفردا").label(LIMITED_DATE).entity(DATE);
        assertChunkedPart("از شنبه تا 20ام فروردین").text("از شنبه تا 20 ام فروردین").label(LIMITED_DATE).entity(DATE);
        assertChunkedPart("از 1395/2/1 تا 1396/5/7").text("از 1395 / 2 / 1 تا 1396 / 5 / 7").label(LIMITED_DATE).entity(DATE);
    }

    @Test
    public void test_forever_date(){
        assertChunkedPart("هر روز").text("هر روز").label(FOREVER_DATE).entity(DATE);
        assertChunkedPart("هر دو هفته").text("هر دو هفته").label(FOREVER_DATE).entity(DATE);
        assertChunkedPart("هر شنبه تا مرداد").text("هر شنبه تا مرداد").label(FOREVER_DATE).entity(DATE);

    }
    @Override
    protected ChunkDetector provideDetector() {
        return new PersianDateDetector();
    }
}
