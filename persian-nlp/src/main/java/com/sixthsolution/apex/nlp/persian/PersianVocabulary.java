package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.nlp.dict.Dictionary;
import com.sixthsolution.apex.nlp.dict.DictionaryBuilder;
import com.sixthsolution.apex.nlp.event.SeekBy;

import static com.sixthsolution.apex.nlp.dict.Tag.*;
import static com.sixthsolution.apex.nlp.ner.Entity.*;
import static com.sixthsolution.apex.nlp.ner.Entity.LOCATION;
import static com.sixthsolution.apex.nlp.ner.Entity.NONE;

/**
 * Created by rozhin on 7/26/2017.
 */
public final class PersianVocabulary {
    public static Dictionary build() {
        DictionaryBuilder vb = new DictionaryBuilder();
        //***************************************
        //WEEK DAYS
        //***************************************
        vb.tag(WEEK_DAY, DATE)
                .e(1, "شنبه")
                .e(2, "یک_شنبه","یکشنبه")
                .e(3, "دوشنبه")
                .e(4, "سه_شنبه","سهشنبه")
                .e(5, "چارشنبه","چهارشنبه")
                .e(6, "پنج_شنبه","پنجشنبه")
                .e(7, "جمعه");
        //***************************************
        //RELATIVE DATE
        //***************************************
        vb.tag(NAMED_DATE, DATE)
                .e(0, "امروز")
                .e(1, "فردا")
                .e(1, "پسفردا","پس_فردا");
        vb.tag(GLOBAL_PREPOSITION,DATE)
                .e(true, "یعد")
                .e(false, "قبل");
        //***************************************
        //MONTH_NAME NAMES
        //***************************************
        vb.tag(MONTH_NAME, DATE)
                .e(1, "فروردین")
                .e(2, "اردیبهشت")
                .e(3, "خرداد")
                .e(4, "تیر")
                .e(5, "مرداد")
                .e(6, "شهریور")
                .e(7, "مهر")
                .e(8, "آبان", "ابان")
                .e(9, "آذر", "اذر")
                .e(10, "دی")
                .e(11, "بهمن")
                .e(12, "اسفند");
        //***************************************
        //TIME
        //***************************************
        vb.tag(TIME_PREFIX, TIME)
                .e("در", "ساعت");
        vb.tag(TIME_RANGE, TIME)
                .e("-");
        vb.tag(TIME_RELATIVE_PREFIX, TIME)
                .e("تا");
        vb.tag(TIME_RELATIVE, TIME)
                .e(9, "صبح")
                .e(12, "ظهر")
                .e(16, "عصر", "بعد_از_ظهر")
                .e(21, "شب","امشب")
                .e(23, "نیمه_شب", "نصف_شب")
                .e(0, "الان");//TODO current time
        vb.tag(TIME_RELATIVE_INDICATOR, TIME)
                .e(false, "قبل","قبل_از")
                .e(true, "بعد","بعد_از");
        vb.tag(TIME_START_RANGE, TIME)
                .e("از");
        vb.tag(TIME_RANGE, TIME)
                .e("تا", "-");
        vb.tag(TIME_SEPARATOR, TIME)
                .e(".", ":");
        vb.tag(TIME_MERIDIEM, TIME)
                .e(0, "ق.ظ")
                .e(12, "ب.ظ");
        vb.tag(TIME_HOUR, TIME)
                .e(SeekBy.HOUR, "ساعت", "ساعت_ها");
        vb.tag(TIME_MIN, TIME)
                .e(SeekBy.MIN, "دقیقه", "دقیقه","دقیقه_ها");
        vb.tag(TIME_SEC, TIME)
                .e("ثانیه", "ثانیه_ها");

        //***************************************
        //DATE
        //***************************************
        vb.tag(DATE_RECURRENCE,DATE)
                .e("هر");
        vb.tag(DATE_RANGE,DATE)
                .e("تا");
//        vb.tag(DATE_FOREVER_KEY,DATE)
//                .e(2,"other");
        vb.tag(DATE_SEPARATOR, DATE)
                .e("/", "-", ".");
        vb.tag(DATE_PREPOSITION, DATE)
                .e("در");
        vb.tag(DATE_SEEKBY, DATE)
                .e(1,"روز", "روز_ها", "روزها")
                .e(7,"هفته", "هفته_ها")
                .e(30,"ماه", "ماه_ها")
                .e(365,"سال", "سال_ها");
        vb.tag(YEAR_SEEK,DATE)
                .e("سال", "سال_ها");
        vb.tag(MONTH_SEEK,DATE)
                .e("ماه", "ماه_ها");
        vb.tag(WEEK_SEEK,DATE)
                .e("هفته", "هفته_ها");
        vb.tag(DAY_SEEK,DATE)
                .e("روز", "روز_ها", "روزها");


        vb.tag(DATE_START_RANGE, DATE)
                .e("از", "از_روز");
        vb.tag(DATE_SUFFIX, DATE)
                .e("ام", "امین");
        vb.tag(DATE_DURATION_SUFFIX, DATE)
                .e("روز",  "روز_ها","روزها");
        vb.tag(DATE_PREFIX, DATE)
                .e("از" , "در" , ",");
        vb.tag(DATE_BAND,DATE)
                .e("اول","پایان","آخر");

        //***************************************
        //SEASONS
        //***************************************
        vb.tag(SEASON, DATE)
                .e(1, "بهار")
                .e(2, "تابستان")
                .e(3, "پاییز")
                .e(4, "زمستان");
        //***************************************
        //RECURRENCES
        //***************************************
//        vb.tag(REC_WEEK_DAYS, DATE)
//                .e(1, "mondays", "mons", "mons.")
//                .e(2, "tuesdays", "tues", "tues.")
//                .e(3, "wednesdays", "weds", "weds.")
//                .e(4, "thursdays", "thurs", "thurs.", "thus", "thus.")
//                .e(5, "fridays", "fris", "fris.")
//                .e(6, "saturdays", "sats", "sats.")
//                .e(7, "sundays", "suns", "suns.");
        //***************************************
        //NUMBERS
        //***************************************
        vb.tag(NUMBER, NONE)
                .e(0, "صفر")
                .e(1, "یک", "اول", "اولین","یکم")
                .e(2, "دو", "دوم","دومین")
                .e(3, "سه", "سومین","سوم")
                .e(4, "چهار", "چهارم","چهارمین")
                .e(5, "پنج", "پنجم","پنجمین")
                .e(6, "شش", "ششم","ششمین")
                .e(7, "هفت", "هفتمین","هفتم")
                .e(8, "هشت", "هشتمین","هشتم")
                .e(9, "نه", "نهمین","نهم")
                .e(10, "ده", "دهمین","دهم")
                .e(11, "یازده", "یازدهم","یازدهمین")
                .e(12, "دوازده", "دوازدهمین","دوازدهم")
                .e(13, "سیزده", "سیزدهم","سیزدهمین")
                .e(14, "چهارده", "چهاردهمین","چهاردهم")
                .e(15, "پانزده", "پانزدهمین","پانزدهم")
                .e(16, "شانزده", "شانزدهم","شانزدهمین")
                .e(17, "هفده", "هفدهم","هفدهمین")
                .e(18, "هجده", "هجدهم", "هجدهمین")
                .e(19, "نوزده", "نوزدهمین","نوزدهم")
                .e(20, "بیست", "بیستمین","بیستم")
                .e(30, "سی", "سی_ام","سی_امین");
        //***************************************
        //Location
        //***************************************
        vb.tag(LOCATION_PREFIX, LOCATION)
                .e("در");
        vb.tag(LOCATION_SUFFIX, LOCATION)
                .e("خیابان", "کوچه", "بزرگراه");
        //***************************************
        //Others
        //***************************************
        vb.tag(PREPOSITION, NONE)
                .e("در", "از", "تا");
        vb.tag(RELATIVE_PREPOSITION , NONE)
                .e(1,"بعدی","بعد");
        vb.tag(RELATIVE_SUFFIX,NONE)
                .e("از_امروز","از_الان","از_فردا");
//        vb.tag(THE_PREFIX,NONE)
//                .e("the","this","current");
        return vb.build();
    }
}
