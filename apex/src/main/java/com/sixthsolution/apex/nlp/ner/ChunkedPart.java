package com.sixthsolution.apex.nlp.ner;

import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ChunkedPart {

    private final Entity entity;
    private final Category category;
    private final List<TaggedWord> taggedWords;

    public ChunkedPart(Entity entity,Category category,
                       List<TaggedWord> taggedWords) {
        this.entity =entity;
        this.category = category;
        this.taggedWords = taggedWords;
    }

    public Category getCategory() {
        return category;
    }

    public List<TaggedWord> getTaggedWords() {
        return taggedWords;
    }

    public String toStringTaggedWords() {
        StringBuilder sb = new StringBuilder();
        for (TaggedWord taggedWord : taggedWords) {
            sb.append(taggedWord.getWord()).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return category.name() + " -> " + toStringTaggedWords() ;
    }
}
