package com.sixthsolution.apex.nlp.persian.event;

import com.sixthsolution.apex.model.Frequency;
import com.sixthsolution.apex.model.WeekDay;
import com.sixthsolution.apex.nlp.dict.Tag;
import com.sixthsolution.apex.nlp.dict.TagValue;
import com.sixthsolution.apex.nlp.dict.Tags;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.RegExChunker;
import com.sixthsolution.apex.nlp.persian.PersianDateDetector;
import com.sixthsolution.apex.nlp.persian.PersianLocationDetector;
import com.sixthsolution.apex.nlp.persian.PersianTimeDetector;
import com.sixthsolution.apex.nlp.persian.calendar.tools.JalaliCalendar;
import com.sixthsolution.apex.nlp.persian.model.PersianExtractor;
import com.sixthsolution.apex.nlp.tagger.TaggedWord;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.util.Pair;
import org.threeten.bp.LocalDate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sixthsolution.apex.nlp.ner.Entity.DATE;

/**
 * @author Rozhin Bayati
 * */

public class StandardPersianExtractor implements PersianExtractor{

    JalaliCalendar date;





    public void extract(PersianEventBuilder builder, JalaliCalendar source, ChunkedPart chunkedPart) {

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
                Pair<JalaliCalendar, JalaliCalendar> pdate = getLimitedDate(source, chunkedPart);
                builder.setStartDate(date);
                builder.setStartDate(pdate.first);
                builder.setEndDate(pdate.second);
                break;

            case FOREVER_DATE:
                Pair<PersianRecurrence, Pair<JalaliCalendar, JalaliCalendar>> pdate2 = getForever(source, chunkedPart);
                builder.setRecurrence(pdate2.first);
                builder.setStartDate(pdate2.second.first);
                builder.setEndDate(pdate2.second.second);
        }
    }

    private Pair<PersianRecurrence, Pair<JalaliCalendar, JalaliCalendar>> getForever(JalaliCalendar source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        JalaliCalendar jc = new JalaliCalendar();
        jc=jc.convertor(LocalDate.now());
        PersianRecurrence recurrence =null;
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
                StandardPersianExtractor sde = new StandardPersianExtractor();
                sde.extract(new PersianEventBuilder(), jc, cp);
                JalaliCalendar untildate = sde.date;
                recurrence=new PersianRecurrence(frequency, interval,untildate,false,byDays);
                return new Pair<>(recurrence,new Pair<>(jc,untildate));

            }
            else {
                recurrence=new PersianRecurrence(frequency, interval,jc,false,byDays);
                return new Pair<>(recurrence,new Pair<>(jc,jc));
            }


        }
        recurrence=new PersianRecurrence(frequency, interval, jc,false,byDays);
        return new Pair<>(recurrence,new Pair<>(jc,recurrence.until()));
    }


    private JalaliCalendar getFormalDate(JalaliCalendar source, ChunkedPart chunkedPart) {

        int year = source.getYear();
        int month = source.getMonth();
        int dayOfMonth = source.getDay();

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
                else {
                    if (firstNumber < 100) {
                        //omits the first ending digits and replace with thirdNumber
                        year = (year / 100) * 100 + firstNumber;
                    }
                    month = secondNumber;
                    dayOfMonth = thirdNumber;
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

        return new JalaliCalendar(year,month,dayOfMonth);
    }


    private JalaliCalendar getRelaxDate(JalaliCalendar source, ChunkedPart chunkedPart) {

        int year = -1;
        int month = -1;
        int dayOfMonth = -1;

        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        TagValue first;
        TagValue second;
        TagValue third;

        JalaliCalendar jc = new JalaliCalendar();
        jc=jc.convertor(LocalDate.now());

        if (taggedWords.size() > 1) {
            String test = taggedWords.get(0).getWord();
            if ((test).matches(".*\\d+.*")) {

//                System.out.println("part1 1❤️ and start with number");
                first = taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER);
                second = taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME);

                dayOfMonth = (int) first.value;
                month = (int) second.value;
                if (taggedWords.size() > 2 && taggedWords.get(taggedWords.size() - 1).getWord().matches("\\d+")) {

                    third = taggedWords.get(taggedWords.size() - 1).getTags().containsTagByValue(Tag.NUMBER);
//                    System.out.println("year:" + third.value);
                    year = Integer.valueOf(third.value.toString());
                    return new JalaliCalendar(year,month,dayOfMonth);
                }
                return new JalaliCalendar(jc.getYear(),month,dayOfMonth);

            }
           //

        }

