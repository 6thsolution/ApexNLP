package com.sixthsolution.apex.nlp.ner;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public enum Label {
    NONE, DATE, TIME, LOCATION, TITLE,
    FIXED_TIME,
    RELATIVE_TIME,
    RANGE_TIME,
    /**
     * Formal dates are those in which the month, day, and year are represented as integers
     * separated by a common separator character. The year is optional and may proceed the month or
     * succeed the day of month. If a two-digit year is given, it must succeed the day of month.
     */
    FORMAL_DATE,
    RELAX_DATE,
    RELATIVE_DATE,
    GLOBAL_DATE,
    FOREVER_DATE,
    LIMITED_DATE,


}
