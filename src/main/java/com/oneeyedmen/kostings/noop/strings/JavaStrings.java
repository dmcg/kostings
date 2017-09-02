
package com.oneeyedmen.kostings.noop.strings;

import org.openjdk.jmh.annotations.Benchmark;

public class JavaStrings {

    @Benchmark
    public void concat() {
        String s1 = "hello";
        String s2 = "world";
        String s3 = s1 + " " + s2;
    }
}

/*
Notes - you can see Hotspot kicking in in the warmup iterations

Look at the numbers! 160 M operations per second
 */