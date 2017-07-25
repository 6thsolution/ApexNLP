package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import org.threeten.bp.LocalDateTime;

import java.util.Iterator;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 *
 */

public class StandardLocationExtractor implements Extractor {

    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        switch (chunkedPart.getLabel()) {
            case LOCATION:
                String location = getLocation(chunkedPart);
                builder.setLocation(location);
                break;
        }
    }

    private String getLocation(ChunkedPart chunkedPart) {
        StringBuilder sb = new StringBuilder();
        Iterator<TaggedWord> itr = chunkedPart.getTaggedWords().iterator();
        while (itr.hasNext()) {
            TaggedWord next = itr.next();
            if (!next.hasTag(Tag.LOCATION_PREFIX)) {
                sb.append(next.getWord());
                if (itr.hasNext()) {
                    sb.append(" ");
                }
            }

        }
        return sb.toString();
    }
}
