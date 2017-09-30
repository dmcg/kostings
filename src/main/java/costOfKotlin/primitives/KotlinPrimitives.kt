package costOfKotlin.primitives

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


open class KotlinPrimitives {

    @Benchmark
    fun _1_baseline(state: IntState, blackhole: Blackhole) {
        /* Needs a non-null check on the parameter compared to the Java version.
         */
        blackhole.consume(state._41)
    }

    @Benchmark
    fun _2_sum(state: IntState, blackhole: Blackhole) {
        blackhole.consume(state._41 + 1)
    }

    @Benchmark
    fun _3_sum_nullable(state: IntState, blackhole: Blackhole) {
        /* Needs a null-check and Integer.intValue() compared to #sum_with_state.
         */
        blackhole.consume(state.nullable_41!! + 1)
    }

    @Benchmark
    fun _4_sum_always_null(state: IntState, blackhole: Blackhole) {
        /* As _3 but when always null.
        */
        blackhole.consume(state.nullInt ?: 0 + 1)
    }

    @Benchmark
    fun _5_sum_50_50_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path.
        */
        blackhole.consume(state.`50 50 NullableInt` ?: 0 + 1)
    }

    @Benchmark
    fun _6_sum_90_10_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path.
        */
        blackhole.consume(state.`90 10 NullableInt` ?: 0 + 1)
    }

    @Test
    fun `parameter null check is undetectable`() {
        assertThat(JavaPrimitives::_1_baseline, probablyFasterThan(this::_1_baseline))
        assertThat(JavaPrimitives::_1_baseline, ! probablyFasterThan(this::_1_baseline))
    }

    @Test
    fun `java sum is undetectable`() {
        assertThat(JavaPrimitives::_1_baseline, ! probablyFasterThan(JavaPrimitives::_2_sum))
    }

    @Test
    fun `kotlin sum is undetectable`() {
        assertThat(this::_1_baseline, probablyFasterThan(this::_2_sum))
    }

    @Test
    fun `nullable sum is detectable`() {
        assertThat(this::_2_sum, probablyFasterThan(this::_3_sum_nullable))
        assertThat(this::_2_sum, probablyFasterThan(this::_4_sum_always_null))
    }

    @Test
    fun `branch_prediction is undetectable_50_50`() {
        assertThat(this::_4_sum_always_null, ! probablyDifferentTo(this::_5_sum_50_50_nullable))
    }

    @Test
    fun `branch_prediction is undetectable_90_10`() {
        assertThat(this::_4_sum_always_null, ! probablyDifferentTo(this::_6_sum_90_10_nullable))
    }

}
