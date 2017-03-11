package com.sixthsolution.apex.nlp.english;

import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.english.filter.DateDetectionFilter;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.Arrays;
import java.util.List;

import static com.nobigsoftware.dfalex.Pattern.anyOf;
import static com.nobigsoftware.dfalex.Pattern.match;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.MONTH_NAME;
import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;
import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.FORMAL_DATE;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class DateDetector extends ChunkDetector {

    /**
     * @return 3/14/2016, 3/14/16, 03/14/16, 03/apr/2016
     */
    private static Pattern formal_date() {
        return match(NUMBER.toString()).then(DATE_SEPARATOR.toString())
                .then(anyOf(NUMBER.toString(), MONTH_NAME.toString()))
                .then(DATE_SEPARATOR.toString())
                .then(NUMBER.toString());
    }

    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                //#### FORMAL DATE
                newPattern(FORMAL_DATE, formal_date())
        );
    }

    @Override
    protected List<? extends ChunkDetectionFilter> getFilters() {
        return Arrays.asList(new DateDetectionFilter());
    }

    @Override
    protected Entity getEntity() {
        return DATE;
    }
}
