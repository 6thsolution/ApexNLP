package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ChunkDetectorAssertion {
    private static ChunkDetectorAssertion instance = null;
    private final ChunkDetector detector;
    private Tokenizer tokenizer;
    private Tagger tagger;

    private ChunkDetectorAssertion(Tokenizer tokenizer, Tagger tagger, ChunkDetector detector) {
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.detector = detector;
    }

    private static ChunkDetectorAssertion getInstance() {
        return instance;
    }

    public static void init(Tokenizer tokenizer, Tagger tagger, ChunkDetector detector) {
        instance = new ChunkDetectorAssertion(tokenizer, tagger, detector);
    }

    public static ChunkedPartAssertion assertChunkedPart(String sentence) {
        System.out.println("chunk detector for: " + sentence);
        long startTime = System.currentTimeMillis();
        ChunkedPartAssertion result = new ChunkedPartAssertion(instance.detector.detect(
                instance.tagger.tag(instance.tokenizer.tokenize(sentence))));
        System.out.println(
                "Detecting takes: " + (System.currentTimeMillis() - startTime) + " millis");
        return result;
    }

    public static class ChunkedPartAssertion {

        private final ChunkedPart chunkedPart;

        public ChunkedPartAssertion(ChunkedPart chunkedPart) {
            this.chunkedPart = chunkedPart;
        }

        public ChunkedPartAssertion entity(Entity entity) {
            assertTrue(chunkedPart.getEntity().equals(entity));
            return this;
        }


        public ChunkedPartAssertion label(Label label) {
            assertTrue(chunkedPart.getLabel().equals(label));
            return this;
        }

        public ChunkedPartAssertion text(String text) {
            StringBuilder sb = new StringBuilder();
            Iterator<TaggedWord> itr = chunkedPart.getTaggedWords().iterator();
            while (itr.hasNext()) {
                sb.append(itr.next().getWord());
                if (itr.hasNext()) {
                    sb.append(" ");
                }
            }
            assertEquals(text, sb.toString());
            return this;
        }
    }
}