//        System.out.println("part 4 just month");
        first = taggedWords.get(0).getTags().containsTagByValue(Tag.MONTH_NAME);
        if (first == null) {
            int dif = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
            if (dif < 0)
                dif = 7 + dif;
            return jc.plusDays(dif);
        } else {
            month = (int) first.value;
            return new JalaliCalendar(jc.getYear(),month,jc.getDay());
        }


    }


    private JalaliCalendar getRelativeDate(JalaliCalendar source, ChunkedPart chunkedPart) {
        //TODO add seasons event
        JalaliCalendar jc= new JalaliCalendar();
        jc=jc.convertor(LocalDate.now());
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        int plusday = 0;
        if (taggedWords.get(0).getTags().containsTag(Tag.NAMED_DATE)) {
            System.out.println("start with named date");
            plusday = (int) taggedWords.get(0).getTags().containsTagByValue(Tag.NAMED_DATE).value;
            return jc.plusDays(plusday);
        }
        if (taggedWords.get(0).getTags().containsTag(Tag.RELATIVE_PREPOSITION)) {
            System.out.println("start with next");
            if (taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) {
                System.out.println("followed by date-seekby");
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.RELATIVE_PREPOSITION).value);
                if (taggedWords.get(1).getTags().containsTag(Tag.DAY_SEEK)) {
                    System.out.println("form of next day");
                    return jc.plusDays(plusday);
                } else if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_SEEK)) {
                    System.out.println("form of next month");
                    if (taggedWords.size() > 2) {
                        if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                            System.out.println("form of next month 20th");
                            return new JalaliCalendar(jc.getYear(), jc.plusMonth(plusday).getMonth(), ((int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value));
                        }
                    }
                    return jc.plusMonth(plusday);
                } else if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_SEEK)) {
                    System.out.println("form of next week");
                    if (taggedWords.size() > 2) {
                        if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                            System.out.println("form of next week third day");
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getDayOfWeek().getValue();
                            return jc.plusDays(7 + plusday);

                        }
                    }
                    return jc.plusDays(plusday * 7);
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
                                            return new JalaliCalendar(jc.plusYear(1).getYear(), jc.plusMonth(12 + plusday).getMonth(), ((int) taggedWords.get(4).getTags().containsTagByValue(Tag.NUMBER).value));


                                        }
                                    }
                                    plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getMonthValue();
                                    return jc.plusMonth(12 + plusday);
                                }
                            }
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value - LocalDate.now().getDayOfYear();
                            return jc.plusYear(1).plusDays(365 + plusday);
                        }
                        if (taggedWords.get(2).getTags().containsTag(Tag.MONTH_NAME)) {
                            System.out.println("form of next year april");
                            if (taggedWords.size() >= 4) {
                                if (taggedWords.get(3).getTags().containsTag(Tag.NUMBER)) {
                                    System.out.println("form of next year april 20th");
                                    plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME).value - LocalDate.now().getMonthValue();
                                    if (plusday < 0)
                                        plusday += 12;
                                    return new JalaliCalendar(jc.plusYear(1).getYear(), jc.plusMonth(plusday).getMonth()
                                            , ((int) taggedWords.get(3).getTags().containsTagByValue(Tag.NUMBER).value));
                                }
                            }
                            plusday = (int) taggedWords.get(2).getTags().containsTagByValue(Tag.MONTH_NAME).value - LocalDate.now().getMonthValue();
                            if (plusday < 0)
                                plusday += 12;
                            return jc.plusYear(1).plusMonth(plusday);
                        }
                    }
                    return jc.plusYear(plusday);
                }
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_DAY)) {
                System.out.println("followed by weekday");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
                if (dif <= 0)
                    dif = 7 + dif;
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.RELATIVE_PREPOSITION).value - 1) * 7;
                return jc.plusDays(dif).plusDays(plusday);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_NAME)) {
                System.out.println("followed by MonthName");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME).value) - (LocalDate.now().getMonth().getValue());
                if (dif <= 0)
                    dif = 12 + dif;
                if (taggedWords.size() > 2) {
                    if (taggedWords.get(2).getTags().containsTag(Tag.NUMBER)) {
                        System.out.println("form of next april 20th");
                        return new JalaliCalendar(jc.getYear(), jc.plusMonth(dif).getMonth(), ((int) taggedWords.get(2).getTags().containsTagByValue(Tag.NUMBER).value));
                    }
                } else return jc.plusMonth(dif);
            }

        } else if (taggedWords.get(0).getTags().containsTag(Tag.NUMBER)) {
            System.out.println("start with number");
            if ((taggedWords.get(1).getTags().containsTag(Tag.DATE_SEEKBY)) && (taggedWords.get(2).getTags().containsTag(Tag.RELATIVE_SUFFIX))) {
                System.out.println("followed by date-seekby");
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value);
                if (taggedWords.get(1).getTags().containsTag(Tag.DAY_SEEK)) {
                    return jc.plusDays(plusday);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_SEEK)) {
                    return jc.plusMonth(plusday);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_SEEK)) {
                    return jc.plusDays(plusday * 7);
                }
                if (taggedWords.get(1).getTags().containsTag(Tag.YEAR_SEEK)) {
                    return jc.plusYear(plusday);
                }
                plusday = ((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value) * ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.DATE_SEEKBY).value);
                return jc.plusDays(plusday);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.WEEK_DAY)) {
                System.out.println("followed by weekday");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.WEEK_DAY).value) - (LocalDate.now().getDayOfWeek().getValue());
                if (dif <= 0)
                    dif = 7 + dif;
                return jc.plusDays(dif).plusDays((((int) taggedWords.get(0).getTags().containsTagByValue(Tag.NUMBER).value) - 1) * 7);
            }
            if (taggedWords.get(1).getTags().containsTag(Tag.MONTH_NAME)) {
                System.out.println("followed by monthName");
                int dif = ((int) taggedWords.get(1).getTags().containsTagByValue(Tag.MONTH_NAME).value) - (LocalDate.now().getMonth().getValue());
                if (dif <= 0)
                    dif = 12 + dif;
                return jc.plusMonth(dif);
            }
        }

        return null;
    }

    private JalaliCalendar getGlobalDate(JalaliCalendar source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        JalaliCalendar jc =new JalaliCalendar();
        jc=jc.convertor(LocalDate.now());
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
        StandardPersianExtractor sde = new StandardPersianExtractor();
        sde.extract(new PersianEventBuilder(),jc, cp2);
        if (forward) {
            return sde.date.plusDays(seek * counter);
        } else {
            return sde.date.minusDays(seek * counter);
        }

    }

    private Pair<JalaliCalendar, JalaliCalendar> getLimitedDate(JalaliCalendar source, ChunkedPart chunkedPart) {
        List<TaggedWord> taggedWords = chunkedPart.getTaggedWords();
        Tags first = taggedWords.get(0).getTags();
        JalaliCalendar jc =new JalaliCalendar();
        jc=jc.convertor(LocalDate.now());
        StandardPersianExtractor sde = new StandardPersianExtractor();
        JalaliCalendar start;
        JalaliCalendar end;
        if (first.containsTag(Tag.DATE_START_RANGE)) {
            int index = 0;
            while (!taggedWords.get(index).getTags().containsTag(Tag.DATE_RANGE)) index++;
            List<TaggedWord> taggi = taggedWords.subList(1, index);
            ChunkedPart cp = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
            List<TaggedWord> taggi2 = chunkedPart.getTaggedWords(index + 1, chunkedPart.getTaggedWords().size());
            ChunkedPart cp2 = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi2)).get(0);
            sde.extract(new PersianEventBuilder(), jc, cp);
            StandardPersianExtractor sde2 = new StandardPersianExtractor();
            sde2.extract(new PersianEventBuilder(), jc, cp2);
            start = sde.date;
            end = sde2.date;
        } else if (first.containsTag(Tag.DATE_RANGE)) {
            List<TaggedWord> taggi = taggedWords.subList(1, taggedWords.size());
            ChunkedPart cp = new RegExChunker(Arrays.asList(new PersianTimeDetector(), new PersianLocationDetector(), new PersianDateDetector())).chunk(new TaggedWords(taggi)).get(0);
            sde.extract(new PersianEventBuilder(),jc, cp);
            start = jc;
            end = sde.date;

        } else {
            ChunkedPart cp = new ChunkedPart(DATE, Label.DATE, chunkedPart.getTaggedWords(1, chunkedPart.getTaggedWords().size()));
            sde.extract(new PersianEventBuilder(), jc, cp);
            start = jc;
            end = sde.date;
        }
        Pair<JalaliCalendar, JalaliCalendar> p = new Pair<>(start, end);
        return p;
    }
}