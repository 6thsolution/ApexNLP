package com.sixthsolution.apex.nlp.persian;
import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.filter.LocationDetectionFilter;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.Arrays;
import java.util.List;

import static com.nobigsoftware.dfalex.Pattern.match;
import static com.nobigsoftware.dfalex.Pattern.repeat;
import static com.sixthsolution.apex.nlp.ner.Entity.LOCATION;

/**
 * Created by rozhin on 7/30/2017.
 */

public class PersianLocationDetector extends ChunkDetector {

    /**
     * @return در_بازار
     */
    private static Pattern location() {
        return match(Tag.LOCATION_PREFIX.toString()).thenMaybe(Tag.LOCATION_NAME.toString()).thenRepeat(Tag.NONE.toString());
    }

    /**
     * @return at 123 st
     */
    private static Pattern address_location() {
        return match(Tag.LOCATION_PREFIX.toString()).then(Tag.LOCATION_SUFFIX.toString()).thenMaybe(repeat(Tag.NONE.toString()))
                .then(Tag.NUMBER.toString());
    }

    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(Label.LOCATION, location()),
                newPattern(Label.LOCATION, address_location())
        );
    }

    @Override
    protected List<? extends ChunkDetectionFilter> getFilters() {
        return Arrays.asList(new LocationDetectionFilter());
    }

    @Override
    protected Entity getEntity() {
        return LOCATION;
    }
}
