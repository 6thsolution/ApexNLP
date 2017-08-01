package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.model.Frequency;
import com.sixthsolution.apex.model.Recurrence;
import com.sixthsolution.apex.model.WeekDay;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.dict.Tags;
import com.sixthsolution.apex.nlp.event.EventBuilder;
import com.sixthsolution.apex.nlp.event.Extractor;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.RegExChunker;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.util.Pair;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;

/**
 * @author Rozhin Bayati
 * */

public class StandardExtractor implements Extractor {

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
                date = getRelativeDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;

            case GLOBAL_DATE:
                date = getGlobalDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setEndDate(date);
                break;

            case LIMITED_DATE:
                Pair<LocalDate, LocalDate> pdate = getLimitedDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setStartDate(pdate.first);
                builder.setEndDate(pdate.second);
                break;

            case FOREVER_DATE:
                Pair<Recurrence, Pair<LocalDate, LocalDate>> pdate2 = getForever(source, chunkedPart);
                builder.setReccurence(pdate2.first);
                builder.setStartDate(pdate2.second.first);
                builder.setEndDate(pdate2.second.second);
        }
    }
    private Pair<Recurrence, Pair<LocalDate, LocalDate>> getForever(LocalDateTime source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        Recurrence recurrence =null;
        int interval = 1;
        Frequency frequency = Frequency.DAILY;
        List<WeekDay> byDays = new ArrayList<>();

        if (taggedWords.get(0).getTags().containsTag(Tag.DATE_RECURRENCE)) {
            if (taggedWords.get(1).getTags().containsTag(Tag.NUMBER)) {
                interval = (int) taggedWords.get(1).getTags().containsTagByValue(Tag.NUMBER).value;
                if (taggedWords.get(2).getTags().containsTag(Tag.DATE_SEEKBY)) {
                    if (taggedWords.get(2).getTags().containsTag(Tag.DAY_SEEK)) {
                        frequency = Frequency.DAILY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_SEEK)) {
                        frequency = Frequency.WEEKLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.MONTH_SEEK)) {
                        frequency = Frequency.MONTHLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.YEAR_SEEK)) {
                        frequency = Frequency.YEARLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_DAY)) {
                        frequency = Frequency.WEEKLY;
                        byDays.add((WeekDay) taggedWords.get(2).getTags().containsTagByValue(Tag.WEEK_DAY).value);
                    }

                }
            } else if (taggedWords.get(1).getTags().containsTag(Tag.DATE_FOREVER_KEY)) {
                interval = 2;
                if (taggedWords.get(2).getTags().containsTag(Tag.DATE_SEEKBY)) {
                    if (taggedWords.get(2).getTags().containsTag(Tag.DAY_SEEK)) {
                        frequency = Frequency.DAILY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_SEEK)) {
                        frequency = Frequency.WEEKLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.MONTH_SEEK)) {
                        frequency = Frequency.MONTHLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.YEAR_SEEK)) {
                        frequency = Frequency.YEARLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_DAY)) {
                        frequency = Frequency.WEEKLY;
                        byDays.add((WeekDay) taggedWords.get(2).getTags().containsTagByValue(Tag.WEEK_DAY).value);
                    }

                }
            } else if (taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) {
                interval = 1;
                if (taggedWords.get(2).getTags().containsTag(Tag.DATE_SEEKBY)) {
                    if (taggedWords.get(2).getTags().containsTag(Tag.DAY_SEEK)) {
                        frequency = Frequency.DAILY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_SEEK)) {
                        frequency = Frequency.WEEKLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.MONTH_SEEK)) {
                        frequency = Frequency.MONTHLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.YEAR_SEEK)) {
                        frequency = Frequency.YEARLY;
                    } else if (taggedWords.get(2).getTags().containsTag(Tag.WEEK_DAY)) {
                        frequency = Frequency.WEEKLY;
                        byDays.add((WeekDay) taggedWords.get(2).getTags().containsTagByValue(Tag.WEEK_DAY).value);
                    }

                }
            }


            int index = 0;
            while (index<taggedWords.size() && (!taggedWords.get(index).getTags().containsTag(Tag.DATE_RANGE))) index++;
            if (index <taggedWords.size()) {
                List<TaggedWord> taggi = chunkedPart.getTaggedWords(index + 1, chunkedPart.getTaggedWords().size());
                ChunkedPart cp = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
                StandardExtractor sde = new StandardExtractor();
                sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
                LocalDate untildate = sde.date;
                recurrence=new Recurrence(frequency, interval,LocalDateTime.of(untildate, LocalTime.now()),false,byDays);
                return new Pair<>(recurrence,new Pair<>(LocalDate.now(),untildate));

            }
            else {
                recurrence=new Recurrence(frequency, interval,LocalDateTime.now(),false,byDays);
                return new Pair<>(recurrence,new Pair<>(LocalDate.now(),LocalDateTime.now().toLocalDate()));
            }


        }
        recurrence=new Recurrence(frequency, interval, LocalDateTime.now(),false,byDays);
        return new Pair<>(recurrence,new Pair<>(LocalDate.now(),recurrence.until().toLocalDate()));
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
        TagValue first;
        TagValue second;
        TagValue third;


        if (taggedWords.size() > 1) {
            String test = taggedWords.get(0).getWord();
            if ((test).matches(".*\\d+.*")) {

//                System.out.println("part1 1❤️ and start with number");
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME);

                if (second == null) {
//                    System.out.println("part1 1❤️ and start with number has of");
                    second = taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME);
                }
                dayOfMonth = (int) first.value;
                month = (int) second.value;
                if (taggedWords.size() > 2 && taggedWords.get(taggedWords.size() - 1).getWord().matches("\\d+")) {

                    third = taggedWords.get(taggedWords.size() - 1).getTags().containsTagByValue(Tag.NUMBER);
//                    System.out.println("year:" + third.value);
                    year = Integer.valueOf(third.value.toString());
                    return LocalDate.of(year, month, dayOfMonth);
                }
                return LocalDate.of(LocalDate.now().getYear(), month, dayOfMonth);

            }
            if (!(test).matches(".*\\d+.*")) {
//                System.out.println("part2 1❤️ and start with month name");
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.NUMBER);
                if ((int) second.value > 30) {
//                    System.out.println("part2 1❤️ and start with month name and year");
                    month = (int) first.value;
                    year = (int) second.value;
                    return LocalDate.of(year, month, LocalDate.now().getDayOfMonth());

                }
                month = (int) first.value;
                dayOfMonth = (int) second.value;
