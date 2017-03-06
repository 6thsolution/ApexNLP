package com.sixthsolution.apex.nlp.dict;

import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.util.Triple;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TagValue extends Triple<Tag, Object, Entity> {

    public Tag tag;
    public Object value;
    public Entity entity;

    public TagValue(Tag tag, Object value, Entity entity) {
        super(tag, value, entity);
        this.tag = tag;
        this.value = value;
        this.entity = entity;
    }
}
