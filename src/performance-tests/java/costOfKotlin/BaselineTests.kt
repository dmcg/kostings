package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.baselines.JavaBaseline
import costOfKotlin.baselines.KotlinBaseline
import org.junit.Test

class BaselineTests {

    @Test
    fun `Java is quicker but not by much`() {
        assertThat(JavaBaseline::baseline, probablyFasterThan(KotlinBaseline::baseline, byMoreThan = 0.03, butNotMoreThan = 0.04))
    }
}