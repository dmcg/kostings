package com.oneeyedmen.kostings.primitives

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThan
import com.oneeyedmen.kostings.Results
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KFunction


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
    fun test() {
        val baselineResult = resultFor(this::_1_baseline)
        val sumResult = resultFor(this::_2_sum)
        assertThat(baselineResult!!.score, greaterThan(sumResult!!.score))
    }

}

private fun resultFor(method: KFunction<*>) = method.methodName?.let { Results.resultNamed(it) }

val KFunction<*>.methodName get() = (this as? FunctionReference)?.let {
    "${it.boundReceiver.javaClass.name}.${it.name}"
}

/*
Summary - neither null-checks nor unboxing are detectable
*/

