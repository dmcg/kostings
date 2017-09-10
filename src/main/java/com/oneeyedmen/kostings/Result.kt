package com.oneeyedmen.kostings

import java.math.BigDecimal

data class Result(
    val benchmarkName: String,
    val mode: String,
    val samples: Int,
    val score: BigDecimal,
    val error: BigDecimal?,
    val units: String
) {

    fun meanIsFasterThan(other: Result) = this.score > other.score
    fun probablyFasterThan(other: Result) = this.lowerBound > other.upperBound
    fun probablySlowerThan(other: Result) = this.upperBound < other.lowerBound
    fun possiblyFasterThan(other: Result) = this.upperBound > other.lowerBound
    fun possiblySlowerThan(other: Result) = this.lowerBound < other.upperBound
    fun fasterByLessThan(other: Result, proportion: Double) = (this.score - other.score) < this.score * BigDecimal(proportion)

    val lowerBound get() = this.score - (this.error ?: BigDecimal.ZERO)
    val upperBound get() = this.score + (this.error ?: BigDecimal.ZERO)
}