package com.sixthsolution.apex.nlp.english;

import com.sixthsolution.apex.nlp.dict.Dictionary;
import com.sixthsolution.apex.nlp.dict.DictionaryBuilder;

import static com.sixthsolution.apex.nlp.dict.Tag.DATE_DURATION_SUFFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_PREPOSITION;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SEEKBY;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_START_RANGE;
import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SUFFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.LOCATION_PREFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.LOCATION_SUFFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.MONTH_NAME;
import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;
import static com.sixthsolution.apex.nlp.dict.Tag.PREPOSITION;
import static com.sixthsolution.apex.nlp.dict.Tag.REC_WEEK_DAYS;
import static com.sixthsolution.apex.nlp.dict.Tag.SEASON;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_HOUR;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MERIDIEM;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MIN;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_PREFIX;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RANGE;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RELATIVE;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RELATIVE_INDICATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_SEC;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_START_RANGE;
import static com.sixthsolution.apex.nlp.dict.Tag.WEEK_DAY;
import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Entity.LOCATION;
import static com.sixthsolution.apex.nlp.ner.Entity.NONE;
import static com.sixthsolution.apex.nlp.ner.Entity.TIME;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public final class EnglishVocabulary {
    public static Dictionary build() {
        DictionaryBuilder vb = new DictionaryBuilder();
        //***************************************
        //WEEK DAYS
        //***************************************
        vb.tag(WEEK_DAY, DATE)
                .e(1, "monday", "mon", "mon.")
                .e(2, "tuesday", "tue", "tue.")
                .e(3, "wednesday", "wed", "wed.")
                .e(4, "thursday", "thur", "thur.", "thu", "thu.", "thus", "thus.")
                .e(5, "friday", "fri", "fri.")
                .e(6, "saturday", "sat", "sat.")
                .e(7, "sunday", "sun", "sun.");
        //***************************************
        //MONTH_NAME NAMES
        //***************************************
        vb.tag(MONTH_NAME, DATE)
                .e(1, "january", "jan", "jan.")
                .e(2, "february", "feb", "feb.")
                .e(3, "march", "mar", "mar.")
                .e(4, "april", "apr", "apr.")
                .e(5, "may")
                .e(6, "june", "jun", "jun.")
                .e(7, "july", "jul", "jul.")
                .e(8, "august", "aug", "aug.")
                .e(9, "september", "sep", "sep.", "sept", "sept.")
                .e(10, "october", "oct", "oct.")
                .e(11, "november", "nov", "novembers", "nov.")
                .e(12, "december", "dec", "dec.");
        //***************************************
        //TIME
        //***************************************
        vb.tag(TIME_PREFIX, TIME)
                .e("at", "in");
        vb.tag(TIME_RANGE, TIME)
                .e("-");
        vb.tag(TIME_RELATIVE, TIME)
                .e(9, "morning")
                .e(12, "noon")
                .e(16, "in_the_evening", "in_the_afternoon", "in_the_after-noon")
                .e(21, "night")
                .e(23, "midnight", "mid-night")
                .e(0, "now");//TODO current time
        vb.tag(TIME_RELATIVE_INDICATOR, TIME)
                .e("before", "after");
        vb.tag(TIME_START_RANGE, TIME)
                .e("from");
        vb.tag(TIME_RANGE, TIME)
                .e("till", "until", "-", "for", "to","during");
        vb.tag(TIME_SEPARATOR, TIME)
                .e(".", ":");
        vb.tag(TIME_MERIDIEM, TIME)
                .e(0, "am", "a.m", "a.m.", "a_m", "a")
                .e(12, "pm", "p.m.", "p.m", "p_m", "p");
        vb.tag(TIME_HOUR, TIME)
                .e("hour", "hours", "hr", "hr.", "hrs", "hrs.", "h");
        vb.tag(TIME_MIN, TIME)
                .e("minutes", "min", "min.", "mins", "mins.");
        vb.tag(TIME_SEC, TIME)
                .e("second", "seconds", "sec", "secs");

        //***************************************
        //DATE
        //***************************************
        vb.tag(DATE_SEPARATOR, DATE)
                .e("/", "-", ".");
        vb.tag(DATE_PREPOSITION, DATE)
                .e("on");
        vb.tag(DATE_SEEKBY, DATE)
                .e("day", "days")
                .e("week", "weeks", "wks", "wks.")
                .e("month", "months")
                .e("year", "years", "yrs");
        vb.tag(DATE_START_RANGE, DATE)
                .e("from", "starts");
        vb.tag(DATE_SUFFIX, DATE)
                .e("th", "from");
        vb.tag(DATE_DURATION_SUFFIX, DATE)
                .e("day", "days");
        //***************************************
        //SEASONS
        //***************************************
        vb.tag(SEASON, DATE)
                .e(1, "spring")
                .e(2, "summer")
                .e(3, "autumn", "fall")
                .e(4, "winter");
        //***************************************
        //RECURRENCES
        //***************************************
        vb.tag(REC_WEEK_DAYS, DATE)
                .e(1, "mondays", "mons", "mons.")
                .e(2, "tuesdays", "tues", "tues.")
                .e(3, "wednesdays", "weds", "weds.")
                .e(4, "thursdays", "thurs", "thurs.", "thus", "thus.")
                .e(5, "fridays", "fris", "fris.")
                .e(6, "saturdays", "sats", "sats.")
                .e(7, "sundays", "suns", "suns.");
        //***************************************
        //NUMBERS
        //***************************************
        vb.tag(NUMBER, NONE)
                .e(0, "zero")
                .e(1, "one", "first", "a", "an")
                .e(2, "two", "second")
                .e(3, "three", "third")
                .e(4, "four", "fourth")
                .e(5, "five", "fifth")
                .e(6, "six", "sixth")
                .e(7, "seven", "seventh")
                .e(8, "eight", "eighth")
                .e(9, "nine", "ninth")
                .e(10, "ten", "tenth")
                .e(11, "eleven", "eleventh")
                .e(12, "twelve", "twelfth")
                .e(13, "thirteen", "thirteenth")
                .e(14, "fourteen", "fourteenth")
                .e(15, "fifteen", "fifteenth")
                .e(16, "sixteen", "sixteenth")
                .e(17, "seventeen", "seventeenth")
                .e(18, "eighteen", "eightteen", "eighteenth")
                .e(19, "nineteen", "nineteenth")
                .e(20, "twenty", "twentieth")
                .e(30, "thirty", "thirtieth");
        //***************************************
        //Location
        //***************************************
        vb.tag(LOCATION_PREFIX, LOCATION)
                .e("at");
        vb.tag(LOCATION_SUFFIX, LOCATION)
                .e("st", "st.", "street");
        //***************************************
        //Others
        //***************************************
        vb.tag(PREPOSITION, NONE)
                .e("on", "in", "at", "of", "to", "with");

        return vb.build();
    }
}
