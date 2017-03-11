package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;

import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardEventDetector implements EventDetector {

    protected Extractor timeExtractor;
    protected Extractor dateExtractor;

    public StandardEventDetector() {
        timeExtractor = provideTimeExtractor();
        dateExtractor = provideDateExtractor();
    }

    @Override
    public Event detect(LocalDateTime source, List<ChunkedPart> chunkedParts) {
        EventBuilder builder = new EventBuilder();
        for (ChunkedPart part : chunkedParts) {
            switch (part.getEntity()) {
                case TIME:
                    timeExtractor.extract(builder, source, part);
                    break;
                case DATE:
                    dateExtractor.extract(builder, source, part);
                    break;
            }
        }
        return builder.build(source);
    }

    protected Extractor provideTimeExtractor() {
        return new StandardTimeExtractor();
    }

    protected Extractor provideDateExtractor() {
        return new StandardDateExtractor();
    }
}
