package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

fun Iterable<Result>.mergeResults(): Result =
    Result(
        benchmarkName = this.allTheSame(Result::benchmarkName),
        mode = this.allTheSame(Result::mode),
        units = this.allTheSame(Result::units),
        stats = this.map { it.stats }.concatStats()
    )

private fun Iterable<DescriptiveStatistics>.concatStats(): DescriptiveStatistics {
    return this.fold(DescriptiveStatistics()) { collector, each ->
        collector.addValuesFrom(each)
        collector
    }
}

private fun DescriptiveStatistics.addValuesFrom(other: DescriptiveStatistics) {
    for (i in 0 until other.n.toInt()) {
        this.addValue(other.getElement(i))
    }
}


private fun <T> Iterable<Result>.allTheSame(property: (Result) -> T): T {
    // fold is nastier
    val firstValue = property(first())
    if (find { property(it) != firstValue } != null)
        throw IllegalArgumentException()
    else
        return firstValue
}