//                System.out.println("size:" + taggedWords.size());
                if (taggedWords.size() > 2 && taggedWords.get(taggedWords.size() - 1).getWord().matches("\\d+")) {

                    third = taggedWords.get(taggedWords.size() - 1).getTags().containsTagByValue(Tag.NUMBER);
//                        System.out.println("year:" + third.value);
                    year = Integer.valueOf(third.value.toString());
                    return LocalDate.of(year, month, dayOfMonth);

                }

                return LocalDate.of(LocalDate.now().getYear(), month, dayOfMonth);

            }
        }

//        System.out.println("part 4 just month");
        first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
        if (first == null) {
            int dif = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
            if (dif < 0)
                dif = 7 + dif;
            return LocalDate.now().plusDays(dif);
        } else {
            month = (int) first.value;
            return LocalDate.of(LocalDate.now().getYear(), month, LocalDate.now().getDayOfMonth());
        }


    }


    private LocalDate getRelativeDate(LocalDateTime source, ChunkedPart chunkedPart) {
        //TODO add seasons event

        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        int plusday = 0;
        if (taggedWords.get(0).getTags().containsTag(Tag.NAMED_DATE)) {
            System.out.println("start with named date");
            plusday = (int) taggedWords.get(0).getTags().containsTagByValue(Tag.NAMED_DATE).value;
            return LocalDate.now().plusDays(plusday);
        }
        if (taggedWords.get(0).getTags().containsTag(Tag.RELATIVE_PREPOSITION)) {
            System.out.println("start with next");
            if (taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) {
                System.out.println("followed by date-seekby");
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.RELATIVE_PREPOSITION).value);
                if (taggedWords.get(1).getTags().containsTag(Tag.DAY_SEEK)) {
                    System.out.println("form of next day");
                    return LocalDate.now().plusDays(plusday);
                } else if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_SEEK)) {
                    System.out.println("form of next month");
                    if (taggedWords.size() > 2) {
                        if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                            System.out.println("form of next month 20th");
                            return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().plusMonths(plusday).getMonth(), ((int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value));
                        }
                    }
                    return LocalDate.now().plusMonths(plusday);
                } else if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_SEEK)) {
                    System.out.println("form of next week");
                    if (taggedWords.size() > 2) {
                        if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                            System.out.println("form of next week third day");
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getDayOfWeek().getValue();
                            return LocalDate.now().plusDays(7 + plusday);

                        }
                    }
                    return LocalDate.now().plusDays(plusday * 7);
                } else if (taggedWords.get(1).getTags().containsTag(Tag.YEAR_SEEK)) {
                    System.out.println("form of next year");
                    if (taggedWords.size() > 2) {
                        if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                            System.out.println("form of next year 20th");
                            if (taggedWords.size() > 3) {
                                if (taggedWords.get(3).getTags().containsTag(Tag.MONTH_SEEK)) {
                                    System.out.println("form of next year second month");
                                    if (taggedWords.size() > 4) {
                                        if (taggedWords.get(4).getTags().containsTag(Tag.NUMBER)) {
                                            System.out.println("form of next year second month 20th");
                                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getMonthValue();
                                            return LocalDate.of(LocalDate.now().plusYears(1).getYear(), LocalDate.now().plusMonths(12 + plusday).getMonth(), ((int) taggedWords.get(4).getTags().containsTagByValue(Tag.NUMBER).value));


                                        }
                                    }
                                    plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getMonthValue();
                                    return LocalDate.now().plusMonths(12 + plusday);
                                }
                            }
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getDayOfYear();
                            return LocalDate.now().plusYears(1).plusDays(365 + plusday);
                        }
                        if (taggedWords.get(2).getTags().containsTag(Tag.MONTH_NAME)) {
                            System.out.println("form of next year april");
                            if (taggedWords.size() >= 4) {
                                if (taggedWords.get(3).getTags().containsTag(Tag.NUMBER)) {
                                    System.out.println("form of next year april 20th");
                                    plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME).value - LocalDate.now().getMonthValue();
                                    if (plusday < 0)
                                        plusday += 12;
                                    return LocalDate.of(LocalDate.now().plusYears(1).getYear(), LocalDate.now().plusMonths(plusday).getMonth()
                                            , ((int) taggedWords.get(3).getTags().containsTagByValue(Tag.NUMBER).value));
                                }
                            }
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME).value - LocalDate.now().getMonthValue();
                            if (plusday < 0)
                                plusday += 12;
                            return LocalDate.now().plusYears(1).plusMonths(plusday);
                        }
                    }
                    return LocalDate.now().plusYears(plusday);
                }
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_DAY)) {
                System.out.println("followed by weekday");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
                if (dif <= 0)
                    dif = 7 + dif;
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.RELATIVE_PREPOSITION).value - 1) * 7;
                return LocalDate.now().plusDays(dif).plusDays(plusday);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_NAME)) {
                System.out.println("followed by MonthName");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME).value) - (LocalDate.now().getMonth().getValue());
                if (dif <= 0)
                    dif = 12 + dif;
                if (taggedWords.size() > 2) {
                    if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                        System.out.println("form of next april 20th");
                        return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().plusMonths(dif).getMonth(), ((int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value));
                    }
                } else return LocalDate.now().plusMonths(dif);
            }

        } else if (taggedWords.get(0).getTags().containsTag(Tag.NUMBER)) {
            System.out.println("start with number");
            if ((taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) && (taggedWords.get(2).getTags().containsTag(Tag.RELATIVE_SUFFIX))) {
                System.out.println("followed by date-seekby");
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value);
                if (taggedWords.get(1).getTags().containsTag(Tag.DAY_SEEK)) {
                    return LocalDate.now().plusDays(plusday);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_SEEK)) {
                    return LocalDate.now().plusMonths(plusday);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_SEEK)) {
                    return LocalDate.now().plusDays(plusday * 7);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.YEAR_SEEK)) {
                    return LocalDate.now().plusYears(plusday);
                }
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value) * ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).value);
                return LocalDate.now().plusDays(plusday);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_DAY)) {
                System.out.println("followed by weekday");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
                if (dif <= 0)
                    dif = 7 + dif;
                return LocalDate.now().plusDays(dif).plusDays((((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value) - 1) * 7);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_NAME)) {
                System.out.println("followed by monthName");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME).value) - (LocalDate.now().getMonth().getValue());
                if (dif <= 0)
                    dif = 12 + dif;
                return LocalDate.now().plusMonths(dif);
            }
        }

        return null;
    }

    private LocalDate getGlobalDate(LocalDateTime source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        int index = 0;
        while (!taggedWords.get(index).getTags().containsTag(Tag.GLOBAL_PREPOSITION)) index++;
//        List<TaggedWord> taggi = taggedWords.subList(0, index);
//        ChunkedPart cp = new RegExChunker(Arrays.asList(new TimeDetector(), new LocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
        List<TaggedWord> taggi2 = chunkedPart.getTaggedWords(index + 1, chunkedPart.getTaggedWords().size());
        ChunkedPart cp2 = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi2)).get(0);
        boolean forward = (boolean) taggedWords.get(index).getTags().containsTagByValue(Tag.GLOBAL_PREPOSITION).value;
        int counter;
        if (taggedWords.get(0).getTags().containsTag(Tag.NUMBER)) {
            counter = (int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value;
        } else {
            counter = 1;
        }
        int seek = 0;
        if (taggedWords.get(0).getTags().containsTag(Tag.DATE_SEEKBY)) {
            seek = (int) taggedWords.get(0).getTags().containsTagByValue(Tag.DATE_SEEKBY).value;
        } else if (taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) {
            seek = (int) taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).value;
        }
        StandardExtractor sde = new StandardExtractor();
        sde.extract(new EventBuilder(), LocalDateTime.now(), cp2);
        if (forward) {
            return sde.date.plusDays(seek * counter);
        } else {
            return sde.date.minusDays(seek * counter);
        }

    }

    private Pair<LocalDate, LocalDate> getLimitedDate(LocalDateTime source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        Tags first = taggedWords.get(0).getTags();

        StandardExtractor sde = new StandardExtractor();
        LocalDate start;
        LocalDate end;
        if (first.containsTag(Tag.DATE_START_RANGE)) {
            int index = 0;
            while (!taggedWords.get(index).getTags().containsTag(Tag.DATE_RANGE)) index++;
            List<TaggedWord> taggi = taggedWords.subList(1, index);
            ChunkedPart cp = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
            List<TaggedWord> taggi2 = chunkedPart.getTaggedWords(index + 1, chunkedPart.getTaggedWords().size());
            ChunkedPart cp2 = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi2)).get(0);
            sde.extract(new EventBuilder(), LocalDateTime.now(), cp);
            StandardExtractor sde2 = new StandardExtractor();
            sde2.extract(new EventBuilder(), LocalDateTime.now(), cp2);
            start = sde.date;
            end = sde2.date;
        } else if (first.containsTag(Tag.DATE_RANGE)) {
            List<TaggedWord> taggi = taggedWords.subList(1, taggedWords.size());
            ChunkedPart cp = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
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