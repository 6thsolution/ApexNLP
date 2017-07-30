package com.sixthsolution.apex.nlp.persian.test;

import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.PersianLocationDetector;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

//TODO check and fix location bugs
public class PersianDLTest extends PersianDetectorTest {

    @Override
    protected ChunkDetector provideDetector() {
        return new PersianLocationDetector();
    }

    @Test
    public void test_location() {
//        assertChunkedPart("در خانه").text("در خانه")
//                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("در خیابان دهم").text("در خیابان")
                .label(Label.LOCATION).entity(Entity.LOCATION);
//        assertChunkedPart("at 123 st.").text("at 123 st.")
//                .label(Label.LOCATION).entity(Entity.LOCATION);
    }

    @Test
    public void test_int_full_sentence() {
//        assertChunkedPart("Grocery shopping at Wegman's Thursday at 5pm").text("at Wegman's")
//                .label(Label.LOCATION).entity(Entity.LOCATION);
//        assertChunkedPart("Meet John at Mall from 9:30 to 12:00").text("at Mall")
//                .label(Label.LOCATION).entity(Entity.LOCATION);
//        assertChunkedPart("Bring Negin lunch at 123 st.").text("at 123 st.")
//                .label(Label.LOCATION).entity(Entity.LOCATION);
    }
}
