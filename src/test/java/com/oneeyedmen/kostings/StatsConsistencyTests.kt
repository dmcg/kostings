package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import org.junit.Assert.assertEquals
import org.junit.Test


class StatsConsistencyTests {

    @Test
    fun `check JMH reported stats`() {
        readResults(resultsDir).forEach {
            assertEquals("For ${it.benchmarkName}", it.score, it.samples.mean(), it.score * 0.001)
        }
    }
}

private fun DoubleArray.mean(): Double {
    val stats = SummaryStatistics()
    this.forEach {
        stats.addValue(it)
    }
    return stats.mean
}
