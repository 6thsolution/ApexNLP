//package com.sixthsolution.apex.nlp.english.test.tokenization;
//
//import com.sixthsolution.apex.nlp.english.EnglishTokenizer;
//import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
//import StandardRegExChunker;
//import com.sixthsolution.apex.nlp.tagger.StandardTagger;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static com.sixthsolution.apex.nlp.test.ChunkerAssertion.assertSentence;
//import static com.sixthsolution.apex.nlp.test.ChunkerAssertion.init;
//
//
///**
// * @author Saeed Masoumi (s-masoumi@live.com)
// */
//
//public class EnglishChunkerTest {
//
//
//    @Before
//    public void setUp() throws Exception {
//        init(new EnglishTokenizer(),
//                new StandardTagger(EnglishVocabulary.build()), new StandardRegExChunker());
//    }
//
//    @Test
//    public void validate_fixed_time_chunk_detection() {
//        assertSentence("7").hasNoTimeChunk();
//        assertSentence("1400").hasNoTimeChunk();//TODO can be a time chunk
//        assertSentence("7 am").hasTimeChunk("7 am");
//        assertSentence("7am").hasTimeChunk("7 am");
//        assertSentence("930am").hasTimeChunk("930 am");
//        assertSentence("at 7am").hasTimeChunk("at 7 am");
//        assertSentence("at 23:45").hasTimeChunk("at 23 : 45");
//        assertSentence("23:45").hasTimeChunk("23 : 45");
//        assertSentence("at four").hasTimeChunk("at four");
//    }
//
//    @Test
//    public void validate_ranged_time_chunk_detection() {
//        assertSentence("1400 - 1600").hasTimeChunk("1400 - 1600");
//        assertSentence("at 5-6pm").hasTimeChunk("at 5 - 6 pm");
//        assertSentence("at nine till eleven").hasTimeChunk("at nine till eleven");
//        assertSentence("from 5pm to 6pm").hasTimeChunk("from 5 pm to 6 pm");
//        assertSentence("from 9 : 30 to 12 : 00").hasTimeChunk("from 9 : 30 to 12 : 00");
//        assertSentence("from 9:30 till 12:00").hasTimeChunk("from 9 : 30 till 12 : 00");

