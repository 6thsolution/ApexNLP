package com.sixthsolution.apex.nlp.parser;

import com.sixthsolution.apex.model.Event;

import org.threeten.bp.LocalDateTime;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface Parser {

    void initialize();

    Event parse(LocalDateTime source, String sentence);
}
