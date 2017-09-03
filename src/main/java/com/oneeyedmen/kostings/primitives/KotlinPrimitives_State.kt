package com.oneeyedmen.kostings.primitives

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


open class KotlinPrimitives_State {

    /*
     * Baseline
     */
    @Benchmark
    fun sum_with_state(state: IntState, blackhole: Blackhole) {
        /* Needs a non-null check on the parameter compared to the Java version. This is not detectable.
         */
        blackhole.consume(state._41 + 1)
    }

    @Benchmark
    fun sum_nullable(state: IntState, blackhole: Blackhole) {
        /* Needs a null-check and Integer.intValue() compared to #sum_with_state. These are not detectable.
         */
        blackhole.consume(state.nullable_41!! + 1)
    }

    @Benchmark
    fun sum_always_null(state: IntState, blackhole: Blackhole) {
        /* The other side of the coin. Still as fast.
        */
        blackhole.consume(state.nullInt ?: 0 + 1)
    }


    @Benchmark
    fun sum_random_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path. Doesn't slow things down.
        */
        blackhole.consume(state.randomNullableInt ?: 0 + 1)
    }

}

/*
Summary - neither null-checks nor unboxing are detectable
*/

