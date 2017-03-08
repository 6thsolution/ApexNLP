package com.sixthsolution.apex.nlp.ner;

import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ChunkedPart {

    private final Entity entity;
    private final Label label;
    private final List<TaggedWord> taggedWords;

    public ChunkedPart(Entity entity,Label label,
                       List<TaggedWord> taggedWords) {
        this.entity =entity;
        this.label = label;
        this.taggedWords = taggedWords;
    }

    public Label getLabel() {
        return label;
    }

    public Entity getEntity() {
        return entity;
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
        return label.name() + " -> " + toStringTaggedWords() ;
    }
}
