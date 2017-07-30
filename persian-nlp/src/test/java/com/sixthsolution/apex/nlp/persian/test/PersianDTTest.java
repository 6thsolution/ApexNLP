package com.sixthsolution.apex.nlp.persian.test;

/**
 * Created by rozhin on 7/30/2017.
 */

import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import com.sixthsolution.apex.nlp.persian.PersianTimeDetector;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.ner.Entity.TIME;
import static com.sixthsolution.apex.nlp.ner.Label.FIXED_TIME;
import static com.sixthsolution.apex.nlp.ner.Label.RANGE_TIME;
import static com.sixthsolution.apex.nlp.ner.Label.RELATIVE_TIME;
import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class PersianDTTest extends PersianDetectorTest {

    //TODO add every x hours

    @Test
    public void test_fixed_time() {
        assertChunkedPart("ساعت 10").text("ساعت 10").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("ساعت 10 ق.ظ").text("ساعت 10 ق.ظ").label(FIXED_TIME).entity(TIME);
//        assertChunkedPart("ساعت 10 صبح").text("ساعت 10 صبح").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("عصر").text("عصر").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("7 ب.ظ").text("7 ب.ظ").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("در 23:20").text("در 23 : 20").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("در 23:20 ب.ظ").text("در 23 : 20 ب.ظ").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("8.20").text("8 . 20").label(FIXED_TIME).entity(TIME);
        assertChunkedPart("ساعت چهار").text("ساعت چهار").label(FIXED_TIME).entity(TIME);
    }

    @Test
    public void test_invalid_fixed_time(){
        assertChunkedPart("7").noDetection();
        assertChunkedPart("12.2.2016").noDetection();
    }

    @Test
    public void test_range_time() {
//        assertChunkedPart("از 4 ب.ظ تا 12").text("از 4 ب.ظ تا 12")
//                .label(RANGE_TIME).entity(TIME);

    }

    @Test
    public void test_relative_time() {
//        assertChunkedPart("تا یک ساعت").text("تا یک ساعت")
//                .label(RELATIVE_TIME).entity(TIME);

    }

    @Override
    protected ChunkDetector provideDetector() {
        return new PersianTimeDetector();
    }
}
