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

    val _error = stats.meanError(0.999)

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

// copied from JMH AbstractStatistics
fun StatisticalSummary.meanError(confidence: Double): Double {
    if (n <= 2) return java.lang.Double.NaN
    val tDist = TDistribution((n - 1).toDouble())
    val a = tDist.inverseCumulativeProbability(1 - (1 - confidence) / 2)
    return a * standardDeviation / Math.sqrt(n.toDouble())
}
