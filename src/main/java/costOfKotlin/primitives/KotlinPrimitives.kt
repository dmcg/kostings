package costOfKotlin.primitives

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
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

    @Test
    fun `java sum is marginally detectable`() {
        assertThat(JavaPrimitives::_1_baseline, probablyDifferentTo(JavaPrimitives::_2_sum))
        assertThat(JavaPrimitives::_1_baseline, ! probablyFasterThan(JavaPrimitives::_2_sum))
    }

    @Test
    fun `kotlin sum is not detectable`() {
        assertThat(this::_1_baseline, ! probablyDifferentTo(this::_2_sum))
    }

    @Test
    fun `sum nullable is slower`() {
        assertThat(this::_2_sum, probablyFasterThan(this::_3_sum_nullable_bang_bang))
        assertThat(this::_2_sum, ! probablyFasterThan(this::_3_sum_nullable_bang_bang, byAFactorOf = 0.001))
    }

    @Test
    fun `sum always null is slower`() {
        assertThat(this::_2_sum, probablyFasterThan(this::_5_sum_elvis_always_null))
        assertThat(this::_2_sum, ! probablyFasterThan(this::_5_sum_elvis_always_null, byAFactorOf = 0.001))
    }

    @Test
    fun `branch_prediction is undetectable_50_50`() {
        assertThat(this::_5_sum_elvis_always_null, ! probablyDifferentTo(this::_6_sum_elvis_50_50_nullable))
    }

    @Test
    fun `branch_prediction is undetectable_90_10`() {
        assertThat(this::_5_sum_elvis_always_null, ! probablyDifferentTo(this::_7_sum_elvis_90_10_nullable))
    }

}
