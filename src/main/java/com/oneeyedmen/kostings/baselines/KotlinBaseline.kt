package com.oneeyedmen.kostings.baselines

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.fasterByLessThan
import com.oneeyedmen.kostings.probablyFasterThan
import com.oneeyedmen.kostings.strings.StringState
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
        assertThat(JavaBaseline::baseline, probablyFasterThan(this::baseline))
        assertThat(JavaBaseline::baseline, fasterByLessThan(this::baseline, 0.05))
    }

}
