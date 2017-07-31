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
 * @author Rozhin Bayati
 */

public class DateDetector extends ChunkDetector {


    /**
     * @return 3/14/2016, 3/14/16, 03/14/16, 03/apr/2016
     */

    private static Pattern formal_date() {
        return match(NUMBER.toString()).then(DATE_SEPARATOR.toString())
                .then(anyOf(NUMBER.toString(), MONTH_NAME.toString()))
                .then(maybe(DATE_SEPARATOR.toString()))
                .then(maybe(NUMBER.toString()));
    }

    /**
     * @return april, april 20, april 20th, april 20 2012
     */
    private static Pattern relax_date_type1() {
        return month_name().then(maybe(NUMBER.toString()).then(maybe(DATE_SUFFIX.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(NUMBER.toString())));
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
    private static Pattern month_name() {
        return match(MONTH_NAME.toString());
    }

    /**
     * @return sunday, monday...
     */
    private static Pattern week_day() {
        return match(WEEK_DAY.toString());
    }

    /**
     * @return april 20, 20 of april 2012, monday, april
     */
    private static Pattern relax_date() {
        return match(anyOf(relax_date_type1(), relax_date_type2(), week_day()));
    }

    /**
     * @return today, tomorrow, tonight, ...
     */
    private static Pattern relative_date_type1() {
        return match(NAMED_DATE.toString());
    }

    /**
     * @return next sunday, ...
     */
    private static Pattern relative_date_type2_0() {
        return match(RELATIVE_PREPOSITION.toString()).then(WEEK_DAY.toString());
    }

    /**
     * @return week 3rd day, week monday, week, ...
     */
    private static Pattern relative_date_type2_1_0() {
        return match(DATE_SEEKBY.toString()).thenMaybe(anyOf(match(WEEK_DAY.toString()), match(NUMBER.toString()).thenMaybe(DATE_SUFFIX.toString()).then(DATE_SEEKBY.toString())));
    }

    /**
     * @return april 3rd week, april first week second day , month 3rd week tuesday, month 20th, month 2nd monday, ...
     */
    private static Pattern relative_date_type2_1_1() {
        return match(anyOf(match(MONTH_NAME.toString()), match(DATE_SEEKBY.toString()))).
                thenMaybe(NUMBER.toString()).thenMaybe(anyOf(relative_date_type2_1_0(), match(WEEK_DAY.toString()), match(DATE_SEEKBY.toString()), match(DATE_SUFFIX.toString())));
    }

    /**
     * @return year april 3rd week, year april first week second day , year april 3rd week tuesday, year 9th month 20th, year 8th month 2nd monday, year 16th week second day, year 15th week , year 6th month, year 100th day, year 40th monday, ...
     */
    private static Pattern relative_date_type2_1_2() {
        return match(DATE_SEEKBY.toString()).thenMaybe(maybe(NUMBER.toString())
                .then(anyOf(match(DATE_SEEKBY.toString()), relative_date_type2_1_1(), relative_date_type2_1_0(), match(WEEK_DAY.toString()))));
    }

    private static Pattern relative_date_type2_1() {
        return match(RELATIVE_PREPOSITION.toString()).then(anyOf(relative_date_type2_1_0(), relative_date_type2_1_1(), relative_date_type2_1_2()));
    }

    /**
     * @return all next types
     */
    // TODO next Season
    private static Pattern relative_date_type2() {
        return match(anyOf(relative_date_type2_0(), relative_date_type2_1()));
    }

    /**
     * @return 6 weeks(days, months, mondays, years, aprils) from now(today)
     */
    private static Pattern relative_date_type3() {
        return match(NUMBER.toString()).then(anyOf(match(DATE_SEEKBY.toString()), match(WEEK_DAY.toString()), match(MONTH_NAME.toString()))).then(RELATIVE_SUFFIX.toString());
    }

    /**
     * @return 3 types of relative date structure
     */
    private static Pattern relative_date() {
        return match(anyOf(relative_date_type1(), relative_date_type2(), relative_date_type3()));
    }

    /**
     * @return the day after tomorrow, one week before sunday
     */
    private static Pattern global_date() {
        return match(maybe(NUMBER.toString()).then(DATE_SEEKBY.toString()).then(GLOBAL_PREPOSITION.toString()).then(anyOf(relative_date(), formal_date(), relax_date())));
    }

    /**
     * @return day, month, year, week
     */
    private static Pattern forever_seek() {
        return match(anyOf(DATE_SEEKBY.toString(), WEEK_DAY.toString()));
    }

    /**
     * @return every other day, every 2 weeks
     */
    //TODO add every week sundays,...
    private static Pattern forever_date() {
        return match(DATE_RECURRENCE.toString()).then(maybe(anyOf(DATE_FOREVER_KEY.toString(), NUMBER.toString()))).then(forever_seek()).then(maybe(DATE_RANGE.toString())).then(maybe(anyOf(relax_date(), relative_date(), formal_date())));
    }

    /**
     * @return till next month, from 12/3/2012 until june,..
     */
    private static Pattern limited_date() {
        return match(maybe(DATE_START_RANGE.toString()).then(maybe(anyOf(relax_date(), relative_date(), formal_date()))).then(DATE_RANGE.toString()).then(anyOf(relax_date(), relative_date(), formal_date())));
    }

    /**
     * @return now, this year, today, current year, the year, ...
     */
    private static Pattern year_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString()).then(YEAR_SEEK.toString())));
    }

    /**
     * @return next year, 2001, the year before 2025, year after next year, year before year 2013, year, ...
     */
    private static Pattern year_part() {
        return match(anyOf(match(RELATIVE_PREPOSITION.toString()).thenMaybe(YEAR_SEEK.toString()), maybe(THE_PREFIX.toString()).thenMaybe(YEAR_SEEK.toString()).then(NUMBER.toString()),
                year_part_relative(), year_part_current(), match(YEAR_SEEK.toString())));
    }


    /**
     * @return year after next, after 2001, year before 2025, three years after next year, the year before year 2013, 4 years from today
     */
    private static Pattern year_part_relative() {
        return match(maybe(anyOf(match(NUMBER.toString()), match(THE_PREFIX.toString()))).then(YEAR_SEEK.toString())
                .then(anyOf(match(GLOBAL_PREPOSITION.toString()), match(DATE_START_RANGE.toString()))).then(year_part()));
    }

    /**
     * @return now, this month, today, current month, the month, ...
     */
    private static Pattern month_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString()).then(MONTH_SEEK.toString())));
    }


    private static Pattern month_part_explicit() {
        return match(anyOf(match(NUMBER.toString()).then(MONTH_SEEK.toString()), match(MONTH_NAME.toString())).thenMaybe(match(DATE_PREPOSITION.toString()).then(year_part())));
    }

    /**
     * @return two months from june(month part), 3rd month after today, 3rd april from now, ...
     */
    private static Pattern month_part_relative() {
        return match(NUMBER.toString()).then(anyOf(match(MONTH_SEEK.toString()), match(MONTH_NAME.toString())).thenMaybe(anyOf(match(GLOBAL_PREPOSITION.toString()), match(DATE_START_RANGE.toString())).then(month_part())));
    }


    static Pattern month_part() {
        return match(anyOf(month_part_current(), maybe(THE_PREFIX.toString()).then(anyOf(month_part_explicit(), month_part_relative()))));
    }

    /**
     * @return now, this week, today, current week, the week, ...
     */
    private static Pattern week_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString()).then(WEEK_SEEK.toString())));
    }

    private static Pattern week_part_explicit() {
        return match(NUMBER.toString()).then(WEEK_SEEK.toString()).then(DATE_PREPOSITION.toString()).then(anyOf(year_part(), month_part()));

    }

    /**
     * @return two weeks from first week of next year(week part), 3rd week after today, ...
     */
    private static Pattern week_part_relative() {
        return match(NUMBER.toString()).then(anyOf(match(GLOBAL_PREPOSITION.toString()), match(DATE_START_RANGE.toString()))).then(week_part());
    }

    private static Pattern week_part() {
        return maybe(THE_PREFIX.toString()).then(anyOf(week_part_current(), week_part_explicit(), week_part_relative()));
    }
