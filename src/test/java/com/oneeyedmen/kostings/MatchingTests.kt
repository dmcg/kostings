package com.oneeyedmen.kostings

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.matchers.probablyDifferentTo
import com.oneeyedmen.kostings.matchers.probablyLessThan
import com.oneeyedmen.kostings.matchers.probablyMoreThan
import org.junit.Test
import java.util.*

private class TestPerformanceData(override val samples: DoubleArray, override val description : String) : PerformanceData

class MatchingTests {
    private val rng = Random()

    @Test(expected = AssertionError::class)
    fun exactlyTheSameDistributionFailsProbablyDifferent() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0))
        assertThat("should fail as exactly the same",benchmarkData, probablyDifferentTo(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun samplesOfSameDataButDifferentSizesFailsProbablyDifferent() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(50) { rng.nextGaussian() })
        val resultData = TestPerformanceData(description = "result data", samples = benchmarkData.samples.copyOfRange(0, 30)) //subset of test data

        assertThat("should fail",resultData, probablyDifferentTo(benchmarkData))
    }


    @Test
    fun postiveOffsetShouldBeProbablyDifferent() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(30) { rng.nextGaussian() + 10 })
        val resultData = TestPerformanceData(description = "result data", samples = DoubleArray(50) { rng.nextGaussian() })

        assertThat("should not be similar",resultData, probablyDifferentTo(benchmarkData))
    }

    @Test
    fun negativeOffsetShouldBeProbablyDifferent() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(30) { rng.nextGaussian() - 10 })
        val resultData = TestPerformanceData(description = "result data", samples = DoubleArray(50) { rng.nextGaussian() })

        assertThat("should not be similar",resultData, probablyDifferentTo(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun differentAlphaEffectsSignificance() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(42.1, 41.3, 42.4, 43.2, 41.8, 41.0, 41.8, 42.8, 42.3, 42.7))
        val resultData = TestPerformanceData(description = "result data", samples = doubleArrayOf(42.7, 43.8, 42.5, 43.1, 44.0, 43.6, 43.3, 43.5, 41.7, 44.1))

        assertThat("should be similar at this level of alpha",resultData, probablyDifferentTo(benchmarkData,alpha = 0.05))
        assertThat("should not be similar at this level of alpha",resultData, probablyDifferentTo(benchmarkData,alpha = 0.001))
    }

    @Test
    fun shouldBeProbablyLessThan() {
        //data chosen to ensure one-sided test works using correct alpha
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0))
        val resultData = TestPerformanceData(description = "result data", samples = benchmarkData.samples.map { x -> x - 2.5 }.toDoubleArray())

        assertThat("should be less than",resultData, probablyLessThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun shouldNotBeProbablyLessThan() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(30) { rng.nextGaussian() })
        val resultData = TestPerformanceData(description = "result data", samples = DoubleArray(50) { rng.nextGaussian() + 5 })

        assertThat("should not be less than",resultData, probablyLessThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun sameDataShouldNotBeProbablyLessThan() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(50) { rng.nextGaussian() })

        assertThat("should fail",benchmarkData, probablyLessThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun shouldBeProbablyLessThanByAFactor() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(1.0, 1.0, 2.0, 3.0, 3.0))
        val resultData = TestPerformanceData(description = "result data", samples = benchmarkData.samples.map { x -> x - 2.0 }.toDoubleArray())

        assertThat("although the result is statistically smaller, it is NOT smaller by a factor of 50%",resultData, probablyLessThan(benchmarkData, byAFactorOf = 0.5)) //0.5 == 50% smaller == 50% of benchmark mean
    }

    @Test
    fun shouldBeProbablyMoreThan() {
        //data chosen to ensure one-sided test works using correct alpha
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0))
        val resultData = TestPerformanceData(description = "result data", samples = benchmarkData.samples.map { x -> x + 2.5 }.toDoubleArray())

        assertThat("should be greater than",resultData, probablyMoreThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun shouldNotBeProbablyMoreThan() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(30) { rng.nextGaussian() + 5 })
        val resultData = TestPerformanceData(description = "result data", samples = DoubleArray(50) { rng.nextGaussian() })

        assertThat("should not be greater than",resultData, probablyMoreThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun sameDataShouldNotBeProbablyMoreThan() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = DoubleArray(50) { rng.nextGaussian() })

        assertThat("should fail",benchmarkData, probablyMoreThan(benchmarkData))
    }

    @Test(expected = AssertionError::class)
    fun shouldBeProbablyMoreThanByAFactor() {
        val benchmarkData = TestPerformanceData(description = "benchmark data", samples = doubleArrayOf(1.0, 1.0, 2.0, 3.0, 3.0))
        val resultData = TestPerformanceData(description = "result data", samples = benchmarkData.samples.map { x -> x + 3.0 }.toDoubleArray())

        assertThat("although the result is statistically larger, it is NOT larger by a factor of 100%",resultData, probablyMoreThan(benchmarkData, byAFactorOf = 1.0)) //1.0 == 100% larger == 2x benchmark mean
    }
}