package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.event.EventDetector;
import com.sixthsolution.apex.nlp.event.StandardEventDetector;
import com.sixthsolution.apex.nlp.ner.Chunker;
import com.sixthsolution.apex.nlp.ner.regex.RegExChunker;
import com.sixthsolution.apex.nlp.parser.StandardParserBase;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import java.util.Arrays;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishParser extends StandardParserBase {

    @Override
    public void initialize() {
        super.initialize();
        //TODO need some training sentences
    }

    @Override
    protected Tagger provideTagger() {
        return new StandardTagger(EnglishVocabulary.build());
    }

    @Override
    protected Tokenizer provideTokenizer() {
        return new EnglishTokenizer();
    }

    @Override
    protected Chunker provideChunker() {
        return new RegExChunker(Arrays.asList(new TimeDetector(), new DateDetector()));
    }

    @Override
    protected EventDetector provideEventDetector() {
        return new StandardEventDetector();
    }
}
