package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.inference.TestUtils



/**
 * Merges results into a single result with the data from all.
 *
 * Checks that results are for the same benchmark.
 *
 * TODO DMCG 2017-10 - check the individual results are statistically compatible before merging.
 */
class CompositeResult(results: Iterable<Result>) : Result {

    override val benchmarkName: String = results.allTheSame(Result::benchmarkName)
    override val mode: String = results.allTheSame(Result::mode)
    override val units: String = results.allTheSame(Result::units)
    override val stats: DescriptiveStatistics = results.map { it.stats }.concatStats()

    override fun toString() = _toString()
}

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


class RejectingTooManyValues(msg: String? = null) : IllegalStateException(msg)

fun rejectAnomalousData(data: DoubleArray, alpha: Double = 0.01, chunkSize: Int  = 50, maximumRejectionFactor: Double = 0.10): DoubleArray {
    /*
    The method here is based on the principle that there is a mean and if the benchmarking process gets interrupted
    this will cause a notch in the data (positive or negative depending on the metric) which can be rejected
     */

    //first let's find the "mode" mean by using ANOVA testing of chunks
    val meanBuckets = arrayListOf<MutableList<DoubleArray>>()
    for (chunk in data.inBatchesOf(chunkSize)) {
        var foundMeanBucket = false
        for (bucket in meanBuckets) {
            val testChunks = bucket + listOf(chunk)
            if (!TestUtils.oneWayAnovaTest(testChunks, alpha)) {
                bucket.add(chunk)
                foundMeanBucket = true
            }
        }
        if (!foundMeanBucket) meanBuckets.add(mutableListOf(chunk))
    }

    //get the mode values
    meanBuckets.sortByDescending { it.size }
    val modeValues = meanBuckets[0].fold(doubleArrayOf(), { a,b -> a+b })

    //check to see if we haven't thrown away too many values
    val rejectionFactor = 1.0 - (modeValues.size / data.size.toDouble())
    if (rejectionFactor > maximumRejectionFactor) throw RejectingTooManyValues("Trying to reject ${rejectionFactor*100}% of the values which is greater than the maximum acceptable amount of ${maximumRejectionFactor*100}%")

    //ok
    return modeValues
}


