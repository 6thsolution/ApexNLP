package com.sixthsolution.apex.nlp.test;

import com.sixthsolution.apex.nlp.tokenization.StandardTokenizer;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public final class TokenizerAssertion {

    private static TokenizerAssertion instance = null;
    private Tokenizer tokenizer;



    private TokenizerAssertion(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private static TokenizerAssertion getInstance() {
        if (instance == null) {
            instance = new TokenizerAssertion(new StandardTokenizer());
        }
        return instance;
    }

    public static void init(Tokenizer tokenizer) {
        getInstance().setTokenizer(tokenizer);
    }

    public static void assertTokens(String sentence, String... tokens) {
        System.out.println("Actual sentence: " + sentence);
        String[] tokenized = getInstance().tokenizer.tokenize(sentence);
        System.out.println("Tokenized sentence: " + toStringTokens(tokenized));
        assertArrayEquals(tokenized, tokens);
    }
    private void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }


    private static String toStringTokens(String[] e) {
        Iterator<String> itr = Arrays.asList(e).iterator();
        StringBuilder sb = new StringBuilder();
        while (itr.hasNext()) {
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }


}
