package com.sixthsolution.apex.sample.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nobigsoftware.dfalex.DfaBuilder;
import com.nobigsoftware.dfalex.DfaState;
import com.nobigsoftware.dfalex.Pattern;
import com.nobigsoftware.dfalex.StringMatcher;
import com.sixthsolution.apex.nlp.english.EnglishParser;
import com.sixthsolution.apex.nlp.english.EnglishVocabulary;
import com.sixthsolution.apex.nlp.ner.ChunkedPart;
import com.sixthsolution.apex.nlp.ner.regexold.LocationChunkMatcher;
import com.sixthsolution.apex.nlp.ner.regexold.RegExChunker;
import com.sixthsolution.apex.nlp.ner.regexold.StandardRegExChunker;
import com.sixthsolution.apex.nlp.ner.regexold.TimeChunkMatcher;
import com.sixthsolution.apex.nlp.tagger.StandardTagger;
import com.sixthsolution.apex.nlp.tagger.TaggedWords;
import com.sixthsolution.apex.nlp.tagger.Tagger;
import com.sixthsolution.apex.nlp.tokenization.StandardTokenizer;
import com.sixthsolution.apex.nlp.tokenization.Tokenizer;

import java.util.List;

import static com.nobigsoftware.dfalex.Pattern.DIGITS;
import static com.nobigsoftware.dfalex.Pattern.match;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EnglishParser parser = new EnglishParser();
        parser.initialize();
        long t = System.currentTimeMillis();
        parser.parse("Wash the Car at Mall on march 19 at 8.45pm 12/12/12");
        parser.parse("Piano lessons Tuesdays and Thursdays at 5-6pm from 1/21 to 2/23\n");
        System.out.println("TImesssss " + (System.currentTimeMillis() - t));

        test("Grocery shopping at Wegmans Thursday at 5pm");
        test("Wash the Car at Mall on march 19 at 8.45pm 12/12/12");
        test("Piano lessons Tuesdays and Thursdays at 5-6pm from 1/21 to 2/23");
        magic("Family Vacation at Singapore from 12/4 for six days /h\n");
        magic("Pick up Noah at MSCS on Mondays, Wednesdays and Fridays at 10a from 11/13 to 11/25 /h\n");
        magic("Internal Team Meeting at R714 on Wednesdays from 11-2p /w\n");
        dfaTest();
    }

    private void dfaTest() {
        long start = System.currentTimeMillis();
        //The <Integer> here means you want integer results
        DfaBuilder<String> builder = new DfaBuilder<>();

        //Lets say you have a list of keywords:
        Pattern pat = Pattern.DIGITS.then(match(":")).then(DIGITS);
        builder.addPattern(pat, "TIME");  //when this pattern matches, we get i out
        DfaState<String> startState = builder.build(null);

        long startMatch = System.currentTimeMillis();
        StringMatcher stringMatcher = new StringMatcher("meet saeed at 12s:12");
        System.out.println(stringMatcher.findNext(startState));
        System.out.println(System.currentTimeMillis() - startMatch);
    }


    private void magic(String sentence) {
        Tokenizer tokenizer = new StandardTokenizer();
        Tagger tagger = new StandardTagger(EnglishVocabulary.build());
        RegExChunker regExChunker = new StandardRegExChunker();
        long t = System.currentTimeMillis();
        TaggedWords tagged =
                tagger.tag(tokenizer.tokenize(sentence));
        List<ChunkedPart> res = regExChunker.chunk(tagged);
        System.out.println(res.toString());
        System.out.println((System.currentTimeMillis() - t));
    }

    private void test(String s) {
        Tokenizer tokenizer = new StandardTokenizer();
        Tagger tagger = new StandardTagger(EnglishVocabulary.build());
        long t = System.currentTimeMillis();

        LocationChunkMatcher location = new LocationChunkMatcher();
        TimeChunkMatcher time = new TimeChunkMatcher();

        TaggedWords tagged = tagger.tag(tokenizer.tokenize(s));
        time.doAction(tagged);
        location.doAction(tagged);

        System.out.println("Chunking takes: " + (System.currentTimeMillis() - t));

    }
}
