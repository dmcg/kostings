package com.oneeyedmen.kostings.noop

import com.oneeyedmen.kostings.Result
import com.oneeyedmen.kostings.check
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark

open class KotlinNoop {

    @Benchmark
    fun noop() {
    }

    @Benchmark
    fun just_return() = 42

    @Test
    fun `noop is quicker`() {
        check(this::noop, Result::couldBeLessThan, this::just_return)
    }

}
