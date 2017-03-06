package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.nlp.tokenization.StandardTokenizer;

/**
 * Created by rozhin on 2/15/2017.
 */
public class PersianTokenizer extends StandardTokenizer {


    @Override
    protected String normalize(String sentence) {
        return
                super.normalize(sentence)
                        .replaceAll("(چار|پنج|چهار|سه|دو|یک)(\\s)(شنبه)","$1\\_$3")
                        .replaceAll("(قبل|بعد)(\\s)(از)(\\s)(ظهر)","$1\\_$3\\_$5")
                        .replaceAll("(روز)(\\s)(ها)","$1\\_$3");


    }
}