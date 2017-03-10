package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.ner.ChunkedPart;

import org.threeten.bp.LocalDateTime;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface Extractor {
    void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart);
}
