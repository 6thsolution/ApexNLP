package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.tokenization.StandardTokenizer;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */

public class EnglishTokenizer extends StandardTokenizer {
    @Override
    protected String normalize(String sentence) {
        return super.normalize(sentence)
                .replaceAll("(\\d+)(\\s+)(\\d+)", "$1 , $3")
                .replaceAll("(,)"," $1 ")
                .replaceAll("(in)(\\s+)(the)(\\s+)(evening|afternoon)", "$1_$3_$5")
                .replaceAll("(\\d+)(th)","")
//                .replaceAll("(\\s+)(from|starts)(\\s+)", "$1, $2$3")
                ;
    }
}