//        assertSentence("at nine for 5 hours").hasTimeChunk("at nine for 5 hours");
//        assertSentence("at noon for 30 min").hasTimeChunk("at noon for 30 min");
//        assertSentence("in one hour").hasTimeChunk("in one hour");
//        assertSentence("from morning till 12 pm").hasTimeChunk("from morning till 12 pm");
//        assertSentence("from now for 2 hour").hasTimeChunk("from now for 2 hour");
//        assertSentence("5 minutes before nine").hasTimeChunk("5 minutes before nine");
//        assertSentence("an hour after noon").hasTimeChunk("an hour after noon");
//    }
//
//    @Test
//    public void validate_formal_date_chunk_detection() {
//        assertSentence("15/apr/2012").hasDateChunk("15 / apr / 2012");
//        assertSentence("on 15/apr/2012").hasDateChunk("on 15 / apr / 2012");
//        assertSentence("11/10/2012").hasDateChunk("11 / 10 / 2012");
//        assertSentence("on 11/10/2012").hasDateChunk("on 11 / 10 / 2012");
//        assertSentence("may 20th").hasDateChunk("may 20 th");
//        assertSentence("may 2016").hasDateChunk("may 2016");
//        assertSentence("13 jan").hasDateChunk("13 jan");
//    }
//
//    @Test
//    public void validate_chunk_detection() {
//        assertSentence("Grocery shopping at Wegmans Thursday at 5pm")
//                .hasLocationChunk("at Wegmans")
//                .hasTimeChunk("at 5 pm");
//
//        assertSentence("Lunch at noon for 30 minutes")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at noon for 30 minutes");
//
//        assertSentence("Lunch tomorrow at Starbucks at 8am")
//                .hasLocationChunk("at Starbucks")
//                .hasTimeChunk("at 8 am");
//
//        assertSentence("12/09 Meet John at Mall from 9:30 to 12:00")
//                .hasLocationChunk("at Mall ,")
//                .hasTimeChunk("from 9 : 30 to 12 : 00")
//                .hasDateChunk("12 / 09");
//
//        assertSentence("Meet John on monday at Mall")
//                .hasLocationChunk("at Mall")
//                .hasNoTimeChunk();
//        assertSentence("Family Dine Out on the 2nd Friday of every month at 6-9p")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 - 9 p");
//        assertSentence("Family Vacation at Singapore from 12/4 for six days")
//                .hasLocationChunk("at Singapore ,")
//                .hasNoTimeChunk();
//        assertSentence("Mission Trip at Jakarta on Nov 13-17 calendar Church")
//                .hasLocationChunk("at Jakarta")
//                .hasNoTimeChunk();
//        assertSentence("Piano lessons Tuesdays and Thursdays and Mondays at 6pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 pm");
//        assertSentence("Meeting with John tomorrow every day until 12.10.2012")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Tennis on Mondays, Tuesdays, Fridays at 10")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 10");
//        assertSentence("Meeting 7 am")
//                .hasNoLocationChunk()
//                .hasTimeChunk("7 am");
//        assertSentence("Meeting at 7 pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 7 pm");
//        assertSentence("Meeting at 23:45")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 23 : 45");
//        assertSentence("Meeting at four")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at four");
//        assertSentence("Meeting at 9 am pdt")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 9 am");
//        assertSentence("Meeting today with Robert")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Go GYM at nine till eleven")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at nine till eleven");
//        assertSentence("Go GYM at nine for 5 hours")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at nine for 5 hours");
//        assertSentence("Meeting at nine during 5 hours")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at nine during 5 hours");
//        assertSentence("Lunch at noon for 30 minutes")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at noon for 30 minutes");
//        assertSentence("Short conversation in one hour")
//                .hasNoLocationChunk()
//                .hasTimeChunk("in one hour");
//
//        assertSentence("Event in 5 days")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        //FIXME
////        assertSentence("meeting with tom 1400")
////                .hasNoLocationChunk()
////                .hasTimeChunk("1400");
//        assertSentence("meeting with tom 930pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("930 pm");
//        assertSentence("meeting with tom 1400-1600")
//                .hasNoLocationChunk()
//                .hasTimeChunk("1400 - 1600");
//
//        assertSentence("Movie 2/12")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//
//            //FIXME
////        assertSentence("Lunch at half past ten")
////                .hasNoLocationChunk()
////                .hasTimeChunk("at half past ten");
//
//        assertSentence("BDay 13 january")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Event 1st of January 2014")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Event 2nd of Aug 2014")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Go GYM 03/21/2013")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Go GYM 2.05.2013 19:00")
//                .hasNoLocationChunk()
//                .hasTimeChunk("19 : 00");
//
//        assertSentence("Meeting on Mon")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Meeting next Friday")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Vacation starts 10 Jan ends 17 Jan")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Vacation starts 10 January 2014 ends 17 January")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Lunch tomorrow at Starbucks at 8am")
//                .hasLocationChunk("at Starbucks")
//                .hasTimeChunk("at 8 am");
//        assertSentence("Grocery shopping at Wegman's Thursday at 5pm")
//                .hasLocationChunk("at Wegman's")
//                .hasTimeChunk("at 5 pm");
//        assertSentence("Lunch at 123 Main St. at 5 pm")
//                .hasLocationChunk("at 123 Main St.")
//                .hasTimeChunk("at 5 pm");
//        assertSentence("Paying bills day on 3rd Tuesday of each month")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Paying bills day repeat on the 3rd Tuesday of each month")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Paying bills every last friday of Month")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Paying bills day today Repeat every month")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Paying bills day today repeat every 2 days")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Paying bills day today repeat every 3 weeks")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Piano lessons on Fridays at 3 am")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 3 am");
//        assertSentence("Piano lessons Tuesdays and Thursdays and Mondays at 6pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 pm");
//        assertSentence("Piano lessons Tuesdays and Thursdays at 5-6pm from 1/21 to 2/23")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 5 - 6 pm");
//        assertSentence("Katine's BDay 13 january repeat yearly")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Yoga trainings at 12 every Sunday")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 12");
//        assertSentence("Soccer practice every Tue at 6")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6");
//        assertSentence("Sam's birthday every year on 5/16")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Lunch every Tuesday until 2/5")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Meeting with John tomorrow every day until 12.10.2012")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Sam's birthday june 19 yearly")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Lunch every day until next friday")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Lunch every day for 2 weeks")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Tennis on Mondays, Tuesdays, Fridays at 10")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 10");
//        assertSentence("Tennis every wed and fri at 6 pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 pm");
//        assertSentence("Piano lesson today at 5-7:30 pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 5 - 7 : 30 pm");
//        assertSentence("Meeting for 2 hours 4pm next Friday")
//                .hasNoLocationChunk()
//                .hasTimeChunk("for 2 hours 4 pm");
//        assertSentence("Lexus test drive from 5 PM to 7 PM on Friday")
//                .hasNoLocationChunk()
//                .hasTimeChunk("from 5 PM to 7 PM");
//        assertSentence("Work 19:00-19:30")
//                .hasNoLocationChunk()
//                .hasTimeChunk("19 : 00 - 19 : 30");
//        assertSentence("holiday from 11 october till 18 october")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//
//        assertSentence("holiday from 5/5 till 5/10")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Meet John at 18:00 15th september")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 18 : 00");
//        //FIXME
////        assertSentence("Lunch with Matthew at 123 Main St at 1:30 Monday")
////                .hasLocationChunk("at 123 Main St")
////                .hasTimeChunk("at 1 : 30");
//        assertSentence("Family vacation from August 9-18")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Staff meeting Tuesday 2pm ")
//                .hasNoLocationChunk()
//                .hasTimeChunk("2 pm");
//
//        assertSentence("Soccer practice every Tuesday at 6")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6");
//        assertSentence("Jim's birthday every year on 5/16")
//                .hasNoLocationChunk()
//                .hasNoTimeChunk();
//        assertSentence("Pizza party on the 2nd Friday of every month at 1pm")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 1 pm");
//        assertSentence("Meet with Abraham at Lenmarc Mall on Mon 10.30-noon")
//                .hasLocationChunk("at Lenmarc Mall")
//                .hasTimeChunk("10 . 30 - noon");
//
//        assertSentence("Mission Trip at Jakarta on Nov 13-17 calendar Church")
//                .hasLocationChunk("at Jakarta")
//                .hasNoTimeChunk();
//
//        //FIXME
////        assertSentence("Internal Team Meeting at R714 on Wednesdays from 11-2p /w")
////                .hasLocationChunk("at R714")
////                .hasTimeChunk("from 11 - 2p");
//        //FIXME
////        assertSentence("Board Meeting at R215 every other Thursday from 11 AM to 2 PM /w")
////                .hasLocationChunk("at R215")
////                .hasTimeChunk("from 11 am to 2 pm");
//
//        assertSentence("Family Dine Out on the 2nd Friday of every month at 6-9p /h")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 - 9 p");
//        assertSentence("Wash the Car every third Thursday at 8.45a /h")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 8 . 45 a");
//        assertSentence("meeting tomorrow from 12 to 13:30 ")
//                .hasTimeChunk("from 12 to 13 : 30")
//                .hasNoLocationChunk();
//        //FIXME
//        assertSentence("Pick up Noah at MSCS on Mondays, Wednesdays and Fridays at 10a from 11/13 to 11/25 /h")
//                .hasLocationChunk("at MSCS")
//                .hasTimeChunk("at 10 a");
//        //FIXME
////        assertSentence("Internal Team Meeting at R714 on Wednesdays from 11-2p ")
////                .hasLocationChunk("at R714")
////                .hasTimeChunk("from 11 - 2 p");
//
//        assertSentence("Family Dine Out on the 2nd Friday of every month at 6-9p ")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 6 - 9 p");
//
//        assertSentence("Wash the Car every third Thursday at 8.45a alert ")
//                .hasNoLocationChunk()
//                .hasTimeChunk("at 8 . 45 a");
//
//
//    }
//}
