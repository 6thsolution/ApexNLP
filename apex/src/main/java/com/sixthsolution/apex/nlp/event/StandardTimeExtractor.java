package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.util.Pair;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardTimeExtractor implements Extractor {

    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        switch (chunkedPart.getLabel()) {
            case FIXED_TIME:
                Pair<LocalTime, LocalTime> time = getFixedTime(source, chunkedPart);
                builder.setStartTime(time.first);
                builder.setEndTime(time.second);
                break;
        }
    }

    private Pair<LocalTime, LocalTime> getFixedTime(LocalDateTime source,
                                                    ChunkedPart chunkedPart) {
        int hour = -1, min = 0;
        for (TaggedWord taggedWord : chunkedPart.getTaggedWords()) {
            TagValue number = taggedWord.getTags().containsTagByValue(Tag.NUMBER);
            if (number == null) {
                number = taggedWord.getTags().containsTagByValue(Tag.TIME_RELATIVE);
            }
            if (number != null) {
                if (hour == -1) {
                    hour = (Integer) number.value;
                } else {
                    min = (Integer) number.value;
                }
            } else {
                TagValue med = taggedWord.getTags().containsTagByValue(Tag.TIME_MERIDIEM);
                if (med != null) {
                    hour += (Integer) med.value;
                }
            }
        }
        LocalTime startTime = source.toLocalTime().withHour(hour).withMinute(min);
        return new Pair<>(startTime, startTime.plusHours(1));
    }
}
