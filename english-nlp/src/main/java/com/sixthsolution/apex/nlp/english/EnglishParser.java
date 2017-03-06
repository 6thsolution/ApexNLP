package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.parser.StandardParserBase;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishParser extends StandardParserBase {

    @Override
    protected Tagger provideTagger() {
        return new StandardTagger(EnglishVocabulary.build());
    }

    @Override
    protected Tokenizer provideTokenizer() {
        return new EnglishTokenizer();
    }
}
