package com.sixthsolution.apex.nlp.persian;

import com.sixthsolution.apex.nlp.dict.Dictionary;
import com.sixthsolution.apex.nlp.dict.DictionaryBuilder;
import com.sixthsolution.apex.nlp.parser.StandardParserBase;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import static com.sixthsolution.apex.nlp.dict.Tag.DATE_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.MONTH_NAME;
import static com.sixthsolution.apex.nlp.dict.Tag.NUMBER;
import static com.sixthsolution.apex.nlp.dict.Tag.SEASON;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_HOUR;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MERIDIEM;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_MIN;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_RELATIVE;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_SEC;
import static com.sixthsolution.apex.nlp.dict.Tag.TIME_SEPARATOR;
import static com.sixthsolution.apex.nlp.dict.Tag.WEEK_DAY;
import static com.sixthsolution.apex.nlp.ner.Entity.DATE;
import static com.sixthsolution.apex.nlp.ner.Entity.NONE;
import static com.sixthsolution.apex.nlp.ner.Entity.TIME;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class PersianParser extends StandardParserBase {

    protected Dictionary provideDictionary() {
        DictionaryBuilder vb = new DictionaryBuilder();
        //***************************************
        //WEEK DAYS
        //***************************************
        vb.tag(WEEK_DAY, DATE)
                .e(1, "شنبه")
                .e(2, "یکشنبه", "یک_شنبه")
                .e(3, "دوشنبه", "دو_شنبه")
                .e(4, "سهشنبه", "سه_شنبه")
                .e(5, "چهارشنبه", "چهار_شنبه", "چارشنبه", "چار_شنبه")
                .e(6, "پنجشنبه", "پنج_شنبه")
                .e(7, "جمعه");
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
        //TIME_RELATIVE
        //***************************************
        vb.tag(TIME_RELATIVE, TIME)
                .e(4, "سحر", "بامداد")
                .e(9, "صبح")
                .e(12, "ظهر")
                .e(16, "عصر")
                .e(21, "شب")
                .e(23, "نیمه_شب", "نصف_شب");
        vb.tag(TIME_SEPARATOR, TIME)
                .e(".", ":");
        vb.tag(TIME_MERIDIEM, TIME)
                .e(0, "قبل_ظهر", "قبل_از_ظهر", "ق.ظ")
                .e(12, "بعد_از_ظهر", "بعد_ظهر", "ب.ظ");
        vb.tag(TIME_HOUR, TIME)
                .e("ساعت");
        vb.tag(TIME_MIN, TIME)
                .e("دقیقه", "دیقه");
        vb.tag(TIME_SEC, TIME)
                .e("ثانیه");
        //***************************************
        //DATE
        //***************************************
        vb.tag(DATE_SEPARATOR, DATE)
                .e("/");
        //***************************************
        //SEASONS
        //***************************************
        vb.tag(SEASON, DATE)
                .e(1, "بهار")
                .e(2, "تابستان")
                .e(3, "پاییز")
                .e(4, "زمستان");
        //***************************************
        //NUMBERS
        //***************************************
        vb.tag(NUMBER, NONE)
                .e(0, "صفر")
                .e(1, "یک", "اول", "اولین")
                .e(2, "دو", "دوم", "دومین")
                .e(3, "سه", "سوم", "سومین")
                .e(4, "چهار", "چار", "چهارم", "چهارمین", "چهار_ام", "چهار_امین")
                .e(5, "پنج", "پنجم", "پنجمین", "پنج_ام", "پنج_امین")
                .e(6, "شیش", "شش", "ششم", "ششمین", "شش_ام", "شش_امین")
                .e(7, "هفت", "هفتم", "هفتمین", "هفت_ام", "هفت_امین")
                .e(8, "هشت", "هشتم", "هشتمین", "هشت_ام", "هشت_امین")
                .e(9, "نه", "نهم", "نهمین", "نه_ام", "نه_امین")
                .e(10, "ده", "دهم", "ده_ام", "دهمین", "ده_امین")
                .e(11, "یازده", "یازدهم", "یازده_ام", "یازدهمین", "یازده_امین")
                .e(12, "دوازده", "دوازدهم", "دوازده_ام", "دوازدهمین", "دوازده_امین")
                .e(13, "سیزده", "سیزدهم", "سیزده_ام", "سیزدهمین", "سیزده_امین")
                .e(14, "چهارده", "چهاردهم", "چهارده_ام", "چهاردهمین", "چهارده_امین", "چارده_امین",
                        "چارده", "چاردهم", "چارده_ام", "چاردهمین")
                .e(15, "پانزده", "پانزدهم", "پانزده_ام", "پانزدهمین", "پانزده_امین")
                .e(16, "شانزده", "شانزدهم", "شانزده_ام", "شانزدهمین", "شانزده_امین")
                .e(17, "هفده", "هفدهم", "هفده_ام", "هفدهمین", "هفده_امین")
                .e(18, "هجده", "هجدهم", "هجده_ام", "هجدهمین", "هجده_امین")
                .e(19, "نوزده", "نوزدهم", "نوزده_ام", "نوزدهمین", "نوزده_امین")
                .e(20, "بیست", "بیستم", "بیست_ام", "بیستمین", "بیست_امین")
                .e(30, "سی", "سیم", "سی_ام", "سیمین", "سی_امین");
        return vb.build();
    }

    @Override
    protected Tagger provideTagger() {
        return new PersianTagger(provideDictionary());
    }

    @Override
    protected Tokenizer provideTokenizer() {
        return new PersianTokenizer();
    }
}
