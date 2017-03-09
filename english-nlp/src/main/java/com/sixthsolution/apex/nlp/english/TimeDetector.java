package com.sixthsolution.apex.nlp.english;

import com.nobigsoftware.dfalex.Matchable;
import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.english.filter.TimeDetectionFilter;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.Arrays;
import java.util.List;

import static com.nobigsoftware.dfalex.Pattern.anyOf;
import static com.nobigsoftware.dfalex.Pattern.match;
import static com.nobigsoftware.dfalex.Pattern.maybe;
import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MERIDIEM;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_PREFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RELATIVE;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_SEPARATOR;
import static com.sixthsolution.apex.nlp.ner.Entity.TIME;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TimeDetector extends ChunkDetector {

    /**
     * @return returns noon, afternoon, etc.
     */
    private static Matchable time_relative() {
        return match(TIME_RELATIVE.toString());
    }

    /**
     * @return returns hh:mm am/pm
     */
    private static Matchable time_hour_min() {
        return match(NUMBER.toString()).thenMaybe(
                match(TIME_SEPARATOR.toString()).then(NUMBER.toString()))
                .thenMaybe(TIME_MERIDIEM.toString());
    }


    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                //#### FIXED TIME
                //(at) 10 (pm), (at) noon, (at) 09:30 (am), (at) 11:30
                newPattern(Label.FIXED_TIME,
                        maybe(TIME_PREFIX.toString()).then(
                                anyOf(time_relative(), time_hour_min()))
                                .thenMaybe(
                                        TIME_MERIDIEM.toString()))
        );
    }

    @Override
    protected List<? extends ChunkDetectionFilter> getFilters() {
        return Arrays.asList(new TimeDetectionFilter());
    }

    @Override
    protected Entity getEntity() {
        return TIME;
    }
}
