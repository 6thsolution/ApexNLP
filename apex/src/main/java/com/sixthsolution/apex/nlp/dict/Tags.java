package com.sixthsolution.apex.nlp.dict;

import com.sixthsolution.apex.nlp.ner.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class Tags extends ArrayList<TagValue> {

    public boolean containsTag(Tag tag) {
        Iterator<TagValue> iterator = iterator();
        while (iterator.hasNext()) {
            TagValue next = iterator.next();
            if (next.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTag(Tag... tags) {
        return containsTag(Arrays.asList(tags));
    }

    public boolean containsTag(Collection<Tag> tags) {
        Iterator<TagValue> iterator = iterator();
        while (iterator.hasNext()) {
            TagValue next = iterator.next();
            for (Tag tag : tags)
                if (next.tag.equals(tag)) {
                    return true;
                }
        }
        return false;
    }

    public boolean containsTagName(int tag) {
        for (TagValue tagValue : this) {
            if (tagValue.tag.id == tag) {
                return true;
            }
        }
        return false;
    }

    public TagValue getTagByEntity(Entity entity) {
        Iterator<TagValue> iterator = iterator();
        while (iterator.hasNext()) {
            TagValue next = iterator.next();
            if (next.entity == entity) {
                return next;
            }
        }
        return null;
    }
}
