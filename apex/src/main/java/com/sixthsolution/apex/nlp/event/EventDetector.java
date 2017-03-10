package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;

import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface EventDetector {

    Event detect(LocalDateTime source, List<ChunkedPart> chunkedParts);
}
