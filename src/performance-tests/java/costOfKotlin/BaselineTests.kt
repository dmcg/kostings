package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import com.oneeyedmen.kostings.probablySlowerThan
import costOfKotlin.baselines.JavaBaseline
import costOfKotlin.baselines.KotlinBaseline
import org.junit.Test

class BaselineTests {

    @Test
    fun `java is quicker but not by much`() {
        assertThat(JavaBaseline::baseline, probablyFasterThan(KotlinBaseline::baseline, byAFactorOf = 0.01))
        assertThat(KotlinBaseline::baseline, probablySlowerThan(JavaBaseline::baseline, byAFactorOf = 0.03))
    }

}
