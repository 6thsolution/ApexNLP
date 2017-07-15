package com.sixthsolution.apex.nlp.english;

import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.english.filter.DateDetectionFilter;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.util.Pair;

import java.util.Arrays;
import java.util.List;

import static com.nobigsoftware.dfalex.Pattern.*;
import static com.sixthsolution.apex.nlp.dict.Tag.*;
import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Label.*;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class DateDetector extends ChunkDetector {

    /**
     * @return 3/14/2016, 3/14/16, 03/14/16, 03/apr/2016
     */
    private static Pattern formal_date() {
        return match(NUMBER.toString()).then(DATE_SEPARATOR.toString())
                .then(anyOf(NUMBER.toString(), MONTH_NAME.toString()))
                .then(DATE_SEPARATOR.toString())
                .then(NUMBER.toString());
    }

    /**
     * @return april, april 20, april 20th, april 20 2012
     */
    private static Pattern relax_date_type1() {
        return month_name().then(maybe(NUMBER.toString()).then(maybe(DATE_SUFFIX.toString())).then(maybe(NUMBER.toString())));
    }

    /**
     * @return 20 april, 20th april, 20 of april, 20 of april 2012
     */
    private static Pattern relax_date_type2() {
        return match(NUMBER.toString()).then(maybe(DATE_SUFFIX.toString())).then(maybe(DATE_PREFIX.toString())).then(month_name()).then(maybe(NUMBER.toString()));
    }

    /**
     * @return april, may...
     */
    private static Pattern month_name(){
        return match(MONTH_NAME.toString());
    }

    /**
     * @return sunday, monday...
     */
    private static Pattern week_day(){
        return match(WEEK_DAY.toString());
    }

    /**
     * @return april 20, 20 of april 2012, monday, april
     */
    private static Pattern relax_date(){
        return match(anyOf(relax_date_type1(),relax_date_type2(),week_day()));
    }


    /**
     * @return today, tomorrow, tonight, ...
     */
    private static Pattern relative_date_type1(){
        return match(NAMED_DATE.toString());
    }

    /**
     * @return next sunday, two monday from today,...
     */
    private static Pattern relative_date_type2(){
        return match(anyOf(match(RELATIVE_PREPOSITION.toString()).then(WEEK_DAY.toString())
                ,match(NUMBER.toString()).then(WEEK_DAY.toString()).then(RELATIVE_SUFFIX.toString())));
    }

    /**
     * @return next april, next april 20th , 20 day of next april,...
     */
    private static Pattern relative_date_type3(){
        return match(anyOf(match(RELATIVE_PREPOSITION.toString()).then(MONTH_NAME.toString()).then(maybe(NUMBER.toString()))
        ,match(NUMBER.toString()).then(maybe(DATE_DURATION_SUFFIX.toString()).then(maybe(DATE_DURATION_SUFFIX.toString()).then(RELATIVE_PREPOSITION.toString()).then(MONTH_NAME.toString())))));
    }

    /**+
     * @return 2 summer from today, winter 2014, next spring,...
     */
    private static Pattern relative_date_type4(){
        return match(anyOf(match(NUMBER.toString()).then(SEASON.toString()).then(RELATIVE_SUFFIX.toString())
        ,match(RELATIVE_PREPOSITION.toString()).then(SEASON.toString()).then(maybe(relax_date()))
        ,match(SEASON.toString()).then(NUMBER.toString())));
    }

    /**
     * @return next week third day, 4 week from now, next year may 20th
     */
    private static Pattern relative_date_type5(){
        return match(anyOf(match(NUMBER.toString()).then(DATE_SEEKBY.toString()).then(RELATIVE_SUFFIX.toString())
        ,match(RELATIVE_PREPOSITION.toString()).then(DATE_SEEKBY.toString()).then(maybe(relax_date()))
        ,match(RELATIVE_PREPOSITION.toString()).then(DATE_SEEKBY.toString()).then(NUMBER.toString()).then(DATE_SEEKBY.toString())));
    }

    /**
     * @return 5 types of relative date structure
     */
    private static Pattern relative_date(){
        return match(anyOf(relative_date_type1(),relative_date_type2(),relative_date_type3(),relative_date_type4(),relative_date_type5()));
    }


    /**
     * @return the day after tomorrow, one week before sunday
     */
    private static Pattern global_date(){
        return match(maybe(NUMBER.toString()).then(DATE_SEEKBY.toString()).then(GLOBAL_PREPOSITION.toString()).then(anyOf(relative_date(),formal_date(),relax_date())));
    }

    /**
     * @return day,month,year,week
     */
    private static Pattern forever_seek(){
        return match(anyOf(DATE_SEEKBY.toString(),WEEK_DAY.toString()));
    }
    /**
     * @return every other day, every 2 weeks
     */
    private static Pattern forever_date(){
        return match(DATE_RECURRENCE.toString()).then(maybe(anyOf(DATE_FOREVER_KEY.toString(),NUMBER.toString()))).then(forever_seek()).then(maybe(DATE_RANGE.toString())).then(maybe(anyOf(relax_date(),relative_date(),formal_date())));
    }

    /**
     * @return till next month, from 12/3/2012 until june,..
     */
    private static Pattern limited_date(){
        return match(maybe(DATE_START_RANGE.toString()).then(maybe(/*anyOf(relative_date(),relax_date(),*/(formal_date()).then(DATE_RANGE.toString()).then(anyOf(relax_date(),relative_date(),formal_date())))));
    }

    private static Pattern year_part(){
        return match(anyOf(
                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(YEAR_SEEK.toString()).then(maybe(RELATIVE_SUFFIX.toString())),
                maybe(THE_PREFIX.toString()).then(maybe(YEAR_SEEK.toString())).then(NUMBER.toString())));
    }
    private static Pattern month_part(){
        return match(anyOf(
                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(anyOf(MONTH_SEEK.toString(),MONTH_NAME.toString())).then(maybe(RELATIVE_SUFFIX.toString())),
                maybe(NUMBER.toString()).then(MONTH_SEEK.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(year_part())));
    }

    private static Pattern week_part(){
        return match(anyOf(
                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(anyOf(WEEK_SEEK.toString(),WEEK_DAY.toString())).then(maybe(RELATIVE_SUFFIX.toString())),
                maybe(NUMBER.toString()).then(WEEK_SEEK.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(month_part())).then(maybe(year_part())));

    }
    private static Pattern start_with_number(){
        return match(NUMBER.toString()).then(maybe(DATE_SEEKBY.toString())).then(DATE_PREFIX.toString()).then(anyOf(year_part(),month_part(),week_part()));
    }

    private static Pattern start_with_day_band(){
        return match(DATE_BAND.toString()).then(maybe(anyOf(DATE_SEEKBY.toString(),WEEK_DAY.toString(),MONTH_NAME.toString()))).then(DATE_PREFIX.toString()).then(anyOf(year_part(),month_part(),week_part()));
    }

    private static Pattern start_with_day_of_week(){
        return match(WEEK_DAY.toString()).then(DATE_PREFIX.toString()).then(anyOf(year_part(),month_part(),week_part()));
    }

    private static Pattern explicit_relative_date(){
        return match(anyOf(start_with_day_band(),start_with_day_of_week(),start_with_number()));
    }


//    //TODO debug explicite relative date
//    private static Pattern date_rules(){
//        return match(anyOf(formal_date(),relax_date(),relative_date(),/*explicit_relative_date(),*/global_date()));
//    }
//    //TODO add lists
//    private static Pattern recurrence(){
//        return match(anyOf(forever_date(),limited_date()/*,lists()*/));
//    }
    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(FORMAL_DATE, formal_date())
                ,newPattern(RELAX_DATE, relax_date())
                ,newPattern(RELATIVE_DATE, relative_date())
//                ,newPattern(EXPLICIT_RELATIVE_DATE,explicit_relative_date())
                ,newPattern(GLOBAL_DATE, global_date())
                ,newPattern(FOREVER_DATE,forever_date())
                //must debug
                ,newPattern(LIMITED_DATE,limited_date())
//                newPattern(DATE_RULES,date_rules())
//                ,newPattern(RECURRENCE,recurrence())
        );
    }

    @Override
    protected List<? extends ChunkDetectionFilter> getFilters() {
        return Arrays.asList(new DateDetectionFilter());
    }

    @Override
    protected Entity getEntity() {
        return DATE;
    }
}
