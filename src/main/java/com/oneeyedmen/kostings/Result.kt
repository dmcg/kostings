package com.oneeyedmen.kostings

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.StatisticalSummary


interface Result {
    val benchmarkName: String
    val mode: String
    val units: String
    val stats: DescriptiveStatistics

    val performanceData get() = performanceData(benchmarkName, stats)

    val score: Double get() = stats.mean

    val samplesCount get() = stats.n.toInt()

    val error_999 get() = stats.meanError(0.999)// the confidence reported by JMH

    fun getSample(i: Int) = stats.getElement(i)

    fun _toString() = EssentialData(this).toString().replaceFirst("EssentialData", "Result")
}

data class EssentialData(
    private val benchmarkName: String,
    private val mode: String,
    private val samplesCount: Int,
    private val score: Double,
    private val error_999: Double,
    private val units: String
) {
    constructor(result: Result) : this(
        benchmarkName = result.benchmarkName,
        mode = result.mode,
        samplesCount = result.samplesCount,
        score = result.score,
        error_999 = result.error_999,
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

