package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardDateExtractor implements Extractor {

    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        switch (chunkedPart.getLabel()) {
            case FORMAL_DATE:
                LocalDate date = getFormalDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;
        }
    }

    private LocalDate getFormalDate(LocalDateTime source, ChunkedPart chunkedPart) {
        LocalDate date = null;
        int year = source.getYear();
        int month = source.getMonthValue();
        int dayOfMonth = source.getDayOfMonth();

        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        if (chunkedPart.getTaggedWords().size() == 5) {
            TagValue first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
            TagValue second = taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER);
            TagValue third = taggedWords.get(4).getTags().containsTagByValue(Tag.NUMBER);
            boolean secondIsExactlyMonth = false;
            if (second == null) {
                second = taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME);
                secondIsExactlyMonth = true;
            }
            if (first != null && second != null && third != null) {
                int firstNumber = (int) first.value;
                int secondNumber = (int) second.value;
                int thirdNumber = (int) third.value;
                // for formats like 2017/04/02
                if (firstNumber >= 1000) {
                    //so it's year
                    year = firstNumber;
                    //so second number is month and third one is day of month
                    month = secondNumber;
                    dayOfMonth = thirdNumber;
                }
                //for formats like 1/02/2017
                else if (thirdNumber >= 1000) {
                    year = thirdNumber;
                    if (secondIsExactlyMonth) {
                        month = secondNumber;
                        dayOfMonth = firstNumber;
                    } else {
                        month = firstNumber;
                        dayOfMonth = secondNumber;
                    }
                } else {
                    if (thirdNumber < 100) {
                        //omits the first ending digits and replace with thirdNumber
                        year = (year / 100) * 100 + thirdNumber;
                    }
                    month = firstNumber;
                    dayOfMonth = secondNumber;
                }

            }
        }
        return LocalDate.of(year, month, dayOfMonth);
    }
}
