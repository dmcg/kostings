package com.oneeyedmen.kostings

import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.kostings.matchers.OffsetStatistics
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.junit.Test

class TestOffsetStats {

    @Test
    fun allStats() {
        val offset = 1.5
        val baseStats = DescriptiveStatistics(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0))
        val testStats = OffsetStatistics(baseStats, offset)

        com.natpryce.hamkrest.assertion.assert.that(testStats.min , equalTo(baseStats.min + offset))
        com.natpryce.hamkrest.assertion.assert.that(testStats.max , equalTo(baseStats.max + offset))
        com.natpryce.hamkrest.assertion.assert.that(testStats.mean , equalTo(baseStats.mean + offset))

        com.natpryce.hamkrest.assertion.assert.that(testStats.n , equalTo(baseStats.n))
        com.natpryce.hamkrest.assertion.assert.that(testStats.variance , equalTo(baseStats.variance))
        com.natpryce.hamkrest.assertion.assert.that(testStats.standardDeviation , equalTo(baseStats.standardDeviation))

        com.natpryce.hamkrest.assertion.assert.that(testStats.sum , equalTo(baseStats.sum + baseStats.n*offset))
    }
}