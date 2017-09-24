package com.oneeyedmen.kostings

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.StatisticalSummary



class Result(
    val benchmarkName: String,
    val mode: String,
    val error: Double,
    val units: String,
    val samples: DoubleArray
) {
    val stats by lazy { DescriptiveStatistics(samples) }

    val performanceData get() = performanceData(benchmarkName, stats)

    val score: Double get() = stats.mean

    val samplesCount get() = stats.n

    val _error = stats.meanCI(0.01)

    override fun toString() = EssentialData(this).toString().replaceFirst("EssentialData", "Result")

}

private data class EssentialData(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Long,
    val score: Double,
    val error: Double,
    val units: String
) {
    constructor(result: Result) : this(
        benchmarkName = result.benchmarkName,
        mode = result.mode,
        samplesCount = result.samplesCount,
        score = result.score,
        error = result.error,
        units = result.units
    )
}

private fun performanceData(description: String, stats: StatisticalSummary): PerformanceData {
    return object : PerformanceData {
        override val description = description
        override val stats = stats
    }
}

// see https://stackoverflow.com/questions/5564621/using-apache-commons-math-to-determine-confidence-intervals
private fun StatisticalSummary.meanCI(level: Double): Double {
        // Create T Distribution with N-1 degrees of freedom
    val tDist = TDistribution((n - 1).toDouble())
    // Calculate critical value
    val critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2)
    // Calculate confidence interval
    return critVal * standardDeviation / Math.sqrt(n.toDouble())

}