package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.parser.Parser;

import org.threeten.bp.LocalDate;
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
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        public EventAssertion(Event event) {
            this.event = event;
        }

        public EventAssertion start(String expectedAsString) {
            assertDateTime(event.start(), expectedAsString);
            return this;
        }

        public EventAssertion end(String expectedAsString) {
            assertDateTime(event.end(), expectedAsString);
            return this;
        }

        private void assertDateTime(LocalDateTime actual, String expectedAsString) {
            LocalDateTime expected = LocalDateTime.parse(expectedAsString, formatter);
            assertEquals(expected.getYear(), actual.getYear());
            assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
            assertEquals(expected.getMonthValue(), actual.getMonthValue());
            assertEquals(expected.getHour(), actual.getHour());
            assertEquals(expected.getMinute(), actual.getMinute());
        }

        public EventAssertion startTime(String expectedAsString) {
            assertTime(event.start().toLocalTime(), expectedAsString);
            return this;
        }

        public EventAssertion endTime(String expectedAsString) {
            assertTime(event.end().toLocalTime(), expectedAsString);
            return this;
        }

        private void assertTime(LocalTime actual, String expectedAsString) {
            LocalTime expected = LocalTime.parse(expectedAsString);
            assertEquals(expected.getHour(), actual.getHour());
            assertEquals(expected.getMinute(), actual.getMinute());

        }

        public EventAssertion startDate(String expectedAsString) {
            assertDate(event.start().toLocalDate(), expectedAsString);
            return this;
        }

        public EventAssertion endDate(String expectedAsString) {
            assertDate(event.end().toLocalDate(), expectedAsString);
            return this;
        }

        private void assertDate(LocalDate actual, String expectedAsString) {
            LocalDate expected = LocalDate.parse(expectedAsString);
            assertEquals(expected.getYear(), actual.getYear());
            assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
            assertEquals(expected.getMonthValue(), actual.getMonthValue());

        }
    }
}
