package com.sixthsolution.apex.nlp.persian.test;

import com.sixthsolution.apex.nlp.persian.PersianTokenizer;
import com.sixthsolution.apex.nlp.test.TokenizerAssertion;
import org.junit.Before;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.TokenizerAssertion.assertTokens;

/**
 * @author Rozhin Bayati
 */

public class PersianTokenizationTest {

    @Before
    public void before_test(){
        PersianTokenizer persianTokenizer=new PersianTokenizer();
        TokenizerAssertion.init(persianTokenizer);
    }

    @Test
    public void test_sentences() {
        assertTokens(
                "22 بهمن روز پیروزی انقلاب اسلامی است",
                "22","بهمن","روز","پیروزی","انقلاب","اسلامی","است");

        assertTokens(
                "فروردین 1-13 مسافرت به شمال",
                "فروردین","1","-","13","مسافرت","به","شمال"
        );
        assertTokens(
                "روز های یکشنبه و سه شنبه قرار ملاقات با رئیس از ساعت 2 تا 5 بعد از ظهر ",
                "روز_های","یکشنبه","و","سه_شنبه","قرار","ملاقات","با","رئیس","از","ساعت","2","تا","5","بعد_از_ظهر"
        );
        assertTokens(
                "6امین روز هفته ساعت 4 چندشنبه با سینا",
                "6","امین","روز","هفته","ساعت","4","چندشنبه","با","سینا"
        );
        assertTokens(
                "5 روز در هفته در ساعت 9 تو میانجاده تاکسی بگیرم",
                "5","روز","در","هفته","در","ساعت","9","تو","میانجاده","تاکسی","بگیرم"
        );
        assertTokens(
                "سال 1399 هجری شمسی",
                "سال","1399","هجری","شمسی"
        );
        assertTokens(
                "روز دویست و دوم سال",
                "روز","دویست","و","دوم","سال"
        );
        assertTokens(
                "ساعت بیست و دو و بیست دقیقه",
                "ساعت","بیست","و","دو","و","بیست","دقیقه"
        );
        assertTokens(
                "هر 5 روز تا عید دوره ی درسی",
                "هر","5","روز","تا","عید","دوره","ی","درسی"
        );
        assertTokens(
                "ماه پنجم سال تا سال بعد",
                "ماه","پنجم","سال","تا","سال","بعد"
        );
        assertTokens(
                "4 روز بعد از عید",
                "4","روز","بعد","از","عید"
        );
        assertTokens(
                "شنبه قبل رمضان",
                "شنبه","قبل","رمضان"
        );
        assertTokens(
                "11 ماه بعد از اولین روز سال",
                "11","ماه","بعد","از","اولین","روز","سال"
        );
        assertTokens(
                "دهم هر ماه خوشالم",
                "دهم","هر","ماه","خوشالم"
        );
        assertTokens(
                "2 روز مانده به عید",
                "2","روز","مانده","به","عید"
        );
    }
}
