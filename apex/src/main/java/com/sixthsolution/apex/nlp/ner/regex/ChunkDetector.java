package com.sixthsolution.apex.nlp.ner.regex;

import com.nobigsoftware.dfalex.DfaBuilder;
import com.nobigsoftware.dfalex.DfaState;
import com.nobigsoftware.dfalex.Pattern;
import com.nobigsoftware.dfalex.StringMatcher;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.dict.Tags;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.List;

import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public abstract class ChunkDetector {

    protected final DfaState<Label> state;
    protected final List<ChunkDetectionFilter> filters;

    public ChunkDetector() {
        filters = getFilters();
        state = createDFA();
    }

    private DfaState<Label> createDFA() {
        DfaBuilder<Label> builder = new DfaBuilder<>();
        for (Pair<Label, Pattern> pair : getPatterns()) {
            builder.addPattern(pair.second, pair.first);
        }
        return builder.build(null);
    }

    public ChunkedPart detect(TaggedWords taggedWords) {
        String sentence = convertTaggedWordsToCharSequence(taggedWords);
        StringMatcher matcher = new StringMatcher(sentence);
        Label result;
        while ((result = matcher.findNext(state)) != null) {
            int startIndex = matcher.getLastMatchStart();
            int endIndex = matcher.getLastMatchEnd();
            if (filters != null) {
                for (ChunkDetectionFilter filter : filters) {
                    if (filter.accept(result, taggedWords, startIndex, endIndex)) {
                        return new ChunkedPart(getEntity(), result,
                                taggedWords.subList(startIndex, endIndex));
                    }
                }
            } else {
                return new ChunkedPart(getEntity(), result,
                        taggedWords.subList(startIndex, endIndex));
            }
        }
        return null;
    }

    private String convertTaggedWordsToCharSequence(TaggedWords taggedWords) {
        StringBuilder sb = new StringBuilder();
        for (TaggedWord taggedWord : taggedWords) {
            Tag tag = getNearestTag(taggedWord.getTags());
            sb.append((char) tag.id);
        }
        return sb.toString();
    }

    private Tag getNearestTag(Tags tags) {
        TagValue tagValue = tags.getTagByEntity(getEntity());
        if (tagValue != null) {
            return tagValue.tag;
        }
        if (tags.containsTag(NUMBER)) {
            return Tag.NUMBER;
        }
        return Tag.NONE;
    }

    protected abstract List<Pair<Label, Pattern>> getPatterns();

    protected abstract List<ChunkDetectionFilter> getFilters();

    protected abstract Entity getEntity();

    public static Pair<Label, Pattern> newPattern(Label label, Pattern pattern) {
        return new Pair<>(label, pattern);
    }
}
