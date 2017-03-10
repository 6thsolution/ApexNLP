package com.sixthsolution.apex.nlp.english.test.tokenization;

import com.sixthsolution.apex.nlp.english.EnglishTokenizer;

import org.junit.Before;
import org.junit.Test;

import static com.sixthsolution.apex.nlp.test.TokenizerAssertion.assertTokens;
import static com.sixthsolution.apex.nlp.test.TokenizerAssertion.init;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EnglishTokenizationTest {

    @Before
    public void setUp() {
        init(new EnglishTokenizer());
    }

    @Test
    public void test_sentences() {
        assertTokens(
                "Pizza party on the 2nd Friday of every month at 1pm\n",
                "Pizza", "party", "on", "the", "2", "nd", "Friday", "of", "every", "month", "at",
                "1", "pm");

        assertTokens(
                "Mission Trip at Jakarta on Nov 13-17 calendar Church\n",
                "Mission", "Trip", "at", "Jakarta", "on", "Nov", "13", "-", "17", "calendar",
                "Church"
        );
        assertTokens("Go GYM 2.05.2013 19:00",
                "Go", "GYM", "2", ".", "05", ".", "2013", ",", "19", ":", "00");
    }
}
