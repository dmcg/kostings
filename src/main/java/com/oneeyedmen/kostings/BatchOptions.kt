package com.oneeyedmen.kostings

import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder

data class BatchOptions(val pattern: String, val forks: Int, val warmups: Int, val measurements: Int, val discriminator: String? = null) {

    fun applyOptions(options: Options) = this.copy(
        pattern = if (options.includes.isNotEmpty()) options.includes.first() else this.pattern,
        forks = options.forkCount.get() ?: this.forks,
        warmups = options.warmupIterations.get() ?: this.warmups,
        measurements = options.warmupIterations.get() ?: this.measurements,
        discriminator = options.output.get() ?: this.discriminator // nasty reuse, but I can't add parameters
    )

    fun toOptions() = OptionsBuilder().include(pattern).forks(forks).warmupIterations(warmups).measurementIterations(measurements)!!

    val outputFilename: String get() = "$pattern-f$forks-w$warmups-m$measurements$discriminatorSuffix"

    private val discriminatorSuffix: String get() = discriminator?.let{ "-$it" } ?: ""
}
