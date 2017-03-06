package com.sixthsolution.apex.nlp.ner.regex;

import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Chunker;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class RegExChunker implements Chunker {

    private final List<ChunkDetector> chunkDetectors;

    public RegExChunker(List<ChunkDetector> chunkDetectors) {
        this.chunkDetectors = chunkDetectors;
    }

    @Override
    public List<ChunkedPart> chunk(TaggedWords taggedWords) {
        TaggedWords clonedTaggedWords = (TaggedWords) taggedWords.clone();
        List<ChunkedPart> chunkedParts = new ArrayList<>();
        for (ChunkDetector detector : chunkDetectors) {
            ChunkedPart result = detector.detect(clonedTaggedWords);
            if (result != null) {
                chunkedParts.add(result);
            }
        }
        return chunkedParts;
    }
}
