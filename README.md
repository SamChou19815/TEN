# TEN

[![Build Status](https://travis-ci.com/SamChou19815/ten.svg?branch=master)](https://travis-ci.com/SamChou19815/ten)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.developersam/game-ten/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.developersam/game-ten)
![GitHub](https://img.shields.io/github/license/SamChou19815/ten.svg)

<img src="https://developersam.com/assets/app-icons/ten.png" alt="TEN" width=300 />

TEN Board Game by Developer Sam.

Read the docs [here](https://docs.developersam.com/game-ten/)

Add this to your `build.gradle` to use the artifact.

```groovy
dependencies {
    compile 'com.developersam:game-ten:+'
}
```

## Additional Rules

The main rules are described 
[here](https://mathwithbaddrawings.com/2013/06/16/ultimate-tic-tac-toe).

To balance the game, I specified an additional rule that when there is same number of big squares 
for black and white, white wins. It can compensate for the first-move advantage for black. With this
rule, the winning probability for black and white is 53:47. Without the rule, the ratio is 7:3.
