package com.oneeyedmen.kostings.baselines;

import com.oneeyedmen.kostings.strings.StringState;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaBaseline {

    @Benchmark
    public void baseline(StringState state, Blackhole blackhole) {
        blackhole.consume(state);
    }


    @Test
    public void dummy() {}
}
