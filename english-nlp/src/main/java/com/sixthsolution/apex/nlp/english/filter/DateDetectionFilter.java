package com.sixthsolution.apex.nlp.english.filter;

import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class DateDetectionFilter extends ChunkDetectionFilter {
    @Override
    public boolean accept(Label label, TaggedWords taggedWords, int startIndex, int endIndex) {
        switch (label) {
            case FORMAL_DATE:
                return true;
        }
        return false;
    }
}
