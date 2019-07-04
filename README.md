# parsec-java
Java JNA wrapper for Parsec SDK

## Build

    gradlew jar

## Test

You will need a sessionId which you can get by running session.py in the SDK.

    gradlew run --args "XXXXXXXXXXX 0"

where XXXXXXXXX is your sessionId