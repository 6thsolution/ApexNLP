package com.sixthsolution.apex.nlp.english;

import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.ner.Category;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.Arrays;
import java.util.List;

import static com.sixthsolution.apex.nlp.ner.Entity.TIME;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TimeDetector extends ChunkDetector {

    @Override
    protected List<Pair<Category, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(Category.FIXED_TIME, Pattern.)

        );
    }

    @Override
    protected List<ChunkDetectionFilter> getFilters() {
        return null;
    }

    @Override
    protected Entity getEntity() {
        return TIME;
    }
}
