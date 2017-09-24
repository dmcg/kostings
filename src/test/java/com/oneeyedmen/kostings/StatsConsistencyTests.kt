package com.oneeyedmen.kostings

import org.junit.Assert.assertEquals
import org.junit.Test


class StatsConsistencyTests {

    @Test
    fun `check JMH reported stats`() {
        readResults(resultsDir).forEach {
            assertEquals("For ${it.benchmarkName}".printed(), it._score, it.score, it.score * 0.001)
            assertEquals("For ${it.benchmarkName}".printed(), it._samplesCount, it.samplesCount)
        }
    }
}