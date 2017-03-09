package com.sixthsolution.apex.nlp.parser;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Chunker;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class StandardParserBase implements Parser {

    private Tokenizer tokenizer = null;
    private Tagger tagger = null;
    private Chunker chunker = null;

    @Override
    public void initialize() {
        tokenizer = provideTokenizer();
        tagger = provideTagger();
        chunker = provideChunker();
    }

    protected abstract Tagger provideTagger();

    protected abstract Tokenizer provideTokenizer();

    protected abstract Chunker provideChunker();

    @Override
    public Event parse(String sentence) {
        //#1
        String[] tokens = tokenizer.tokenize(sentence);
        //#2
        TaggedWords taggedWords = tagger.tag(tokens);
        //#3
        List<ChunkedPart> chunkedParts = chunker.chunk(taggedWords);

        return null;
    }

}
