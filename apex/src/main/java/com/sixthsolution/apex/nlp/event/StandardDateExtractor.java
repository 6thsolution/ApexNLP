package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.util.Pair;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.List;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class StandardDateExtractor implements Extractor {

    LocalDate date;


    @Override
    public void extract(EventBuilder builder, LocalDateTime source, ChunkedPart chunkedPart) {
        switch (chunkedPart.getLabel()) {
            case FORMAL_DATE:
                date = getFormalDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;
            case RELAX_DATE:
                date = getRelaxDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;
            case RELATIVE_DATE:
                date=getRelativeDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;
            case LIMITED_DATE:
                date=getLimitedDate(source, chunkedPart).first;
                builder.setStartDate(date);
                date=getLimitedDate(source, chunkedPart).second;
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
    

    private LocalDate getRelativeDate(LocalDateTime source, ChunkedPart chunkedPart) {
        return null;
    }

    private Pair<LocalDate,LocalDate> getLimitedDate(LocalDateTime source, ChunkedPart chunkedPart) {
       List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        TagValue first = taggedWords.get(0).getTags().containsTagByValue(Tag.DATE_START_RANGE);

        StandardDateExtractor sde = new StandardDateExtractor();

        if (first.value.equals("from")) {
            int index = taggedWords.indexOf(Tag.DATE_RANGE);
            List<TaggedWord> taggi = chunkedPart.getTaggedWords(1, index);
            ChunkedPart cp = new ChunkedPart(DATE, Label.DATE, taggi);
            List<TaggedWord> taggi2 = chunkedPart.getTaggedWords(index, chunkedPart.getTaggedWords().size());
            ChunkedPart cp2 = new ChunkedPart(DATE, Label.DATE, taggi2);
            sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
            StandardDateExtractor sde2 = new StandardDateExtractor();
            sde2.extract(new EventBuilder(), LocalDateTime.now(), cp2);
            return new Pair<>(sde.date, sde2.date);

        } else {
            ChunkedPart cp = new ChunkedPart(DATE, Label.DATE, chunkedPart.getTaggedWords(1, chunkedPart.getTaggedWords().size()));
            sde.extract(new EventBuilder(),LocalDateTime.now(),cp);
            return new Pair<>(LocalDate.now(),sde.date);
        }
    }
}
