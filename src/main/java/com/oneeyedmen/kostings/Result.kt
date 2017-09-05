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

    fun couldBeGreaterThan(other: Result) = this.upperBound > other.lowerBound
    fun couldBeLessThan(other: Result) = this.lowerBound < other.upperBound

    val lowerBound get() = this.score - (this.error ?: BigDecimal.ZERO)
    val upperBound get() = this.score + (this.error ?: BigDecimal.ZERO)
}