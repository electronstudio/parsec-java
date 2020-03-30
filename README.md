# parsec-java
Java JNA wrappers for Parsec SDK

## Build

    gradlew jar

## Test

You will need a sessionId which you can get by running session.py in the SDK.

    gradlew run --args "XXXXXXXXXXX 0"

where XXXXXXXXX is your sessionId

## Use

*com.parsecgaming.ParsecLibrary* is an auto-generated JNA wrapper from the Parsec C header.

See [com.parsecgaming.examples.Host](https://github.com/electronstudio/parsec-java/blob/master/src/com/parsecgaming/examples/Host.java) for how to use it.

If you aren't a C programmer you probably wont enjoy dealing with JNA, so there
is another wrapper on top: *uk.co.electronstudio.parsec.Parsec*

See [com.parsecgaming.examples.HostWrapper](https://github.com/electronstudio/parsec-java/blob/master/src/com/parsecgaming/examples/HostWrapper.java) for how to use it.
