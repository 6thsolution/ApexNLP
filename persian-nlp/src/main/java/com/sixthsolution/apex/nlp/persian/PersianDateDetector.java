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
    private static Pattern month_name() {
        return match(MONTH_NAME.toString());
    }

    /**
     * @return shanbe, yekshanbe...
     */
    private static Pattern week_day() {
        return match(WEEK_DAY.toString());
    }

    /**
     * @return 20 of tir 2012, shanbe, tir
     */
    private static Pattern relax_date() {
        return match(anyOf(month_name(), relax_date_type2(), week_day()));
    }


    /**
     * @return emshab, farda, pasfarda, ...
     */
    private static Pattern relative_date_type1() {
        return match(NAMED_DATE.toString());
    }

    /**
     * @return shanbe bad, ...
     */
    private static Pattern relative_date_type2_0() {
        return match(WEEK_DAY.toString()).then(RELATIVE_PREPOSITION.toString());
    }

    /**
     * @return hafte bad sevomin rooz , hafte bad shanbe
     */
    private static Pattern relative_date_type2_1_0() {
        return match(DATE_SEEKBY.toString()).then(RELATIVE_PREPOSITION.toString()).thenMaybe(anyOf(match(WEEK_DAY.toString()), match(NUMBER.toString()).thenMaybe(DATE_SUFFIX.toString()).then(DATE_SEEKBY.toString())));
    }

    /**
     * @return farvardin bad sevomin hafte, farvardin badi avalin hafte dovomin rooz , mah bad sevomin hafte seshanbe, mah bad 20 om, mah bad dovomin shanbe, ...
     */
    private static Pattern relative_date_type2_1_1() {
        return match(anyOf(match(MONTH_NAME.toString()), match(DATE_SEEKBY.toString()))).then(RELATIVE_PREPOSITION.toString()).
                thenMaybe(NUMBER.toString()).thenMaybe(anyOf(relative_date_type2_1_0(), match(WEEK_DAY.toString()), match(DATE_SEEKBY.toString()), match(DATE_SUFFIX.toString())));
    }

    /**
     * @return sal bad farvardin sevomin hafte, sal bad farvardin avalin hafte sevomin rooz , sal bad farvardin sevomin hafte shanbe, sal bad sevomin mah 20om, sal bad hashtomin mah dovomin shanbe, sal bad 16 omin hafte dovomin rooz, sal bad 17 omin hafte , sal bad 9omin mah, sal bad sadomin rooz, sal bad chehlomin hafte, ...
     */
    private static Pattern relative_date_type2_1_2() {
        return match(DATE_SEEKBY.toString()).then(RELATIVE_PREPOSITION.toString()).thenMaybe(maybe(NUMBER.toString())
                .then(anyOf(match(DATE_SEEKBY.toString()), relative_date_type2_1_1(), relative_date_type2_1_0(), match(WEEK_DAY.toString()))));
    }

    private static Pattern relative_date_type2_1() {
        return match(anyOf(relative_date_type2_1_0(), relative_date_type2_1_1(), relative_date_type2_1_2()));
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
        return match(maybe(PREPOSITION.toString()).then(NUMBER.toString()).then(anyOf(match(DATE_SEEKBY.toString()), match(WEEK_DAY.toString()), match(MONTH_NAME.toString()))).then(RELATIVE_SUFFIX.toString()));
    }

    /**
     * @return 3 types of relative date structure
     */
    private static Pattern relative_date() {
        return match(anyOf(relative_date_type1(), relative_date_type2(), relative_date_type3()));
    }

    /**
     * @return rooze bad az shanbe
     */
    private static Pattern global_date() {
        return match(maybe(NUMBER.toString()).then(DATE_SEEKBY.toString()).then(GLOBAL_PREPOSITION.toString()).then(anyOf(relative_date(), formal_date(), relax_date())));
    }

    /**
     * @return rooz mah sal
     */
    private static Pattern forever_seek() {
        return match(anyOf(DATE_SEEKBY.toString(), WEEK_DAY.toString()));
    }

    /**
     * @return har rooz,har mah ta sale bad
     */
    //TODO add every week sundays,...
    private static Pattern forever_date() {
        return match(DATE_RECURRENCE.toString()).then(maybe(NUMBER.toString())).then(forever_seek()).then(maybe(DATE_RANGE.toString())).then(maybe(anyOf(relax_date(), relative_date(), formal_date())));
    }

    /**
     * @return ta mah bad, az 12/3/2012 ta tir  ,..
     */
    private static Pattern limited_date() {
        return match(maybe(DATE_PREFIX.toString()).then(maybe(anyOf(relax_date(), relative_date(), formal_date()))).then(DATE_RANGE.toString()).then(anyOf(relax_date(), relative_date(), formal_date())));
    }


    /**
     * @return alan, emrooz, emsal, in mah,in hafte,...
     */
    private static Pattern year_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString())).thenMaybe(DATE_SEEKBY.toString()));
    }

    /**
     * @return sal bad, 2001, the year before 2025, year after next year, year before year 2013, year, ...
     */
    private static Pattern year_part() {
        return match(anyOf(match(DATE_SEEKBY.toString()).then(GLOBAL_PREPOSITION.toString()), maybe(THE_PREFIX.toString()).thenMaybe(DATE_SEEKBY.toString()).then(NUMBER.toString()),
                year_part_relative(), year_part_current(), match(DATE_SEEKBY.toString())));
    }

    private static Pattern year_part_exact() {
        return match(anyOf(match(DATE_SEEKBY.toString()).then(RELATIVE_PREPOSITION.toString()), maybe(THE_PREFIX.toString())
                .thenMaybe(DATE_SEEKBY.toString()).then(NUMBER.toString()), year_part_current(), match(DATE_SEEKBY.toString())));
    }

    /**
     * @return year after next, after 2001, year before 2025, three years after next year, the year before year 2013, 4 years from today
     */
    private static Pattern year_part_relative() {
        return match(maybe(anyOf(match(NUMBER.toString()), match(THE_PREFIX.toString()))).then(DATE_SEEKBY.toString())
                .then(anyOf(match(GLOBAL_PREPOSITION.toString()), match(DATE_PREFIX.toString()))).then(year_part_exact()));
    }

    /**
     * @return now, this month, today, current month, the month, ...
     */
    private static Pattern month_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString()).then(DATE_SEEKBY.toString())));
    }

    private static Pattern month_part_explicit() {
        return match(anyOf(match(NUMBER.toString()).then(DATE_SEEKBY.toString()), match(MONTH_NAME.toString())).thenMaybe(maybe(DATE_PREFIX.toString()).then(year_part())));
    }

    static Pattern month_part_exact() {
        return match(anyOf(month_part_current(), maybe(THE_PREFIX.toString()).then(anyOf(month_part_explicit()))));
    }

    /**
     * @return two months from june(month part), 3rd month after today, 3rd april from now, ...
     */
    private static Pattern month_part_relative() {
        return match(NUMBER.toString()).then(anyOf(match(DATE_SEEKBY.toString()), match(MONTH_NAME.toString()))
                .thenMaybe(anyOf(match(GLOBAL_PREPOSITION.toString()), maybe(DATE_PREFIX.toString())).then(month_part_exact())));
    }

    static Pattern month_part() {
        return match(anyOf(match(DATE_SEEKBY.toString()).then(DATE_SEEKBY.toString()), month_part_current(), maybe(THE_PREFIX.toString()).then(anyOf(month_part_explicit(), month_part_relative()))));
    }

    /**
     * @return now, this week, today, current week, the week, ...
     */
    private static Pattern week_part_current() {
        return match(anyOf(match(CURRENT.toString()), match(THE_PREFIX.toString()).then(DATE_SEEKBY.toString())));
    }

    private static Pattern week_part_explicit() {
        return match(NUMBER.toString()).then(DATE_SEEKBY.toString()).thenMaybe(DATE_PREFIX.toString()).then(anyOf(year_part(), month_part()));
    }

    private static Pattern week_part_exact() {
        return maybe(THE_PREFIX.toString()).then(anyOf(week_part_current(), week_part_explicit()));
    }

    /**
     * @return two weeks from first week of next year(week part), 3rd week after today, ...
     */
    private static Pattern week_part_relative() {
        return match(NUMBER.toString()).then(anyOf(match(GLOBAL_PREPOSITION.toString()), maybe(DATE_PREFIX.toString()))).then(week_part_exact());
    }

    private static Pattern week_part() {
        return maybe(THE_PREFIX.toString()).then(anyOf(week_part_current(), week_part_explicit(), week_part_relative()));
    }

    private static Pattern start_with_number() {
        return match(NUMBER.toString()).thenMaybe(DATE_SUFFIX.toString()).then(DATE_SEEKBY.toString()).thenMaybe(DATE_PREFIX.toString()).then(anyOf(year_part(), month_part(), week_part()));
    }


    private static Pattern start_with_day_band() {
        return match(DATE_BAND.toString()).thenMaybe(anyOf(match(DATE_SEEKBY.toString()), match(WEEK_DAY.toString()), match(MONTH_NAME.toString())))
                .thenMaybe(DATE_PREFIX.toString()).then(anyOf(year_part(), month_part(), week_part()));
    }

    private static Pattern start_with_day_of_week() {
        return match(WEEK_DAY.toString()).then(maybe(DATE_PREFIX.toString())).then(anyOf(week_part(), year_part(), month_part()));
    }

    //TODO add DATE_BAND for second parts
    private static Pattern explicit_relative_date() {
        return match(anyOf(start_with_day_band(), start_with_day_of_week(), start_with_number()));
    }


    @Override
    protected List<Pair<Label, Pattern>> getPatterns() {
        return Arrays.asList(
                newPattern(FORMAL_DATE, formal_date())
                , newPattern(RELAX_DATE, relax_date())
                , newPattern(RELATIVE_DATE, relative_date())
                , newPattern(EXPLICIT_RELATIVE_DATE, explicit_relative_date())
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
