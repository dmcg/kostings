package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.StatisticalSummary

class Result(
    val benchmarkName: String,
    val mode: String,
    val error: Double,
    val units: String,
    val samples: DoubleArray
) {

    val performanceData by lazy {
        performanceData(benchmarkName, DescriptiveStatistics(samples))
    }

    val score: Double get() = performanceData.stats.mean

    val samplesCount get() = performanceData.stats.n

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