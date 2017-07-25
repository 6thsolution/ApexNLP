package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

import static com.sixthsolution.apex.nlp.dict.Tag.*;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */

public class TimeDetectionFilter extends ChunkDetectionFilter {

    @Override
    public boolean accept(Label label, TaggedWords taggedWords, int startIndex, int endIndex) {
        switch (label) {
            case FIXED_TIME:
            //ignore single number
            if (startIndex == endIndex - 1 &&
                    taggedWords.get(startIndex).getTags().containsTag(NUMBER)) {
                return false;
            }
            //ignore date formats like 12.02.2012
            if (taggedWords.size() > endIndex &&
                    taggedWords.get(endIndex - 1).getTags().containsTag(NUMBER) &&
                    taggedWords.get(endIndex).getTags().containsTag(DATE_SEPARATOR)) {
                return false;
            }
            return true;
            case RANGE_TIME:
                //ignore like فروردین 13-17
                if (startIndex > 0 && taggedWords.get(startIndex - 1).hasTag(MONTH_NAME)) {
                    return false;
                }
                else if (startIndex > 0 && taggedWords.get(startIndex - 1).hasTag(DATE_SEPARATOR)) { // edited by scrc
                    return false;
                }
                return true;
            case RELATIVE_TIME:
                return true;
        }
        return false;
    }
}
