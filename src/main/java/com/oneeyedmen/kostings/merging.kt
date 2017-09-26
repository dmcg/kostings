package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.inference.TestUtils

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

fun hasAnomalousData(data: DoubleArray, alpha: Double = 0.01, chunkSize: Int  = 50): Boolean {
        val chunkedData = data.asList().batch(chunkSize).map { x -> x.toDoubleArray() }
        //can you reject the hypothesis that the chunks are the same w.r.t mean, and only have an alpha% chance of being wrong?
        return TestUtils.oneWayAnovaTest(chunkedData, alpha)
}


fun rejectAnomalousData(data: DoubleArray, alpha: Double = 0.01, chunkSize: Int  = 50): DoubleArray {
    //val chunkedData = data.asList().batch(chunkSize).map { x -> x.toDoubleArray() }
    return data
}


private fun <T> Iterable<T>.batch(chunkSize: Int) =
        withIndex().                        // create index value pairs
                groupBy { it.index / chunkSize }.   // create grouping index
                map { it.value.map { it.value } }   // split into different partitions