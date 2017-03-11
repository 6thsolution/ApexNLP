package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishParser;

import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;

import static com.sixthsolution.apex.nlp.test.ParserAssertion.assertEvent;
import static com.sixthsolution.apex.nlp.test.ParserAssertion.init;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EventTest {

    @Before
    public void setUp() {
        init(LocalDateTime.of(2017, 4, 10, 9, 30), new EnglishParser());
    }

    @Test
    public void validate_time_extractor() {
        assertEvent("in the evening").startTime("16:00").endTime("17:00");
        assertEvent("from morning - 9 pm").startTime("09:00").endTime("21:00");
        assertEvent("from 5pm till 6pm").startTime("17:00").endTime("18:00");
        assertEvent("at 5-6pm").startTime("17:00").endTime("18:00");
        assertEvent("at nine till eleven").startTime("09:00").endTime("11:00");
        assertEvent("from 5am to 6pm").startTime("05:00").endTime("18:00");
        assertEvent("from 9:30 to 10:30").startTime("09:30").endTime("10:30");
        //TODO no idea how to support
        //        assertEvent("from 11.5 - 12.5 ").startTime("11:")
    }

    @Test
    public void test_full_sentence() {
        assertEvent("Grocery shopping at Wegmans Thursday at 5pm")
                .startTime("17:00")
                .endTime("18:00");
        assertEvent("12/09 Meet John at Mall from 9:30 to 12:00").
                startTime("09:30")
                .endTime("12:00");
        assertEvent("Meet John on monday at Mall")
                .startTime("09:30")
                .endTime("10:30");
        assertEvent("Family Dine Out on the 2nd Friday of every month at 6-9p")
                .startTime("18:00")
                .endTime("21:00");
        assertEvent("Mission Trip at Jakarta on Nov 13-17 calendar Church")
                .startTime("09:30")
                .endTime("10:30");
        assertEvent("Wash the Car at Mall on march 19 at 8.45pm 12/12/12")
                .startTime("20:45")
                .endTime("21:45");
    }

}
