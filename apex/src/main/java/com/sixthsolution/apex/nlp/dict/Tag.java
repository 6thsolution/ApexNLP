package com.sixthsolution.apex.nlp.dict;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
public enum Tag {
    NONE(97),
    NUMBER(98),
    PREPOSITION(99),
    RELATIVE_PREPOSITION(125),
    RELATIVE_SUFFIX(126),
    //LOCATION
    LOCATION_PREFIX(100),
    LOCATION_SUFFIX(101),
    //TIME
    TIME_PREFIX(102),                //e.g. at, in ,the
    TIME_START_RANGE(103),       //e.g. from
    TIME_RANGE(104),
    TIME_RELATIVE_PREFIX(122),   //e.g. for
    TIME_RELATIVE(105),              //e.g. morning
    TIME_RELATIVE_INDICATOR(106),   //e.g. before,after
    TIME_HOUR(107),                 //e.g. hour
    TIME_MIN(108),                   //e.g. minutes
    TIME_SEC(109),                   //e.g. seconds
    TIME_MERIDIEM(110),            //e.g am, pm
    TIME_SEPARATOR(111),             //e.g :, .
    //DATE
    DATE_PREPOSITION(112),
    DATE_SEEKBY(113),
    DATE_START_RANGE(114),
    DATE_SUFFIX(115),
    DATE_DURATION_SUFFIX(116),
    DATE_SEPARATOR(117),
    WEEK_DAY(118),
    MONTH_NAME(119),
    SEASON(120),
    DATE_PREFIX(123),
    //RECURRENCE
    REC_WEEK_DAYS(121),
    NAMED_DATE(124),
    GLOBAL_PREPOSITION(126),
    DATE_RECURRENCE(127),
    DATE_RANGE(128),
    DATE_FOREVER_KEY(129);

    public int id;

    Tag(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf((char) id);
    }
}