//    private static Pattern year_part(){
//        return match(anyOf(
//                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(YEAR_SEEK.toString()).then(maybe(RELATIVE_SUFFIX.toString())),
//                maybe(THE_PREFIX.toString()).then(maybe(YEAR_SEEK.toString())).then(NUMBER.toString())));
//    }
//    private static Pattern month_part(){
//        return match(anyOf(
//                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(anyOf(MONTH_SEEK.toString(),MONTH_NAME.toString())).then(maybe(RELATIVE_SUFFIX.toString())),
//                maybe(NUMBER.toString()).then(MONTH_SEEK.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(year_part())));
//    }
//
//    private static Pattern week_part(){
//        return match(anyOf(
//                match(maybe(anyOf(THE_PREFIX.toString(),NUMBER.toString(),RELATIVE_PREPOSITION.toString()))).then(anyOf(WEEK_SEEK.toString(),WEEK_DAY.toString())).then(maybe(RELATIVE_SUFFIX.toString())),
//                maybe(NUMBER.toString()).then(WEEK_SEEK.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(month_part())).then(maybe(year_part())));
//
//    }
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


        //TODO debug explicite relative date
    private static Pattern date_rules(){
        return match(anyOf(formal_date(),relax_date(),relative_date(),/*explicit_relative_date(),*/global_date()));
    }
//    //TODO add lists
//    private static Pattern recurrence(){
//        return match(anyOf(forever_date(),limited_date()/*,lists()*/));
//    }
    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(FORMAL_DATE, formal_date())
                , newPattern(RELAX_DATE, relax_date())
                , newPattern(RELATIVE_DATE, relative_date())
//                ,newPattern(EXPLICIT_RELATIVE_DATE,explicit_relative_date())
                , newPattern(GLOBAL_DATE, global_date())
                , newPattern(FOREVER_DATE, forever_date())
                , newPattern(LIMITED_DATE, limited_date())

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
