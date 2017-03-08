package com.sixthsolution.apex.nlp.ner.regex;

import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class ChunkDetectionFilter {

    public abstract boolean accept(Label result, TaggedWords taggedWords, int startIndex, int endIndex);
}
