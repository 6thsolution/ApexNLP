package com.sixthsolution.apex.nlp.tagger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class TaggedWords extends ArrayList<TaggedWord> {

    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<TaggedWord> itr = iterator();
        while (itr.hasNext()) {
            sb.append(itr.next().getWord());
            if (itr.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
