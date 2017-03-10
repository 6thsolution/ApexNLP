<p align="center">
<img  src="https://raw.githubusercontent.com/6thsolution/ApexNLP/master/logo.png" width="200" height="200" />
</p>
<p align="center">
    <a href="https://travis-ci.org/6thsolution/ApexNLP">
        <img src="https://travis-ci.org/6thsolution/ApexNLP.svg?branch=master"
             alt="Build Status">
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

**ApexNLP** is a deterministic rule-based system designed for recognizing and normalizing event expressions.  It will convert given sentences to a simple [`Event`](https://github.com/6thsolution/ApexNLP/blob/master/apex/src/main/java/com/sixthsolution/apex/model/Event.java) class.

- [Features](#features)
- [Installation](#installation)
- [Example](#example)
- [Documentation](#documentation)
- [License](#license)

## Features
*  Robust and Fast
*  Easy to Use
*  Support Multi Languages
*  Written in Java and support Android 
*  Support most date/time formats

## Installation

## Example
Below you can see a sample usage of **ApexNLP** in android:
```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    //TODO
  }
}

public class ExampleActivity extends Activity {
}
```

## Documentation
<table>
  <tr>
    <td><a href="">Usage Workflows</a></td>
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
Copyright 2017 6thSolution

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
