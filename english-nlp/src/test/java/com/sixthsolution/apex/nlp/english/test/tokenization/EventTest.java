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
    public void foo() {
        assertEvent("Grocery shopping at Wegmans Thursday at 5pm")
                .startTime("17:00")
                .endTime("18:00");
        assertEvent("12/09 Meet John at Mall from 9:30 to 12:00").
                startTime("09:30")
                .endTime("12:00");
    }

}
