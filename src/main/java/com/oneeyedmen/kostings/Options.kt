package com.oneeyedmen.kostings

import org.openjdk.jmh.runner.options.OptionsBuilder

data class Options(val pattern: String, val forks: Int, val warmups: Int, val measurements: Int, val discriminator: String? = null) {

    fun toOptions() = OptionsBuilder().include(pattern).forks(forks).warmupIterations(warmups).measurementIterations(measurements)!!

    val outputFilename: String get() = "$pattern-f$forks-w$warmups-m$measurements$discriminatorSuffix"

    private val discriminatorSuffix: String get() = discriminator?.let{ "-$it" } ?: ""
}
