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
            case RANGE_TIME:
                Pair<LocalTime, LocalTime> time = getFixedOrRangeTime(source, chunkedPart);
                builder.setStartTime(time.first);
                builder.setEndTime(time.second);
                break;

        }
    }

    private Pair<LocalTime, LocalTime> getFixedOrRangeTime(LocalDateTime source,
                                                           ChunkedPart chunkedPart) {
        int startHour = -1, startMin = 0;
        int endHour = -1, endMin = 0;
        boolean switchToEnd = false;
        boolean appliedMeridiemToStartHour = false;
        for (TaggedWord taggedWord : chunkedPart.getTaggedWords()) {
            TagValue number = taggedWord.getTags().containsTagByValue(Tag.NUMBER);
            if (number == null) {
                number = taggedWord.getTags().containsTagByValue(Tag.TIME_RELATIVE);
                if (number != null && startHour == -1) {
                    appliedMeridiemToStartHour = true;
                }
            }
            if (number != null) {
                int numberVal = (Integer) number.value;
                if (!switchToEnd && startHour == -1) {
                    startHour = numberVal;
                } else if (!switchToEnd) {
                    startMin = numberVal;
                } else if (switchToEnd && endHour == -1) {
                    endHour = numberVal;
                } else if (switchToEnd) {
                    endMin = numberVal;
                }
            } else {
                TagValue med = taggedWord.getTags().containsTagByValue(Tag.TIME_MERIDIEM);
                if (med != null) {
                    if (!switchToEnd && startHour <= 12) {
                        startHour += (Integer) med.value;
                        appliedMeridiemToStartHour = true;
                    } else if (endHour <= 12) {
                        endHour += (Integer) med.value;
                        if (!appliedMeridiemToStartHour && startHour <= 12) {
                            startHour += (Integer) med.value;
                        }
                    }
                } else {
                    TagValue rangeSwitch = taggedWord.getTags().containsTagByValue(Tag.TIME_RANGE);
                    if (rangeSwitch != null) {
                        switchToEnd = true;
                    }
                }
            }
        }
        LocalTime startTime = source.toLocalTime().withHour(startHour).withMinute(startMin);
        LocalTime endTime = null;
        if (endHour == -1) {
            endTime = startTime.plusHours(1);
        } else {
            endTime = source.toLocalTime().withHour(endHour).withMinute(endMin);
        }
        return new Pair<>(startTime, endTime);
    }
}
