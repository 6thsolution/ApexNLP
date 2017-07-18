package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishParser;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;

import static com.sixthsolution.apex.nlp.test.ParserAssertion.assertEvent;
import static com.sixthsolution.apex.nlp.test.ParserAssertion.init;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EventTest {
    int year;
    int month;
    int day;
    int hour;
    int min;


    @Before
    public void setUp() {
        year = LocalDateTime.now().getYear();
        month = LocalDateTime.now().getMonthValue();
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
        min = LocalDateTime.now().getMinute();
        System.out.println(LocalDateTime.now());
        System.out.println(year + "/" + month + "/" + day + " " + hour + ":" + min);
        init(LocalDateTime.of(year, Integer.valueOf(check_zero(String.valueOf(month))), Integer.valueOf(check_zero(String.valueOf(day))), Integer.valueOf(check_zero(String.valueOf(hour))), Integer.valueOf(check_zero(String.valueOf(min)))), new EnglishParser());
    }

    @Test
    public void test_time_extraction() {
        //formal
        assertEvent("in the evening").startTime("16:00").endTime("17:00");
        //range
        assertEvent("at morning - 9 pm").startTime("09:00").endTime("21:00");
        assertEvent("at 5pm till 6pm").startTime("17:00").endTime("18:00");
        assertEvent("at 5-6pm").startTime("17:00").endTime("18:00");
        assertEvent("at nine till eleven").startTime("09:00").endTime("11:00");
        assertEvent("at 5am to 6pm").startTime("05:00").endTime("18:00");
        assertEvent("at 9:30 to 10:30").startTime("09:30").endTime("10:30");
        //relative
        assertEvent("for 2 hours before noon").startTime("10:00").endTime("12:00");
        assertEvent("for one hour").startTime(check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min))).endTime(check_zero(String.valueOf(hour+1))+":"+check_zero(String.valueOf(min)));
        //TODO no idea how to support
        //        assertEvent("from 11.5 - 12.5 ").startTime("11:")
    }

    @Test
    public void test_date_extraction() {
        //formal date
        assertEvent("15/apr/2012").startDate("2012-04-15").endDate("2012-04-15");
        assertEvent("2017/04/10").startDate("2017-04-10").endDate("2017-04-10");
        assertEvent("15/apr/2012").start("2012/04/15 " + check_zero(String.valueOf(hour)) + ":" + check_zero(String.valueOf(min))).end("2012/04/15 " + check_zero(String.valueOf(hour + 1)) + ":" + check_zero(String.valueOf(min)));
        assertEvent("2017/04/10").start("2017/04/10 " + check_zero(String.valueOf(hour)) + ":" + check_zero(String.valueOf(min))).end("2017/04/10 " + check_zero(String.valueOf(hour + 1)) + ":" + check_zero(String.valueOf(min)));
        //TODO fix this format
        assertEvent("12/09").startDate("2017-09-12").endDate("2017-09-12");

        //limited
        //TODO fix confusion of limited date with time
        assertEvent("from 7/15/2017 until 8/15/2017").startDate("2017-07-15").endDate("2017-08-15");
//        assertEvent("till 2017/08/10").startDate("2017-07-15").endDate("2017-08-10");
//        assertEvent("till 2017/08/10").start("2017/07/15 "+check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min)) );
        assertEvent("until 15/apr/2012").endDate("2012-04-15");
        assertEvent("till 2017/04/10").end("2017/04/10 " + check_zero(String.valueOf(hour + 1)) + ":" + check_zero(String.valueOf(min)));
        assertEvent("until 15/apr/2012").end("2012/04/15 " + check_zero(String.valueOf(hour + 1)) + ":" + check_zero(String.valueOf(min)));
        //TODO fix startDate of limited date

        //relax
        assertEvent("april").startDate(year+"-04-"+check_zero(String.valueOf(day))).endDate(year+"-04-"+check_zero(String.valueOf(day)));
        assertEvent("apr").start(year+"/04/"+check_zero(String.valueOf(day))+" "+check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min)));
        assertEvent("jan 20").startDate(year+"-01-20").endDate(year+"-01-20");
        assertEvent("april 20").start(year+"/04/20 "+check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min))).end(year+"/04/20 "+check_zero(String.valueOf(hour+1))+":"+check_zero(String.valueOf(min)));
        assertEvent("december 2012").startDate("2012"+"-12-"+day).endDate("2012"+"-12-"+day);
//        assertEvent("april 20 of 2012").startDate("2012-04-20");
        assertEvent("20 jan ").startDate(year+"-01-20").endDate(year+"-01-20");
        assertEvent("20 jan 2012").startDate("2012"+"-01-20").endDate("2012"+"-01-20");
        assertEvent("20 of jan").startDate(year+"-01-20").endDate(year+"-01-20");
        assertEvent("20 of jan").start(year+"/01/20 "+check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min))).end(year+"/01/20 "+check_zero(String.valueOf(hour+1))+":"+check_zero(String.valueOf(min)));
    }


    @Test
    public void test_full_sentence() {
//        assertEvent("Grocery shopping at Wegmans Thursday at 5pm")
//                .location("Wegmans")
//                .startTime("17:00")
//                .endTime("18:00");
        assertEvent("12/09 Meet John at Mall from 9:30 to 12:00")
                .location("Mall")
                .startTime("09:30")
                .endTime("12:00");
//        assertEvent("Meet John on monday at Mall")
//                .location("Mall")
//                .start("2017/04/10 09:30")
//                .end("2017/04/10 10:30");
//        assertEvent("Family Dine Out on the 2nd Friday of every month at 6-9p")
//                .startTime("18:00")
//                .endTime("21:00");
//        assertEvent("Mission Trip at Jakarta on Nov 13-17 calendar Church")
//                .location("Jakarta")
//                .startTime(check_zero(String.valueOf(hour))+":"+check_zero(String.valueOf(min)))
//                .endTime(check_zero(String.valueOf(hour+1))+":"+check_zero(String.valueOf(min)));
        assertEvent("Wash the Car at Mall at 8.45pm 5/12/13")
                .location("Mall")
                .start("2013/05/12 20:45")
                .end("2013/05/12 21:45");
//        assertEvent("meeting with Tom for two hours after noon")
//                .start("2017/07/15 12:00").end("2017/07/15 14:00");
    }

    public String check_zero(String digit) {
        if (digit.length() == 1)
            return "0" + digit;
        else
            return digit;
    }

}
