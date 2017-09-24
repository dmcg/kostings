package com.oneeyedmen.kostings

import org.junit.Assert.assertEquals
import org.junit.Test

class StatsConsistencyTests {

    @Test
    fun `check JMH reported stats`() {
        readResults(resultsDir).forEach {
            assertEquals("For ${it.benchmarkName}".printed(), it.error, it._error, it._error * 0.000001)
        }
    }
}