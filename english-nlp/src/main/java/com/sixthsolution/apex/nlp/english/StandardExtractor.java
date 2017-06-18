package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.event.EventBuilder;
import com.sixthsolution.apex.nlp.event.StandardDateExtractor;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * Created by rozhin on 6/18/2017.
 */
public class StandardExtractor extends StandardDateExtractor {
    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        switch (chunkedPart.getLabel()) {
            case RELAX_DATE:
                LocalDate date = getRelaxDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;
        }
    }

    private LocalDate getRelaxDate(LocalDateTime source, ChunkedPart chunkedPart) {
        LocalDate date = null;
        int year = source.getYear();
        int month = source.getMonthValue();
        int dayOfMonth = source.getDayOfMonth();

        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        TagValue first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
        TagValue second = taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME);
        TagValue third = taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER);

        if (taggedWords.size()>=3) {
            TagValue intchk = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
            Integer n = -1;
            if (intchk.value.getClass() == n.getClass()) {
                boolean secondIsExactlyMonth = false;
                if (second == null) {
                    second = taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME);
                    third = taggedWords.get(3).getTags().containsTagByValue(Tag.NUMBER);
                    secondIsExactlyMonth = true;
                }
                dayOfMonth=(int)first.value;
                month=(int)second.value;
                year=(int)third.value;

            } else {
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.NUMBER);
                third = taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER);
                month=(int)first.value;
                dayOfMonth=(int)second.value;
                year=(int)third.value;
            }
        }else {
            first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
            boolean firstIsExactlyMonth = false;
            if (first == null) {
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.WEEK_DAY);
                firstIsExactlyMonth = true;
            }
            month=(int)first.value;
            //TODO week day no idea:(

        }

        return LocalDate.of(year, month, dayOfMonth);
    }
}
