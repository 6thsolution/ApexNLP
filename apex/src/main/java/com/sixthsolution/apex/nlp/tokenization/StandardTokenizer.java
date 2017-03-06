package com.sixthsolution.apex.nlp.tokenization;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardTokenizer implements Tokenizer {

    private static final String REGEX = "([\\s]+)";

    @Override
    public String[] tokenize(String sentence) {
        return normalize(sentence).split(splitRegexRule());
    }

    protected String normalize(String sentence) {
        return sentence
                //useful for num/num/num or numPM num.numPM ...
                .replaceAll("(\\d+)", " $1 ")
                .replaceAll("(-|/)", " $1 ")
                .trim();
    }

    protected String splitRegexRule() {
        return REGEX;
    }
}
