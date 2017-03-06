package com.sixthsolution.apex.nlp.dict;

import com.sixthsolution.apex.nlp.ner.Entity;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
public class DictionaryBuilder {

    private Dictionary dictionary = new Dictionary();

    public Dictionary build() {
        return dictionary;
    }

    public TagEntryBuilder tag(Tag tag, Entity entity) {
        return new TagEntryBuilder(dictionary, tag, entity);
    }

    public static class TagEntryBuilder {
        private final Dictionary dictionary;
        private final Tag tag;
        private final Entity entity;

        TagEntryBuilder(Dictionary dictionary, Tag tag, Entity entity) {
            this.dictionary = dictionary;
            this.tag = tag;
            this.entity = entity;
        }

        public TagEntryBuilder e(Object value, String... words) {
            dictionary.addAll(words, tag, value, entity);
            return this;
        }

        public TagEntryBuilder e(String... words) {
            dictionary.addAll(words, tag, "", entity);
            return this;
        }
    }
}
