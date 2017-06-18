package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.LocationDetector;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;

import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.ChunkDetectorAssertion.assertChunkedPart;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class LocationDetectorTest extends EnglishDetectorTest {

    @Override
    protected ChunkDetector provideDetector() {
        return new LocationDetector();
    }

    @Test
    public void test_location() {
        assertChunkedPart("at home").text("at home")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("at Starbucks").text("at Starbucks")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("at 123 st.").text("at 123 st.")
                .label(Label.LOCATION).entity(Entity.LOCATION);
    }

    @Test
    public void test_int_full_sentence() {
        assertChunkedPart("Grocery shopping at Wegman's Thursday at 5pm").text("at Wegman's")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("Meet John at Mall from 9:30 to 12:00").text("at Mall")
                .label(Label.LOCATION).entity(Entity.LOCATION);
        assertChunkedPart("Bring Negin lunch at 123 st.").text("at 123 st.")
                .label(Label.LOCATION).entity(Entity.LOCATION);
    }
}
