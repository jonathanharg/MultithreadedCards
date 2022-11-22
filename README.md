# Multithreaded Card Game

This is a multithreaded implementation of a card game in which players have to race to get a hand
consisting of all the same cards.

This code is written in Java 19, tested using JUnit 5 and complies with Google Java Style.

## Main Game

### Compiling

```shell
javac ./src/*.java
jar cfm cards.jar MANIFEST.MF -C src/ .
```

### Running

To run the code simply execute

```shell
java -jar cards.jar
Please enter the number of players:
...
```

then, when prompted, give a valid number of players and a `deckfile`.

> Note. a `deckfile` must be valid for a given number of players, and can either 
> be a relative or absolute path.

## Testing

Compile the tests with the [Junit Platform Console Standalone jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/).
Version 1.9.1 of the Junit Platform Console will be used in following examples.

> Note. for release builds Junit Platform Console is already provided and the tests
> have already been compiled.

```shell
javac -cp ./src/*.java ./tests/*.java
javac -cp junit-platform-console-standalone-1.9.1.jar ./src/*.java ./tests/*.java
```

To run the unit tests

**Warning!** Running unit tests will delete any files ending in `_output.txt` from
your current directory. This is to clean up artifacts from testing, but may
result in data loss if you're not careful!

```shell
java -jar junit-platform-console-standalone-1.9.1.jar --scan-class-path -cp tests -cp src
```
