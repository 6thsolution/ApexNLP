package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.ner.regex.RegExChunker;

import java.util.Arrays;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishChunker extends RegExChunker {

    public EnglishChunker() {
        super(Arrays.asList(new TimeDetector()));
    }

}
