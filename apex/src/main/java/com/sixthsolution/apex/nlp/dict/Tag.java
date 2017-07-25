package com.sixthsolution.apex.nlp.dict;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */
public enum Tag {
    NONE(97),
    NUMBER(98),
    PREPOSITION(99),
    RELATIVE_PREPOSITION(100),
    RELATIVE_SUFFIX(101),
    //LOCATION
    LOCATION_PREFIX(102),
    LOCATION_SUFFIX(103),
    //TIME
    TIME_PREFIX(104),                //e.g. at, in ,the
    TIME_START_RANGE(105),       //e.g. from
    TIME_RANGE(106),
    TIME_RELATIVE_PREFIX(107),   //e.g. for
    TIME_RELATIVE(108),              //e.g. morning
    TIME_RELATIVE_INDICATOR(109),   //e.g. before,after
    TIME_HOUR(110),                 //e.g. hour
    TIME_MIN(111),                   //e.g. minutes
    TIME_SEC(112),                   //e.g. seconds
    TIME_MERIDIEM(113),            //e.g am, pm
    TIME_SEPARATOR(114),             //e.g :, .
    //DATE
    DATE_PREPOSITION(115),
    DATE_SEEKBY(116),
    DATE_START_RANGE(117),
    DATE_SUFFIX(118),
    DATE_DURATION_SUFFIX(119),
    DATE_SEPARATOR(120),
    WEEK_DAY(121),
    MONTH_NAME(122),
    SEASON(123),
    DATE_PREFIX(124),
    //RECURRENCE
    REC_WEEK_DAYS(125),
    NAMED_DATE(126),
    GLOBAL_PREPOSITION(127),
    DATE_RECURRENCE(128),
    DATE_RANGE(129),
    DATE_FOREVER_KEY(130),
    THE_PREFIX(131),
    DATE_BAND(132),
    YEAR_SEEK(133),
    MONTH_SEEK(134),
    WEEK_SEEK(135),
    DAY_SEEK(136);

    public int id;

    Tag(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf((char) id);
    }
}
