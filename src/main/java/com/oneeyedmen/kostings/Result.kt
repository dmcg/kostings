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

    fun meanIsFasterThan(other: Result) = this.score > other.score
    fun probablyFasterThan(other: Result) = this.lowerBound > other.upperBound
    fun probablySlowerThan(other: Result) = this.upperBound < other.lowerBound
    fun possiblyFasterThan(other: Result) = this.upperBound > other.lowerBound
    fun possiblySlowerThan(other: Result) = this.lowerBound < other.upperBound
    fun fasterByLessThan(other: Result, proportion: Double) = (this.score - other.score) < this.score * proportion

    val lowerBound get() = this.score - this.error
    val upperBound get() = this.score + this.error

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