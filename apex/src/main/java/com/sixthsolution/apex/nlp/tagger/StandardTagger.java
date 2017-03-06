package com.sixthsolution.apex.nlp.tagger;

import com.sixthsolution.apex.nlp.dict.Dictionary;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.dict.Tags;
import com.sixthsolution.apex.nlp.ner.Entity;

import java.util.Arrays;
import java.util.List;

import static com.sixthsolution.apex.nlp.util.NumericUtils.isNumeric;
import static com.sixthsolution.apex.nlp.util.NumericUtils.toInt;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardTagger implements Tagger {

    protected final Dictionary dictionary;

    public StandardTagger(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public TaggedWords tag(String[] tokenizedSentence) {
        TaggedWords taggedWords = new TaggedWords();
        List<String> tokens = Arrays.asList(tokenizedSentence);
        for (String token : tokens) {
            Tags tags = null;
            if (isNumeric(token)) {
                tags = new Tags();
                tags.add(new TagValue(Tag.NUMBER, toInt(token), Entity.NONE));
            } else {
                tags = dictionary.getRelatedTags(token, true);
            }
            taggedWords.add(new TaggedWord(token, tags));
        }
        return taggedWords;
    }


}
