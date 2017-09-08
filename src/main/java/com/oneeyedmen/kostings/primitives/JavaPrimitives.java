
package com.oneeyedmen.kostings.primitives;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaPrimitives {

    @Benchmark
    public void _1_baseline(IntState state, Blackhole blackhole) {
        blackhole.consume(state.get_41());
    }

    @Benchmark
    public void _2_sum(IntState state, Blackhole blackhole) {
        blackhole.consume(state.get_41() + 1);
    }

    @Test
    public void dummy() {}

}
