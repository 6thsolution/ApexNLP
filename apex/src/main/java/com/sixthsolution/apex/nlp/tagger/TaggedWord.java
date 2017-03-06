package com.sixthsolution.apex.nlp.tagger;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.Tags;

import java.util.Collection;
import java.util.Set;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TaggedWord {

    private Tags tags;
    private final String word;

    public TaggedWord(String word) {
        this.word = word;
        this.tags = new Tags();
    }

    public TaggedWord(String word, Tags tags) {
        this.word = word;
        this.tags = tags;
    }

    public String getWord() {
        return word;
    }

    public Tags getTags() {
        return tags;
    }

    public boolean hasTag(Collection<Tag> validTags) {
        return getTags().containsTag(validTags);
    }

    @Override
    public String toString() {
        return "TaggedWord{" +
                "word='" + word + '\'' +
                ", tags=" + tags +
                '}';
    }

}
