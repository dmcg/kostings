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
    fun couldBeFasterThan(other: Result) = this.upperBound > other.lowerBound
    fun couldBeSlowerThan(other: Result) = this.lowerBound < other.upperBound

    val lowerBound get() = this.score - (this.error ?: BigDecimal.ZERO)
    val upperBound get() = this.score + (this.error ?: BigDecimal.ZERO)
}