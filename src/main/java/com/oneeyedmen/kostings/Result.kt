package com.oneeyedmen.kostings

import com.oneeyedmen.kostings.matchers.Stats
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.StatisticalSummary


interface Result : Stats {
    val benchmarkName: String
    val mode: String
    val units: String

    override val data: DescriptiveStatistics
    override val description get() = benchmarkName

    val score: Double get() = data.mean

    val samplesCount get() = data.n.toInt()

    val error_999 get() = data.meanError(0.999)// the confidence reported by JMH

    fun getSample(i: Int) = data.getElement(i)

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

private fun performanceData(description: String, stats: StatisticalSummary): Stats {
    return object : Stats {
        override val description = description
        override val data = stats
    }
}

// copied from JMH AbstractStatistics
fun StatisticalSummary.meanError(confidence: Double): Double {
    if (n <= 2) return java.lang.Double.NaN
    val tDist = TDistribution((n - 1).toDouble())
    val a = tDist.inverseCumulativeProbability(1 - (1 - confidence) / 2)
    return a * standardDeviation / Math.sqrt(n.toDouble())
}

