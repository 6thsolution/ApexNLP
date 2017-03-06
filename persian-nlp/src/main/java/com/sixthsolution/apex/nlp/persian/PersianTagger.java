package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.nlp.dict.Dictionary;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import java.util.ListIterator;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

class PersianTagger extends StandardTagger {
    public PersianTagger(Dictionary dictionary) {
        super(dictionary);
    }


}
