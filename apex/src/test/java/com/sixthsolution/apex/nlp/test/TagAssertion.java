package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.tagger.TaggedWords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TagAssertion {

    private final TaggedWords taggedWords;


    TagAssertion(TaggedWords taggedWords) {
        this.taggedWords = taggedWords;
    }

    public TagAssertion hasTags(String regex) {

        return this;
    }
}
