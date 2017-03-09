package com.sixthsolution.apex.nlp.english.filter;

import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TimeDetectionFilter extends ChunkDetectionFilter {

    @Override
    public boolean accept(Label label, TaggedWords taggedWords, int startIndex, int endIndex) {
        if (label.equals(Label.FIXED_TIME)) {
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
        }
        return false;
    }
}
