package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.ner.Category;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;

import java.util.List;

import static com.sixthsolution.apex.nlp.ner.Category.DATE;
import static com.sixthsolution.apex.nlp.ner.Category.LOCATION;
import static com.sixthsolution.apex.nlp.ner.Category.TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ChunkAssertion {

    private final List<ChunkedPart> chunkedParts;

    public ChunkAssertion(List<ChunkedPart> chunks) {
        this.chunkedParts = chunks;
    }

    public ChunkAssertion hasLocationChunk(String loc) {
        assertChunk(loc, LOCATION);
        return this;
    }

    public ChunkAssertion hasNoLocationChunk() {
        assertEmpty(LOCATION);
        return this;
    }

    public ChunkAssertion hasTimeChunk(String time) {
        assertChunk(time, TIME);
        return this;
    }

    public ChunkAssertion hasNoTimeChunk() {
        assertEmpty(TIME);
        return this;
    }

    public ChunkAssertion hasDateChunk(String date) {
        assertChunk(date, DATE);
        return this;
    }
    private ChunkedPart getChunkedPartByType(Category type) {
        for (ChunkedPart part : chunkedParts) {
            if (part.getCategory().equals(type)) {
                return part;
            }
        }
        return null;
    }

    private void assertEmpty(Category type) {
        assertNull(getChunkedPartByType(type));
    }

    private void assertChunk(String phrase, Category type) {
        ChunkedPart chunk = getChunkedPartByType(type);
        assertNotNull(chunk);
        assertEquals(phrase, chunk.toStringTaggedWords());
    }

    @Override
    public String toString() {
        return chunkedParts.toString();
    }

    public void print() {
        System.out.println(toString());
    }
}
