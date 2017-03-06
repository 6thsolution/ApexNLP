package com.sixthsolution.apex.nlp.ner;

import com.sixthsolution.apex.nlp.tagger.TaggedWords;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface Chunker {

    List<ChunkedPart> chunk(TaggedWords taggedWords);
}
