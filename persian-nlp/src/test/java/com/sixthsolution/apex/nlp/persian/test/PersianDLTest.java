package com.sixthsolution.apex.nlp.persian.test;

import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.PersianLocationDetector;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

public class PersianDLTest extends PersianDetectorTest {

    @Override
    protected ChunkDetector provideDetector() {
        return new PersianLocationDetector();
    }

    @Test
    public void test_location() {
        assertChunkedPart("در کوچه شقایق").text("در کوچه شقایق")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("در خیابان دهم").text("در خیابان دهم")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("در بزرگراه چمران").text("در بزرگراه چمران")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("در بازار").text("در بازار")
                .label(Label.LOCATION).entity(Entity.LOCATION);

    }

    @Test
    public void test_int_full_sentence() {
        assertChunkedPart("خرید در بازار در روز چهارشنبه").text("در بازار")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("امشب ملاقات با دوستم در رستوران").text("در رستوران")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("خرید لباس در خیابان دهم").text("در خیابان دهم")
                .label(Label.LOCATION).entity(Entity.LOCATION);
    }
}
