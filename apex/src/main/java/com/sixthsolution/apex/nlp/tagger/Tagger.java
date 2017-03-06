package com.sixthsolution.apex.nlp.tagger;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface Tagger {
    TaggedWords tag(String[] tokenizedSentence);
}
