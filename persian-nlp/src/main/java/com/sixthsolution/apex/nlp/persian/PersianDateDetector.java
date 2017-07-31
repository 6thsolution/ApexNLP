package com.sixthsolution.apex.nlp.persian;

import com.nobigsoftware.dfalex.Pattern;
import com.sixthsolution.apex.nlp.ner.Entity;
import com.sixthsolution.apex.nlp.ner.Label;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetectionFilter;
import com.sixthsolution.apex.nlp.ner.regex.ChunkDetector;
import com.sixthsolution.apex.nlp.persian.filter.DateDetectionFilter;
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

public class PersianDateDetector extends ChunkDetector {

    /**
     * @return 1394/5/1, 94/5/2, 1394/farvardin/2,
     */
    private static Pattern formal_date() {
        return match(NUMBER.toString()).then(DATE_SEPARATOR.toString())
                .then(anyOf(NUMBER.toString(), MONTH_NAME.toString()))
                .then(maybe(DATE_SEPARATOR.toString()))
                .then(maybe(NUMBER.toString()));
    }

//    /**
//     * @return farvardin, tir 20, tir 20om, april 20 2012
//     */
//    private static Pattern relax_date_type1() {
//        return month_name().then(maybe(NUMBER.toString()).then(maybe(DATE_SUFFIX.toString())).then(maybe(DATE_PREFIX.toString())).then(maybe(NUMBER.toString())));
//    }

    /**
     * @return 20 tir, 20om azar,  20 azar 1393
     */
    private static Pattern relax_date_type2() {
        return match(NUMBER.toString()).then(maybe(DATE_SUFFIX.toString())).then(maybe(DATE_PREFIX.toString())).then(month_name()).then(maybe(NUMBER.toString()));
    }

    /**
     * @return tir, aban...
     */
    private static Pattern month_name(){
        return match(MONTH_NAME.toString());
    }

    /**
     * @return shanbe, yekshanbe...
     */
    private static Pattern week_day(){
        return match(WEEK_DAY.toString());
    }

    /**
     * @return  20 of tir 2012, shanbe, tir
     */
    private static Pattern relax_date(){
        return match(anyOf(month_name(),relax_date_type2(),week_day()));
    }


    /**
     * @return emshab, farda, pasfarda, ...
     */
    private static Pattern relative_date_type1(){
        return match(NAMED_DATE.toString());
    }

    /**
     * @return shanbe bad, ...
     */
    private static Pattern relative_date_type2_0(){
        return match(WEEK_DAY.toString()).then(RELATIVE_PREPOSITION.toString());
    }

    /**
     * @return hafte bad sevomin rooz , hafte bad shanbe
     */
    private static Pattern relative_date_type2_1_0(){
        return match(DATE_SEEKBY.toString()).then(RELATIVE_PREPOSITION.toString()).thenMaybe(anyOf(match(WEEK_DAY.toString()), match(NUMBER.toString()).thenMaybe(DATE_SUFFIX.toString()).then(DATE_SEEKBY.toString())));
    }

    /**
     * @return april 3rd week, april first week second day , month 3rd week tuesday, month 20th, month 2nd monday, ...
     */
    private static Pattern relative_date_type2_1_1(){
        return match(anyOf(match(MONTH_NAME.toString()), match(DATE_SEEKBY.toString()))).
                thenMaybe(NUMBER.toString()).thenMaybe(anyOf(relative_date_type2_1_0(), match(WEEK_DAY.toString()), match(DATE_SEEKBY.toString()), match(DATE_SUFFIX.toString())));
    }

    /**
     * @return year april 3rd week, year april first week second day , year april 3rd week tuesday, year 9th month 20th, year 8th month 2nd monday, year 16th week second day, year 15th week , year 6th month, year 100th day, year 40th monday, ...
     */
    private static Pattern relative_date_type2_1_2(){
        return match(DATE_SEEKBY.toString()).thenMaybe(maybe(NUMBER.toString())
                .then(anyOf(match(DATE_SEEKBY.toString()), relative_date_type2_1_1(), relative_date_type2_1_0(), match(WEEK_DAY.toString()))));
    }

    private static Pattern relative_date_type2_1(){
        return match(RELATIVE_PREPOSITION.toString()).then(anyOf(relative_date_type2_1_0(),relative_date_type2_1_1(),relative_date_type2_1_2()));
    }

    /**
     * @return all next types
     */
    // TODO next Season
    private static Pattern relative_date_type2(){
        return match(anyOf(relative_date_type2_0(),relative_date_type2_1()));
    }

    /**
     * @return 6 weeks(days, months, mondays, years, aprils) from now(today)
     */
    private static Pattern relative_date_type3(){
        return match(NUMBER.toString()).then(anyOf(match(DATE_SEEKBY.toString()), match(WEEK_DAY.toString()), match(MONTH_NAME.toString()))).then(RELATIVE_SUFFIX.toString());
    }

    /**
     * @return 3 types of relative date structure
     */
    private static Pattern relative_date(){
        return match(anyOf(relative_date_type1(),relative_date_type2(), relative_date_type3()));
    }
    /**
     * @return rooze bad az shanbe
     */
    private static Pattern global_date(){
        return match(maybe(NUMBER.toString()).then(DATE_SEEKBY.toString()).then(GLOBAL_PREPOSITION.toString()).then(anyOf(relative_date(),formal_date(),relax_date())));
    }

    /**
     * @return rooz mah sal
     */
    private static Pattern forever_seek(){
        return match(anyOf(DATE_SEEKBY.toString(),WEEK_DAY.toString()));
    }
    /**
     * @return har rooz,har mah ta sale bad
     */
    //TODO add every week sundays,...
    private static Pattern forever_date(){
        return match(DATE_RECURRENCE.toString()).then(maybe(NUMBER.toString())).then(forever_seek()).then(maybe(DATE_RANGE.toString())).then(maybe(anyOf(relax_date(),relative_date(),formal_date())));
    }

    /**
     * @return ta mah bad, az 12/3/2012 ta tir  ,..
     */
    private static Pattern limited_date(){
        return match(maybe(DATE_START_RANGE.toString()).then(maybe(anyOf(relax_date(),relative_date(),formal_date()))).then(DATE_RANGE.toString()).then(anyOf(relax_date(),relative_date(),formal_date())));
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


    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(FORMAL_DATE, formal_date())
                ,newPattern(RELAX_DATE, relax_date())
                ,newPattern(RELATIVE_DATE, relative_date())
//                ,newPattern(EXPLICIT_RELATIVE_DATE,explicit_relative_date())
                ,newPattern(GLOBAL_DATE, global_date())
                ,newPattern(FOREVER_DATE,forever_date())
                ,newPattern(LIMITED_DATE,limited_date())

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
