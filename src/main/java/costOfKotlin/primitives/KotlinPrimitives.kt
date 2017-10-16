package costOfKotlin.primitives

import org.openjdk.jmh.annotations.Benchmark


open class KotlinPrimitives {

    @Benchmark
    fun _1_baseline(state: IntState): Int {
        /* Needs a non-null check on the parameter compared to the Java version.
         */
        return state._41
    }

    @Benchmark
    fun _2_sum(state: IntState): Int {
        return state._41 + 1
    }

    @Benchmark
    fun _3_sum_nullable_bang_bang(state: IntState): Int {
        /* Needs a null-check and Integer.intValue() compared to #sum_with_state.
         */
        return state.nullable_41!! + 1
    }

    @Benchmark
    fun _4_sum_elvis_never_null(state: IntState): Int {
        return (state.nullable_41 ?: 0) + 1
    }

    @Benchmark
    fun _5_sum_elvis_always_null(state: IntState): Int {
        return (state.nullInt ?: 0) + 1
    }

    @Benchmark
    fun _6_sum_elvis_50_50_nullable(state: IntState): Int {
        /* Stop always picking the same branch by forcing a random path.
        */
        return (state.`50 50 NullableInt` ?: 0) + 1
    }

    @Benchmark
    fun _7_sum_elvis_90_10_nullable(state: IntState): Int {
        /* Stop always picking the same branch by forcing a random path.
        */
        return (state.`90 10 NullableInt` ?: 0) + 1
    }
}
