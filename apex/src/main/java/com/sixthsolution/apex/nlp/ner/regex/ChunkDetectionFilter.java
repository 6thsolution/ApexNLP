package com.sixthsolution.apex.nlp.ner.regex;

import com.sixthsolution.apex.nlp.ner.Category;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class ChunkDetectionFilter {

    public abstract boolean accept(Category result, TaggedWords taggedWords, int startIndex, int endIndex);
}
