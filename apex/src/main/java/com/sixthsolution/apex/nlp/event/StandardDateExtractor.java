package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.dict.Tags;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
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

//        switch (chunkedPart.getLabel()) {
//
//            case FORMAL_DATE:
//                date = getFormalDate(source, chunkedPart);
//                builder.setStartDate(date);
//                builder.setEndDate(date);
//                break;
//
//            case RELAX_DATE:
//                date = getRelaxDate(source, chunkedPart);
//                builder.setStartDate(date);
//                builder.setEndDate(date);
//                break;
//
//            case RELATIVE_DATE:
//                date=getRelativeDate(source, chunkedPart);
//                builder.setStartDate(date);
//                builder.setEndDate(date);
//                break;
//
//            case LIMITED_DATE:
//                Pair<LocalDate,LocalDate> pdate =getLimitedDate(source, chunkedPart);                builder.setStartDate(date);
//                builder.setStartDate(pdate.first);
//                builder.setEndDate(pdate.second);
//                break;
//
//        }
    }

    private LocalDate getFormalDate(LocalDateTime source, ChunkedPart chunkedPart) {

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
        //the form monthnumber/yearnumber has been ignored
        else if (chunkedPart.getTaggedWords().size() == 3) {
            TagValue first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
            TagValue second = taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER);
            boolean firstIsExactlyMonth = false;
            if (first == null) {
                first = taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME);
                firstIsExactlyMonth = true;
            }
            if (first != null && second != null) {
                int firstNumber = (int) first.value;
                int secondNumber = (int) second.value;
                //for formats like 1/02
                if (!firstIsExactlyMonth) {
                    month = secondNumber;
                    dayOfMonth = firstNumber;
                } else {
                    month = firstNumber;
                    dayOfMonth = secondNumber;
                }
            }
        }
        date = LocalDate.of(year, month, dayOfMonth);
        return date;
    }


    private LocalDate getRelaxDate(LocalDateTime source, ChunkedPart chunkedPart) {

        int year = -1;
        int month = -1;
        int dayOfMonth = -1;

        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        TagValue first ;
        TagValue second ;
        TagValue third ;


        if (taggedWords.size() > 1) {
            TagValue intchk = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
            String test=taggedWords.get(0).getWord();
            if ((test).matches(".*\\d+.*")) {

                System.out.println("part1 1<3 and start with number");
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME);

                if (second == null) {
                    System.out.println("part1 1<3 and start with number has of");
                    second = taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME);
                }
                dayOfMonth = (int) first.value;
                month = (int) second.value;
                if (taggedWords.size()>2 && taggedWords.get(taggedWords.size()-1).getWord().matches("\\d+")) {

                    third = taggedWords.get(taggedWords.size()-1).getTags().containsTagByValue(Tag.NUMBER);
                    System.out.println("year:"+third.value);
                    year = Integer.valueOf(third.value.toString());
                    return LocalDate.of(year, month, dayOfMonth);
                }
                return LocalDate.of(LocalDate.now().getYear(), month, dayOfMonth);

            }
            if (!(test).matches(".*\\d+.*")) {
                System.out.println("part2 1<3 and start with month name");
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.NUMBER);
                if ((int) second.value > 30) {
                    System.out.println("part2 1<3 and start with month name and year");
                    month = (int) first.value;
                    year = (int) second.value;
                    return LocalDate.of(year, month, LocalDate.now().getDayOfMonth());

                }
                month = (int) first.value;
                dayOfMonth = (int) second.value;
                System.out.println("size:"+taggedWords.size());
                if (taggedWords.size()>2&&taggedWords.get(taggedWords.size()-1).getWord().matches("\\d+")) {

                    third = taggedWords.get(taggedWords.size()).getTags().containsTagByValue(Tag.NUMBER);
                    System.out.println("year:"+third.value);
                    year = Integer.valueOf(third.value.toString());
                    return LocalDate.of(year, month, dayOfMonth);
                }
                return LocalDate.of(LocalDate.now().getYear(), month, dayOfMonth);

            }
        }


        System.out.println("part 4 just month");
        first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
        if (first == null) {
            first = taggedWords.get(0).getTags().containsTagByValue(Tag.WEEK_DAY);
        }
        month = (int) first.value;
        return LocalDate.of(LocalDate.now().getYear(), month, LocalDate.now().getDayOfMonth());
        //TODO else for weekday


    }


    private LocalDate getRelativeDate(LocalDateTime source, ChunkedPart chunkedPart)
    {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        TagValue first = taggedWords.get(0).getTags().containsTagByValue(Tag.NAMED_DATE);

        LocalDate res =LocalDate.now();
        if(first.tag.equals(Tag.NAMED_DATE)){
            res=LocalDate.now().plusDays((int)first.value);
            return res;
        }
        else if(first.tag.equals(Tag.RELATIVE_PREPOSITION)) {
            if(taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).equals(Tag.DATE_SEEKBY)){
                res=LocalDate.now().plusDays(((int)first.value)*((int)taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).value));
            }
            if(taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).equals(Tag.WEEK_DAY)){
                int dif=((int)taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).value)-(LocalDate.now().getDayOfWeek().getValue());
                res=LocalDate.now().plusDays(dif).plusDays(((int)first.value-1)*7);
            }

        }
        else{// (first.tag.equals(Tag.NUMBER)){
            if((taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).equals(Tag.DATE_SEEKBY))&&(taggedWords.get(2).getTags().containsTagByValue(Tag.RELATIVE_SUFFIX).tag.equals(Tag.RELATIVE_SUFFIX))){
                res=LocalDate.now().plusDays(((int)first.value)*((int)taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).value));
            }

        }

        return res;
    }

    private Pair<LocalDate, LocalDate> getLimitedDate(LocalDateTime source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        Tags first = taggedWords.get(0).getTags();

        StandardDateExtractor sde = new StandardDateExtractor();
        LocalDate start;
        LocalDate end;
        if (first.containsTag(Tag.DATE_START_RANGE)) {
            int index = 0;
            while (!taggedWords.get(index).getTags().containsTag(Tag.DATE_RANGE)) index++;
            List<TaggedWord> taggi = taggedWords.subList(1, index);
            //TODO add for another forms
            ChunkedPart cp = new ChunkedPart(DATE, Label.FORMAL_DATE, taggi);
            List<TaggedWord> taggi2 = chunkedPart.getTaggedWords(index + 1, chunkedPart.getTaggedWords().size());
            ChunkedPart cp2 = new ChunkedPart(DATE, Label.FORMAL_DATE, taggi2);
            sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
            StandardDateExtractor sde2 = new StandardDateExtractor();
            sde2.extract(new EventBuilder(), LocalDateTime.now(), cp2);
            start = sde.date;
            end = sde2.date;
        }
            else if (first.containsTag(Tag.DATE_RANGE)){
                List<TaggedWord> taggi = taggedWords.subList(1, taggedWords.size());
                ChunkedPart cp = new ChunkedPart(DATE, Label.FORMAL_DATE, taggi);
                sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
                start = LocalDate.now();
                end = sde.date;

        } else {
            ChunkedPart cp = new ChunkedPart(DATE, Label.DATE, chunkedPart.getTaggedWords(1, chunkedPart.getTaggedWords().size()));
            sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
            start = LocalDate.now();
            end = sde.date;
        }
        Pair<LocalDate, LocalDate> p = new Pair<>(start, end);
        return p;
    }
}
