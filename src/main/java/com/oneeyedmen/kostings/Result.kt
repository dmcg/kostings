package com.oneeyedmen.kostings

class Result(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Int,
    val score: Double,
    val error: Double,
    val units: String,
    val samples: DoubleArray? = null
) {
    fun asPerformanceData() = performanceData(benchmarkName, samples!!)

    override fun toString() = EssentialData(this).toString().replaceFirst("EssentialData", "Result")
}

private data class EssentialData(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Int,
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

private fun performanceData(description: String, samples: DoubleArray): PerformanceData {
    return object : PerformanceData {
        override val description = description
        override val samples = samples
    }
}