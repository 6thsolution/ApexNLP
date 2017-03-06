package com.sixthsolution.apex.nlp.dict;

import com.sixthsolution.apex.nlp.ner.Entity;

import java.util.HashMap;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
public class Dictionary extends HashMap<String, Tags> {
    private static final Tags NONE_TAG;

    static {
        NONE_TAG = new Tags();
        NONE_TAG.add(new TagValue(Tag.NONE, "", Entity.NONE));
    }

    public void addAll(String[] words, Tag tag, Object value, Entity entity) {
        TagValue tagValue = new TagValue(tag, value, entity);
        for (String word : words) {
            update(word, tagValue);
        }
    }

    public void update(String word, TagValue tagValue) {
        Tags posting = getOrEmpty(word);
        posting.add(tagValue);
        put(word, posting);
    }

    public Tags getOrEmpty(String word) {
        if (!containsKey(word)) {
            return new Tags();
        }
        return get(word);
    }

    public Tags getRelatedTags(String word, boolean caseInsensitive) {
        if (caseInsensitive) {
            return getTags(word.toLowerCase());
        }
        return getTags(word);
    }

    private Tags getTags(String word) {
        Tags tags = getOrEmpty(word);
        if (!tags.isEmpty()) {
            return tags;
        }
        return NONE_TAG;
    }
    
}
