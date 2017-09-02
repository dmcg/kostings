package com.oneeyedmen.kostings.noop.strings

import org.openjdk.jmh.annotations.Benchmark

open class KotlinStrings {

    @Benchmark
    fun concat() {
        val s1 = "hello"
        val s2 = "world"
        val s3 = "${s1} ${s2}"
    }
}

/*
 Kotlin has an extra StringBuilder.append with a blank string
 */
