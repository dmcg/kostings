package com.oneeyedmen.kostings.baselines

import com.oneeyedmen.kostings.Result
import com.oneeyedmen.kostings.check
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
    fun `kotlin is quicker`() {
        check(this::baseline, Result::isFasterThan, JavaBaseline::baseline)
    }

}
