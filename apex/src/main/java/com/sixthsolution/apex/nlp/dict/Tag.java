package com.sixthsolution.apex.nlp.dict;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
public enum Tag {
    NONE(97),
    NUMBER(98),
    PREPOSITION(99),
    //LOCATION
    LOCATION_PREFIX(100),
    LOCATION_SUFFIX(101),
    //TIME
    TIME_PREFIX(102),                //e.g. at, in ,the
    TIME_START_RANGE(103),       //e.g. from
    TIME_RANGE(104),                 //e.g. till, until, -
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
    //RECURRENCE
    REC_WEEK_DAYS(121);

    public int id;

    Tag(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf((char) id);
    }
}
