package com.oneeyedmen.kostings.primitives

import com.oneeyedmen.kostings.Result
import com.oneeyedmen.kostings.check
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


open class KotlinPrimitives {

    @Benchmark
    fun _1_baseline(state: IntState, blackhole: Blackhole) {
        /* Needs a non-null check on the parameter compared to the Java version. This is not detectable.
         */
        blackhole.consume(state._41)
    }

    @Benchmark
    fun _2_sum(state: IntState, blackhole: Blackhole) {
        blackhole.consume(state._41 + 1)
    }

    @Benchmark
    fun _3_sum_nullable(state: IntState, blackhole: Blackhole) {
        /* Needs a null-check and Integer.intValue() compared to #sum_with_state. These are not detectable.
         */
        blackhole.consume(state.nullable_41!! + 1)
    }

    @Benchmark
    fun _4_sum_always_null(state: IntState, blackhole: Blackhole) {
        /* The other side of the coin. Still as fast.
        */
        blackhole.consume(state.nullInt ?: 0 + 1)
    }

    @Benchmark
    fun _5_sum_50_50_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path. Mixed results.
        */
        blackhole.consume(state.`50 50 NullableInt` ?: 0 + 1)
    }

    @Benchmark
    fun _6_sum_90_10_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path. Finally slower.
        */
        blackhole.consume(state.`90 10 NullableInt` ?: 0 + 1)
    }

    @Test
    fun `nothing is definitely faster than baseline`() {
        check(this::_1_baseline, Result::couldBeLessThan, this::_2_sum)
        check(this::_1_baseline, Result::couldBeLessThan, this::_3_sum_nullable)
        check(this::_1_baseline, Result::couldBeLessThan, this::_4_sum_always_null)
        check(this::_1_baseline, Result::couldBeLessThan, this::_5_sum_50_50_nullable)
        check(this::_1_baseline, Result::couldBeLessThan, this::_6_sum_90_10_nullable)
    }

    @Test
    fun `nullable is not detectably slower than non-nullable`() {
        check(this::_2_sum, Result::couldBeGreaterThan, this::_3_sum_nullable)
        check(this::_2_sum, Result::couldBeGreaterThan, this::_4_sum_always_null)
        check(this::_2_sum, Result::couldBeGreaterThan, this::_5_sum_50_50_nullable)
        check(this::_2_sum, Result::couldBeGreaterThan, this::_6_sum_90_10_nullable)
    }
}




/*
Summary - neither null-checks nor unboxing are detectable
*/

