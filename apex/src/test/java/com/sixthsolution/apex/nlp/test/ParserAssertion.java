package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.parser.Parser;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ParserAssertion {
    private static ParserAssertion instance = null;
    private final Parser parser;
    private final LocalDateTime source;

    private ParserAssertion(LocalDateTime source, Parser parser) {
        parser.initialize();
        this.parser = parser;
        this.source = source;
    }

    public static void init(LocalDateTime source, Parser parser) {
        instance = new ParserAssertion(source, parser);
    }

    public static EventAssertion assertEvent(String sentence) {
        long startTime = System.currentTimeMillis();
        Event event = instance.parser.parse(instance.source, sentence);
        System.out.println(
                "Parsing takes: " + (System.currentTimeMillis() - startTime) + " millis");
        return new EventAssertion(event);
    }

    public static class EventAssertion {

        private final Event event;
        private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        public EventAssertion(Event event) {
            this.event = event;
        }

        public EventAssertion startTime(String exceptedFormat) {
            assertTime(event.start().toLocalTime(), exceptedFormat);
            return this;
        }

        public EventAssertion endTime(String exceptedFormat) {
            assertTime(event.end().toLocalTime(), exceptedFormat);
            return this;
        }

        private void assertTime(LocalTime actual, String exceptedFormat) {
            LocalTime excepted = LocalTime.parse(exceptedFormat);
            assertEquals(excepted.getHour(), actual.getHour());
            assertEquals(excepted.getMinute(), actual.getMinute());

        }

    }
}
