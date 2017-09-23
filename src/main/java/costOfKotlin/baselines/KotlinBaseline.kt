package costOfKotlin.baselines

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.strings.StringState
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

open class KotlinBaseline {

    @Benchmark
    fun baseline(state: StringState, blackhole: Blackhole) {
        blackhole.consume(state)
    }

    @Test
    fun `java is quicker but not by much`() {
        assertThat(JavaBaseline::baseline, probablyDifferentTo(this::baseline, 0.05))
        assertThat(JavaBaseline::baseline, ! probablyFasterThan(this::baseline, byAFactorOf = 0.05))
    }

}
