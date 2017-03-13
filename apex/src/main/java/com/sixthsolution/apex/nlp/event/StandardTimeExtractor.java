package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.util.Pair;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sixthsolution.apex.nlp.dict.Tag.TIME_HOUR;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MIN;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RELATIVE_INDICATOR;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardTimeExtractor implements Extractor {

    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        Pair<LocalTime, LocalTime> time = null;
        switch (chunkedPart.getLabel()) {
            case FIXED_TIME:
            case RANGE_TIME:
                time = getFixedOrRangeTime(source, chunkedPart);
                builder.setStartTime(time.first);
                builder.setEndTime(time.second);
                break;
            case RELATIVE_TIME:
                time = getRelativeTime(source, chunkedPart);
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

    private Pair<LocalTime, LocalTime> getRelativeTime(LocalDateTime source,
                                                       ChunkedPart chunkedPart) {
        Iterator<TaggedWord> itr = chunkedPart.getTaggedWords().iterator();
        int seekOffset = 0;
        SeekBy seekBy = null;
        boolean forwarding = true;
        LocalTime startTime = source.toLocalTime();
        while (itr.hasNext()) {
            TaggedWord next = itr.next();
            if (next.hasTag(Tag.NUMBER)) {
                seekOffset = (Integer) next.getTags().containsTagByValue(Tag.NUMBER).value;
            } else if (next.hasTag(TIME_HOUR)) {
                seekBy = (SeekBy) next.getTags().containsTagByValue(Tag.TIME_HOUR).value;
            } else if (next.hasTag(TIME_MIN)) {
                seekBy = (SeekBy) next.getTags().containsTagByValue(Tag.TIME_MIN).value;
            } else if (next.hasTag(TIME_RELATIVE_INDICATOR)) {
                forwarding =
                        (Boolean) next.getTags()
                                .containsTagByValue(Tag.TIME_RELATIVE_INDICATOR).value;
                List<TaggedWord> formalTaggedWords = new ArrayList<>();
                while (itr.hasNext()) {
                    formalTaggedWords.add(itr.next());
                }
                ChunkedPart formalPart =
                        new ChunkedPart(chunkedPart.getEntity(), chunkedPart.getLabel(),
                                formalTaggedWords);
                startTime = getFixedOrRangeTime(source, formalPart).first;
                break;
            }
        }
        LocalTime endTime = null;
        switch (seekBy) {
            case HOUR:
                if (forwarding) {
                    endTime = startTime.plusHours(seekOffset);
                } else {
                    endTime = startTime.minusHours(seekOffset);
                }
                break;
            case MIN:
                if (forwarding) {
                    endTime = startTime.plusMinutes(seekOffset);
                } else {
                    endTime = startTime.minusMinutes(seekOffset);
                }
                break;
        }
        //start time is greater, so swap them
        if (startTime.compareTo(endTime) > 0) {
            return new Pair<>(endTime, startTime);
        }
        return new Pair<>(startTime, endTime);
    }

}
