package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.inference.TestUtils.oneWayAnovaTest

object DataChecking{

    @JvmStatic
    fun main(args: Array<String>) {
        readBatches(canonicalResultsDir).forEach { batch ->
            reportIffness(batch)
        }
        readBatches(resultsDir).forEach { batch ->
            reportIffness(batch)
        }
    }

    private fun reportIffness(batch: Batch) {
        val iffyness: Map<Result, String> = batch.results.associate { it to isIffy(it.stats.values) }
        println("${batch.dataFile} : ${iffyness.summary()}")
        iffyness.forEach { result, iffy ->
            println("    ${result.benchmarkName} : ${iffy}")
        }
        println()
    }

}

private fun Map<Result, String>.summary(): String = this.values.find { it != "ok" } ?: "ok"

private fun isIffy(values: DoubleArray): String =
    when {
        values.size < 50 -> "too small"
        else -> if (hasAnomalousData(values)) "iffy" else "ok"
    }

fun hasAnomalousData(data: DoubleArray, alpha: Double = 0.01, chunkSize: Int = 50): Boolean {
    // can you reject the hypothesis that the chunks are the same w.r.t mean, and only have an alpha% chance of being wrong?
    return oneWayAnovaTest(data.inBatchesOf(chunkSize), alpha)
}


