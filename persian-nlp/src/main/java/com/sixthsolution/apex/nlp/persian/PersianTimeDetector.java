package com.sixthsolution.apex.nlp.persian;

/**
 * Created by rozhin on 7/26/2017.
 */

import com.nobigsoftware.dfalex.Pattern;
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
import static com.sixthsolution.apex.nlp.dict.Tag.*;
import static com.sixthsolution.apex.nlp.ner.Entity.TIME;
import static com.sixthsolution.apex.nlp.ner.Label.FIXED_TIME;
import static com.sixthsolution.apex.nlp.ner.Label.RANGE_TIME;
import static com.sixthsolution.apex.nlp.ner.Label.RELATIVE_TIME;

import com.sixthsolution.apex.nlp.persian.filter.TimeDetectionFilter;


public class PersianTimeDetector extends ChunkDetector {

    /**
     * @return returns شب،ظهر،عصر...
     */
    private static Pattern time_relative() {
        return match(TIME_RELATIVE.toString());
    }

    /**
     * @return returns hh:mm ق.ظ/ب.ظ
     */
    private static Pattern time_hour_min() {
        return match(NUMBER.toString()).thenMaybe(
                match(TIME_SEPARATOR.toString()).then(NUMBER.toString()))
                .thenMaybe(anyOf(TIME_MERIDIEM.toString(),TIME_RELATIVE.toString()));
    }

    /**
     * @return  like time_hour_min but starts with در/ساعت
     */
    private static Pattern fixed_time() {
        return maybe(TIME_PREFIX.toString()).then(
                anyOf(time_relative(), time_hour_min()))
                .thenMaybe(
                        TIME_MERIDIEM.toString());
    }

    /**
     * @return از (time) تا (time)
     */
    private static Pattern range_time() {
        return match(match(maybe(TIME_START_RANGE.toString()).then(maybe(fixed_time())).then(TIME_RELATIVE_PREFIX.toString()).then(fixed_time())));
    }



    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(FIXED_TIME, fixed_time()),
                newPattern(RANGE_TIME, range_time())
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
