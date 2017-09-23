package com.oneeyedmen.kostings

import java.math.BigDecimal

class Result(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Int,
    val score: Double,
    val error: Double,
    val units: String,
    val samples: DoubleArray? = null
) {
    fun asPerformanceData() = object: PerformanceData {
        override val description = this@Result.toString()
        override val samples = this@Result.samples!!
    }

    fun meanIsFasterThan(other: Result) = this.score > other.score
    fun possiblySlowerThan(other: Result) = this.lowerBound < other.upperBound
    fun fasterByLessThan(other: Result, proportion: Double) = (this.score - other.score) < this.score * proportion

    private val lowerBound get() = this.score - this.error
    private val upperBound get() = this.score + this.error

    override fun toString() = EssentialData(this).toString().replaceFirst("EssentialData", "Result")

    fun summary() = "$benchmarkName ${lowerBound.roundedTo(5)} < ${score.roundedTo(5)} < ${upperBound.roundedTo(5)}"
}

private fun Double.roundedTo(sigfigs: Int) = BigDecimal(this).round(java.math.MathContext(sigfigs, java.math.RoundingMode.HALF_EVEN)).toDouble()

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