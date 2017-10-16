package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.primitives.JavaPrimitives
import costOfKotlin.primitives.KotlinPrimitives
import org.junit.Test

class PrimitivesTests {

    @Test
    fun `java sum is marginally detectable`() {
        assertThat(JavaPrimitives::_1_baseline, probablyDifferentTo(JavaPrimitives::_2_sum))
        assertThat(JavaPrimitives::_1_baseline, !probablyFasterThan(JavaPrimitives::_2_sum))
    }

    @Test
    fun `kotlin sum is not detectable`() {
        assertThat(KotlinPrimitives::_1_baseline, !probablyDifferentTo(KotlinPrimitives::_2_sum))
    }

    @Test
    fun `sum nullable is slower`() {
        assertThat(KotlinPrimitives::_2_sum, probablyFasterThan(KotlinPrimitives::_3_sum_nullable_bang_bang))
        assertThat(KotlinPrimitives::_2_sum, !probablyFasterThan(KotlinPrimitives::_3_sum_nullable_bang_bang, byAFactorOf = 0.001))
    }

    @Test
    fun `sum always null is slower`() {
        assertThat(KotlinPrimitives::_2_sum, probablyFasterThan(KotlinPrimitives::_5_sum_elvis_always_null))
        assertThat(KotlinPrimitives::_2_sum, !probablyFasterThan(KotlinPrimitives::_5_sum_elvis_always_null, byAFactorOf = 0.001))
    }

    @Test
    fun `branch_prediction is undetectable_50_50`() {
        assertThat(KotlinPrimitives::_5_sum_elvis_always_null, !probablyDifferentTo(KotlinPrimitives::_6_sum_elvis_50_50_nullable))
    }

    @Test
    fun `branch_prediction is undetectable_90_10`() {
        assertThat(KotlinPrimitives::_5_sum_elvis_always_null, !probablyDifferentTo(KotlinPrimitives::_7_sum_elvis_90_10_nullable))
    }

}