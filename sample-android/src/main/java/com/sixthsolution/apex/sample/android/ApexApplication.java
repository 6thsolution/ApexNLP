package com.sixthsolution.apex.sample.android;

import android.app.Application;

import com.sixthsolution.apex.Apex;
import com.sixthsolution.apex.nlp.english.EnglishParser;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ApexApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Apex.init(new Apex.ApexBuilder()
                .addParser("en", new EnglishParser())
                .build());
    }
}
