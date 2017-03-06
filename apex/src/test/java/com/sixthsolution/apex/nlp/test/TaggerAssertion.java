package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public final class TaggerAssertion {

    private static TaggerAssertion instance = null;
    private Tokenizer tokenizer;
    private Tagger tagger;

    private TaggerAssertion(Tokenizer tokenizer, Tagger tagger) {
        this.tokenizer = tokenizer;
        this.tagger = tagger;
    }

    private static TaggerAssertion getInstance() {
        return instance;
    }

    public static void init(Tokenizer tokenizer, Tagger tagger) {
        instance = new TaggerAssertion(tokenizer, tagger);
    }

    public static TagAssertion assertSentence(String word) {
        return getInstance().makeTagAssertion(word);
    }

    private TagAssertion makeTagAssertion(String word) {
        TaggedWords taggedWords = tagger.tag(tokenizer.tokenize(word));
        return new TagAssertion(taggedWords);
    }
}
