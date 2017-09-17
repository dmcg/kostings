package com.oneeyedmen.kostings

import java.math.BigDecimal

data class Result(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Int,
    val score: BigDecimal,
    val error: BigDecimal?,
    val units: String,
    val samples: List<BigDecimal>? = null
) {

    fun meanIsFasterThan(other: Result) = this.score > other.score
    fun probablyFasterThan(other: Result) = this.lowerBound > other.upperBound
    fun probablySlowerThan(other: Result) = this.upperBound < other.lowerBound
    fun possiblyFasterThan(other: Result) = this.upperBound > other.lowerBound
    fun possiblySlowerThan(other: Result) = this.lowerBound < other.upperBound
    fun fasterByLessThan(other: Result, proportion: Double) = (this.score - other.score) < this.score * BigDecimal(proportion)

    val lowerBound get() = this.score - (this.error ?: BigDecimal.ZERO)
    val upperBound get() = this.score + (this.error ?: BigDecimal.ZERO)

    override fun toString() = EssentialData(this).toString().replaceFirst("EssentialData", "Result")

    fun summary() = "$benchmarkName ${lowerBound.roundedTo(5)} < ${score.roundedTo(5)} < ${upperBound.roundedTo(5)}"
}

private fun BigDecimal.roundedTo(sigfigs: Int) = this.round(java.math.MathContext(sigfigs, java.math.RoundingMode.HALF_EVEN))

private data class EssentialData(
    val benchmarkName: String,
    val mode: String,
    val samplesCount: Int,
    val score: BigDecimal,
    val error: BigDecimal?,
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