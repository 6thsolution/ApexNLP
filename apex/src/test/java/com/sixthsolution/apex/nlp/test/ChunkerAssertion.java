package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.ner.Chunker;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ChunkerAssertion {

    private static ChunkerAssertion instance = null;
    private final Chunker chunker;
    private Tokenizer tokenizer;
    private Tagger tagger;

    private ChunkerAssertion(Tokenizer tokenizer, Tagger tagger, Chunker chunker) {
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.chunker = chunker;
    }

    private static ChunkerAssertion getInstance() {
        return instance;
    }

    public static void init(Tokenizer tokenizer, Tagger tagger, Chunker chunker) {
        instance = new ChunkerAssertion(tokenizer, tagger, chunker);
    }

    public static ChunkAssertion assertSentence(String word) {
        return getInstance().makeChunkAssertion(word);
    }

    private ChunkAssertion makeChunkAssertion(String word) {
        System.out.println("Category assertion for: " + word);
        long startTime = System.currentTimeMillis();
        TaggedWords taggedWords = tagger.tag(tokenizer.tokenize(word));
        ChunkAssertion result = new ChunkAssertion(chunker.chunk(taggedWords));
        System.out.println(
                "Chunking takes " + (System.currentTimeMillis() - startTime) + " millis.");
        System.out.println("------------------------------");
        return result;
    }


}
