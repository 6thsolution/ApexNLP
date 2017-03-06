package com.sixthsolution.apex.nlp.parser;

import com.sixthsolution.apex.model.Event;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public interface Parser {

    void initialize();

    Event parse(String sentence);
}
