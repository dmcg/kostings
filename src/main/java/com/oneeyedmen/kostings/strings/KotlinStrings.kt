package com.oneeyedmen.kostings.strings

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

open class KotlinStrings {

    @Benchmark
    fun baseline(state: StringState, blackhole: Blackhole) {
        blackhole.consume(state.hello)
        blackhole.consume(state.world)
    }

    @Benchmark
    fun concat(state: StringState, blackhole: Blackhole) {
        /* There is an extra append("") compared to the Java
         */
        blackhole.consume("${state.hello} ${state.world}")
    }

    // The compiler optimizes this to a constant
    fun inline_concat() = "${"hello"} ${"world"}"

    // And even this
    fun inline_concat_2() = "${"${"hello" + " " + "world"}"}"

}

/*
 Kotlin has an extra StringBuilder.append with a blank string
 */
