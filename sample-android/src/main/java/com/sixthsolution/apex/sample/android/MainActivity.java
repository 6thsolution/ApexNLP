package com.sixthsolution.apex.sample.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sixthsolution.apex.Apex;
import com.sixthsolution.apex.model.Event;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long t = System.currentTimeMillis();
        Event event = Apex.nlp("en", "Wash the Car at Mall on march 19 at 8.45pm 12/12/12");
        System.out.println(event.start().toString());
        System.out.println(event.end().toString());
        System.out.println("Takes " + (System.currentTimeMillis() - t) + " millis");
    }

}
