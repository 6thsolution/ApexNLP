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
    public void test_relax_date()
    {
        assertChunkedPart("مهر").text("مهر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20 تیر").text("20 تیر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("یکم تیر").text("یکم تیر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("20ام تیر").text("20 ام تیر").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("10 فروردین 59").text("10 فروردین 59").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("بیستم فروردین 75").text("بیستم فروردین 75").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("شنبه").text("شنبه").label(RELAX_DATE).entity(DATE);
        assertChunkedPart("دوشنبه").text("دوشنبه").label(RELAX_DATE).entity(DATE);

    }

    @Test
    public void test_relative_date(){
        assertChunkedPart("امروز").text("امروز").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("پسفردا").text("پسفردا").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("شنبه بعدی").text("شنبه بعد").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("8 هفته از_امروز").text("8 هفته از_امروز").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("فروردین بعدی").text("فروردین بعدی").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("فروردین بعدی 20 ام").text("فروردین بعدی 20 ام").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("next spring").text("next spring").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("2 summer from today").text("2 summer from today").label(RELATIVE_DATE).entity(DATE);
////        assertChunkedPart("winter 2014").text("winter 2014").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("هفته بعدی").text("هفته بعدی").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("هفته بعدی سومین روز").text("هفته بعدی سومین روز").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("4 شنبه از_فردا").text("4 شنبه از_فردا").label(RELATIVE_DATE).entity(DATE);
        assertChunkedPart("سال بعدی سومین روز").text("سال بعدی سومین روز").label(RELATIVE_DATE).entity(DATE);
    }


    @Test
    public void test_global_date(){
        assertChunkedPart(" یک هفته بعد 1396/4/2").text("یک هفته بعد 1396 / 4 / 2").label(GLOBAL_DATE).entity(DATE);
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
    @Test
    public void test_explicit_relative_date(){
        assertChunkedPart("سومین روز سال").text("سومین روز سال").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("دومین ماه امسال").text("دومین ماه امسال").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("اولین هفته فروردین").text("اولین هفته فروردین").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("بیستمین روز این ماه").text("بیستمین روز این ماه").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("سومین روز از سال 2010").text("سومین روز از سال 2010").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("آخر فروردین").text("آخر فروردین").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("دومین روز از هفته بعدی").text("دومین روز از هفته بعدی").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("شنبه دومین هفته از فروردین امسال").text("شنبه دومین هفته از فروردین امسال").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("آخرین روز از دومین هفته از ماه بعد").text("آخرین روز از دومین هفته از ماه بعد").label(EXPLICIT_RELATIVE_DATE).entity(DATE);
        assertChunkedPart("اول سال بعد").text("اول سال بعد").label(EXPLICIT_RELATIVE_DATE).entity(DATE);

    }

    @Override
    protected ChunkDetector provideDetector() {
        return new PersianDateDetector();
    }
}
