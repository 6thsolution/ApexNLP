package com.sixthsolution.apex.nlp.parser;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class StandardParserBase implements Parser {

    private Tokenizer tokenizer = null;
    private Tagger tagger = null;

    @Override
    public void initialize() {
        tokenizer = provideTokenizer();
        tagger = provideTagger();
    }

    protected abstract Tagger provideTagger();

    protected abstract Tokenizer provideTokenizer();

    @Override
    public Event parse(String sentence) {
        //#1
        String[] tokens = tokenizer.tokenize(sentence);
        //#2
        TaggedWords taggedWords = tagger.tag(tokens);
        //#3
        return null;
    }

}
