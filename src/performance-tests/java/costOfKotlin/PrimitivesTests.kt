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
    fun `kotlin sum is marginally detectable`() {
        assertThat(KotlinPrimitives::_1_baseline, probablyDifferentTo(KotlinPrimitives::_2_sum))
        assertThat(KotlinPrimitives::_1_baseline, !probablyFasterThan(KotlinPrimitives::_2_sum))
    }

    @Test
    fun `sum nullable is slower than sum`() {
        assertThat(KotlinPrimitives::_2_sum, probablyFasterThan(KotlinPrimitives::_3_sum_nullable_bang_bang, byMoreThan = 0.01, butNotMoreThan = 0.05))
    }

    @Test
    fun `sum elvis never null is slower than sum`() {
        assertThat(KotlinPrimitives::_2_sum, probablyFasterThan(KotlinPrimitives::_4_sum_elvis_never_null, byMoreThan = 0.01, butNotMoreThan = 0.05))
    }

    @Test
    fun `sum elvis always null is slower than sum but faster than never null`() {
        assertThat(KotlinPrimitives::_2_sum, probablyFasterThan(KotlinPrimitives::_5_sum_elvis_always_null, byMoreThan = 0.01, butNotMoreThan = 0.05))
        assertThat(KotlinPrimitives::_5_sum_elvis_always_null, probablyFasterThan(KotlinPrimitives::_4_sum_elvis_never_null, byMoreThan = 0.01, butNotMoreThan = 0.05))
    }

    @Test
    fun `random null is worst`() {
        assertThat(KotlinPrimitives::_4_sum_elvis_never_null, probablyFasterThan(KotlinPrimitives::_6_sum_elvis_50_50_nullable))
//        assertThat(KotlinPrimitives::_4_sum_elvis_never_null, probablyFasterThan(KotlinPrimitives::_7_sum_elvis_90_10_nullable))
    }

}