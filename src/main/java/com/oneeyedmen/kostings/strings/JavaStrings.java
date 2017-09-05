
package com.oneeyedmen.kostings.strings;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaStrings {

    @Benchmark
    public void baseline(StringState state, Blackhole blackhole) {
        blackhole.consume(state.getHello());
        blackhole.consume(state.getWorld());
    }

    @Benchmark
    public void concat(StringState state, Blackhole blackhole) {
        blackhole.consume(state.getHello() + " " + state.getWorld());
    }

    @Test
    public void dummy() {}

}