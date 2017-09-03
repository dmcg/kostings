
package com.oneeyedmen.kostings.primitives;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaPrimitives {

    @Benchmark
    public void sum_things_wrong_here() {
        int i1 = 41;
        int i2 = 1;
        int sum = i1 + i2;
        // This is as fast as noop
    }

    @Benchmark
    public int sum() {
        int i1 = 41;
        int i2 = 1;
        return i1 + i2;
        // and this is as fast as a straight return
    }

    @Benchmark
    public void sum_with_state(IntState state) {
        state.setResult(state.get_41() + 1);
    }

    @Benchmark
    public void sum_with_state_and_blackhole(IntState state, Blackhole blackhole) {
        blackhole.consume(state.get_41() + 1);
    }
}
