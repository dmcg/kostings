package com.oneeyedmen.kostings.matchers

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.oneeyedmen.kostings.PerformanceData
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.inference.TestUtils.tTest


/**
 * Is it likely that the test [PerformanceData] shows a significantly different mean metric than the benchmark [PerformanceData] ? (either higher or lower)
 *
 * Use Student's T testing to determine if there is a statistically significant difference between the test and benchmark samples in the [PerformanceData]s
 *
 * @param[benchmarkData] the benchmark [PerformanceData]
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyDifferentTo(benchmarkData: PerformanceData, alpha: Double = 0.05): Matcher<PerformanceData> =
        object : Matcher<PerformanceData> {
            override val description get() = "was statistically significantly different to ${benchmarkData.description}"

            override fun invoke(actual: PerformanceData): MatchResult {
                val testStats = DescriptiveStatistics(actual.samples)
                val benchmarkStats = DescriptiveStatistics(benchmarkData.samples)

                //the null-Hypothesis is that test == benchmark
                val rejectNullHypothesis = tTest(benchmarkStats,testStats,alpha)

                return if (rejectNullHypothesis) {
                    //so we can say test mean != benchmark mean, with a probability of alpha% that we are wrong (i.e. (1-alpha)% confidence)
                    MatchResult.Match
                } else {
                    //so we can't say it's different
                    MatchResult.Mismatch("the expectation cannot be met because the probability of being wrong is more than ${alpha}")
                }
            }
        }


/**
 * Is it likely that the test [PerformanceData] shows a significantly smaller metric than the benchmark [PerformanceData] ?
 *
 * Use Student's T testing to determine if there is a statistically significant negative difference between the test and benchmark samples in the [PerformanceData]s
 *
 * @param[benchmarkData] the benchmark [PerformanceData]
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 * @param[byAFactorOf] the expected factor by which the test mean is smaller than the benchmark mean, as a [Double] percent of the benchmark mean
 *      (e.g. 0.10 would be 10%, implying the test mean is expected to be >10% smaller than the benchmark mean, or >90% of the benchmark mean)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyLessThan(benchmarkData: PerformanceData, alpha: Double = 0.05, byAFactorOf: Double = 0.0): Matcher<PerformanceData> =
        object : Matcher<PerformanceData> {
            override val description get() = "was statistically significantly less than ${benchmarkData.description}" + if (byAFactorOf>0.0) " by a factor of ${byAFactorOf}" else ""

            override fun invoke(actual: PerformanceData): MatchResult {
                val factorAmount = benchmarkData.samples.average()*byAFactorOf
                val offsetTestData = actual.samples.map { x->x+factorAmount }.toDoubleArray()
                //is the test mean actually larger?
                if (offsetTestData.average() >= benchmarkData.samples.average())
                    //yup - so no chance this is probable
                    return MatchResult.Mismatch((if (byAFactorOf>0.0) "the offset mean was larger: offset" else "the mean was larger:") + " test mean[${offsetTestData.average()}] > benchmark mean[${benchmarkData.samples.average()}]")
                else {
                    //t-Test the null-Hypothesis is that test mean-byAFactorOf*benchmark mean > benchmark mean
                    val rejectNullHypothesis = tTest(benchmarkData.samples,offsetTestData, alpha * 2) //one-sided test

                    return if (rejectNullHypothesis) {
                        //so we can say that test mean-byAFactorOf*benchmark mean < benchmark mean, with a probability of alpha% that we are wrong (i.e. (1-alpha)% confidence)
                        MatchResult.Match
                    } else
                        //so we can't say that it is smaller
                        MatchResult.Mismatch("the expectation cannot be met because the probability of being wrong is more than ${alpha}")
                }
            }
        }


/**
 * Is it likely that the test [PerformanceData] shows a significantly greater metric than the benchmark [PerformanceData] ?
 *
 * Use Student's T testing to determine if there is a statistically significant positive difference between the test and benchmark samples in the [PerformanceData]s
 *
 * @param[benchmarkData] the benchmark [PerformanceData]
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 * @param[byAFactorOf] the expected factor by which the test mean is greater than the benchmark mean, as a [Double] percent of the benchmark mean
 *      (e.g. 0.10 would be 10%, implying the test mean is expected to be >10% larger than the benchmark mean, or >110% of the benchmark mean)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyMoreThan(benchmarkData: PerformanceData, alpha: Double = 0.05, byAFactorOf: Double = 0.0): Matcher<PerformanceData> =
        object : Matcher<PerformanceData> {
            override val description get() = "was statistically significantly more than ${benchmarkData.description}" + if (byAFactorOf>0.0) " by a factor of ${byAFactorOf}" else ""

            override fun invoke(actual: PerformanceData): MatchResult {
                val factorAmount = benchmarkData.samples.average()*byAFactorOf
                val offsetTestData = actual.samples.map { x->x-factorAmount }.toDoubleArray()
                //is the test mean actually smaller?
                if (offsetTestData.average() <= benchmarkData.samples.average())
                    //yup - so no chance this is probable
                    return MatchResult.Mismatch((if (byAFactorOf>0.0) "the offset mean was smaller: offset" else "the mean was smaller:") + " test mean[${offsetTestData.average()}] < benchmark mean[${benchmarkData.samples.average()}]")
                else {
                    //t-Test the null-Hypothesis is that test mean-byAFactorOf*benchmark mean < benchmark mean
                    val rejectNullHypothesis = tTest(benchmarkData.samples,offsetTestData, alpha * 2) //one-sided test

                    return if (rejectNullHypothesis) {
                        //so we can say that test mean-byAFactorOf*benchmark mean > benchmark mean, with a probability of alpha% that we are wrong (i.e. (1-alpha)% confidence)
                        MatchResult.Match
                    } else
                        //so we can't say that it is bigger
                        MatchResult.Mismatch("the expectation cannot be met because the probability of being wrong is more than ${alpha}")
                }
            }
        }

