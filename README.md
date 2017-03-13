<p align="center">
<img  src="https://raw.githubusercontent.com/6thsolution/ApexNLP/master/logo.png" width="200" height="200" />
</p>
<p align="center">
    <a href="https://travis-ci.org/6thsolution/ApexNLP">
        <img src="https://travis-ci.org/6thsolution/ApexNLP.svg?branch=master"
             alt="Build Status">
    </a>
     <a href="https://bintray.com/6thsolution/apexnlp/apex/_latestVersion">
        <img src="https://api.bintray.com/packages/6thsolution/apexnlp/apex/images/download.svg"
             alt="Latest Version">
    </a>
    <a href="https://codecov.io/gh/6thsolution/ApexNLP">
        <img src="https://codecov.io/gh/6thsolution/ApexNLP/branch/master/graph/badge.svg"
             alt="codecov">
    </a>
    <a href="https://github.com/6thsolution/ApexNLP/issues">
        <img src="https://img.shields.io/github/issues/6thsolution/ApexNLP.svg"
             alt="GitHub Issues">
    </a>
     <a href="https://github.com/6thsolution/ApexNLP/stargazers">
        <img src="https://img.shields.io/github/stars/6thsolution/ApexNLP.svg"
             alt="GitHub Stars">
    </a>
</p>

A natural language processing tool that makes it easy to create events like `Piano lessons Tuesdays at 6pm`. 

**ApexNLP** is a deterministic rule-based system designed for recognizing and normalizing different formats of event expressions.  It will convert given sentences to a simple [`Event`](https://github.com/6thsolution/ApexNLP/blob/master/apex/src/main/java/com/sixthsolution/apex/model/Event.java) class. Out-of-the-box, it expects and processes English language text. But, it was designed to work with multiple human languages

- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
- [Example](#example)
- [Documentation](#documentation)
- [License](#license)

## Overview
There are many date/time parsers like [natty](https://natty.joestelmach.com) or [chrono](https://github.com/wanasit/chrono). But ApexNLP is not another date/time parser, It is an event parser with some NLP tasks which helps you to easily create a calendar Event. It support most event formats from simple to complex such as:
- Family Dine Out on the 2nd Friday of every month at 6-9p
- Meet John on monday at Mall
- Tennis on Mondays, Tuesdays, Fridays at 10
- Paying bills day repeat on the 3rd Tuesday of each month
- Piano lessons Tuesdays and Thursdays at 5-6pm from 1/21 to 2/23
- Meeting with John tomorrow every day until 12.10.2018"

Here are some sample outputs:

**input sentence:** `Lunch at noon for 30 minutes`

**event response:**

```javascript
{
  "title" : "Lunch",
  "location" : "",
  "startDateTime" : "2017/4/13 12:00",
  "endDateTime" : "2017/4/13 12:30",
  "isAllDay" : false,
  "recurrence" : null
}
```
**input sentence:**  `Family Vacation at Singapore from 12/4 for six days`:

**event response:**

```javascript
{
  "title" : "Family Vacation ",
  "location" : "Singapore",
  "startDateTime" : "2017/12/04",
  "endDateTime" : "2017/12/09",
  "isAllDay" : true,
  "recurrence" : null
}
```

## Features
*  Robust and Fast
*  Easy to Use
*  Support Multi Languages
*  Written in Java and support Android 
*  Support most date/time formats

## Installation
First add our repository:
```groovy
repositories {
    //...
    maven {
        url  "http://dl.bintray.com/6thsolution/apexnlp"
    }

}
```
Then add needed libraries:
```groovy
dependencies{
    //ApexNLP core library
    compile 'com.sixthsolution.apex:apex:0.1.0-alpha1'
    //NLP for English
    compile 'com.sixthsolution.apex:english-nlp:0.1.0-alpha1'
    //ThreeTen 
    compile 'org.threeten:threetenbp:1.3.3'
}
```
**Note:** If you are using android, replace _threeten_ dependency with [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)

## Example
Below you can see a sample usage of **ApexNLP** in android:
```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Apex.init(new Apex.ApexBuilder()
                 .addParser("en", new EnglishParser())
                    .build()); 
  }
  
}

public class ExampleActivity extends Activity {

  public Event getEvent(String sentence){
    return Apex.nlp("en", sentence);                    
  }
  
}
```

## Documentation
<table>
  <tr>
    <td><a href="https://github.com/6thsolution/ApexNLP/wiki/Usage-Workflows">Usage Workflows</a></td>
    <td><p>How to use ApexNLP and its features.</p></td>
  </tr>
  <tr>
    <td><a href="">API Reference</a></td>
    <td><p>Java doc for ApexNLP's API.</p></td>
  </tr>
  <tr>
    <td><a href="https://github.com/6thsolution/ApexNLP/wiki/Multiple-Languages-Support">Multiple Languages Support</a></td>
    <td><p>How to add and support a new language to the ApexNLP project. </p></td>
  </tr>
</table>

## License

```
Copyright 2017 6thSolution Technologies Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
