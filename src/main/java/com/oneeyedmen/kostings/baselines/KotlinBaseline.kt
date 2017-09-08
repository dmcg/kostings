package com.oneeyedmen.kostings.baselines

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.couldBeSlowerThan
import com.oneeyedmen.kostings.meanIsFasterThan
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
    fun `kotlin is quicker but not convincingly`() {
        assertThat(this::baseline, meanIsFasterThan(JavaBaseline::baseline))
        assertThat(this::baseline, couldBeSlowerThan(JavaBaseline::baseline))
    }

}
