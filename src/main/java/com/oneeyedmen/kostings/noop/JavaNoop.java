package com.oneeyedmen.kostings.noop;

import org.openjdk.jmh.annotations.Benchmark;

public class JavaNoop {

    @Benchmark public void noop() {
    }

    @Benchmark public int just_return() {
        return 42;
    }
}
