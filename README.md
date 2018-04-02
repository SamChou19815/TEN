# TEN

TEN Board Game by Developer Sam.

[![Release](https://jitpack.io/v/SamChou19815/TEN.svg)](https://jitpack.io/#SamChou19815/TEN)

Add this to your `build.gradle` to use the artifact.

```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.SamChou19815:TEN:1.0.0'
}
```

## Additional Rules

The main rules are described [here](https://mathwithbaddrawings.com/2013/06/16/ultimate-tic-tac-toe)

To balance the game, I specified an additional rule that when there is same 
number of big squares for black and white, white wins. It can compensate for 
the first-move advantage for black. With this rule, the winning probability for
black and white is 53:47. Without the rule, the ratio is 7:3.