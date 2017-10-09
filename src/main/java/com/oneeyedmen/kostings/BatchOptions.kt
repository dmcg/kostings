package com.oneeyedmen.kostings

import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder

data class BatchOptions(val pattern: String, val forks: Int, val warmups: Int, val measurements: Int, val discriminator: String? = null) {

    companion object {
        fun fromFilename(filename: String): BatchOptions? {
            val match: MatchResult = filenameRegex.matchEntire(filename) ?: return null

            return BatchOptions(
                pattern = match.value(1),
                forks = match.value(2).toInt(),
                warmups = match.value(3).toInt(),
                measurements = match.value(4).toInt(),
                discriminator = match.value(5))
        }

        private val filenameRegex = """^(.*?)-f(\d+)-w(\d+)-m(\d+)-(.*)\.json""".toRegex()

    }

    fun applyOptions(options: Options) = this.copy(
        pattern = if (options.includes.isNotEmpty()) options.includes.first() else this.pattern,
        forks = options.forkCount.orElse(this.forks),
        warmups = options.warmupIterations.orElse(this.warmups),
        measurements = options.measurementIterations.orElse(this.measurements),
        discriminator = options.output.orElse(this.discriminator) // nasty reuse, but I can't add parameters
    )

    fun toOptions() = OptionsBuilder().include(pattern).forks(forks).warmupIterations(warmups).measurementIterations(measurements)!!

    val outputFilename: String get() = "$pattern-f$forks-w$warmups-m$measurements$discriminatorSuffix"

    private val discriminatorSuffix: String get() = discriminator?.let{ "-$it" } ?: ""

}

private fun MatchResult.value(index: Int) = groups[index]!!.value
